package no.ion.hwmonitor;

import no.ion.hwmonitor.liquidctl.KrakenStats;
import no.ion.hwmonitor.liquidctl.NzxtKraken;
import no.ion.hwmonitor.lmsensors.LmSensors;
import no.ion.hwmonitor.lmsensors.SensorStats;
import no.ion.hwmonitor.nvidia.NvidiaSmi;
import no.ion.hwmonitor.nvidia.NvidiaSmiStats;
import no.ion.hwmonitor.process.ProcessExecutor;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import static no.ion.hwmonitor.util.Exceptions.uncheck;
import static no.ion.hwmonitor.util.Exceptions.uncheckIO;

public class HardwareSensorLogger {
    private static final String separator = " ";
    private final Path logPath;
    private final FileSystem fileSystem;
    private final ProcessExecutor processExecutor;

    private boolean hasNvidiaGraphicsCard = true;
    private boolean hasNzxtKrakenXCooler = true;
    private boolean hasLmSensors = true;

    private final NvidiaSmi nvidiaSmi;
    private final NzxtKraken nzxtKraken;
    private final LmSensors lmSensors;

    public HardwareSensorLogger(Path logPath) {
        this.logPath = logPath;
        fileSystem = logPath.getFileSystem();
        processExecutor = new ProcessExecutor();
        nvidiaSmi = new NvidiaSmi(fileSystem, processExecutor);
        nzxtKraken = new NzxtKraken(fileSystem, processExecutor);
        lmSensors = new LmSensors(fileSystem, processExecutor);
    }

    public static void main(String... args) {
        if (args.length != 1 || args[0].equals("-h") || args[0].equals("--help") || args[0].equals("help")) {
            usage(null);
            return;
        }

        Path logPath = Paths.get(args[0]);
        if (!Files.isDirectory(logPath.getParent())) {
            usage("Parent directory of " + args[0] + " does not exist");
            return;
        }

        HardwareSensorLogger logger = new HardwareSensorLogger(logPath);
        logger.blockingStart();
    }

    private static void usage(String messageOrNull) {
        if (messageOrNull != null) {
            System.out.println(messageOrNull + "\n");
        }

        System.out.println("Takes one argument: the path to the hardware log file to append to");
    }

    public void blockingStart() {
        boolean dummy = false;

        for (logNow(); dummy; logNow()) {
            sleepFor(Duration.ofSeconds(1));
        }
    }

    private void sleepFor(Duration duration) {
        uncheck(() -> Thread.sleep(duration.toMillis()), RuntimeException::new);
    }

    private void logNow() {
        var string = new StringBuilder();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
        string.append(String.format("%1$tF %1$tT%1$tz", calendar));

        if (hasLmSensors) {
            SensorStats stats = lmSensors.probe();
            string.append(separator).append("cpu-temp=").append(stats.temperatureInput()).append('C');
            string.append(separator).append("cpu-temp2=").append(stats.temperature2Input()).append('C');
            string.append(separator).append("wifi-temp=").append(stats.temperatureWifi()).append('C');
        }

        if (hasNvidiaGraphicsCard) {
            NvidiaSmiStats stats = nvidiaSmi.probeNvidiaDevice();
            string.append(separator).append("gpu-temp=").append(stats.temperatureGpu()).append('C');
            string.append(separator).append("gfx-power=").append(stats.powerDraw()).append('W');
            string.append(separator).append("gpu-util=").append(stats.utilizationGpuInPercent()).append('%');
            string.append(separator).append("gfx-fan=").append(stats.fanSpeedInPercent()).append('%');
        }

        if (hasNzxtKrakenXCooler) {
            KrakenStats stats = nzxtKraken.probe();
            string.append(separator).append("kraken-temp=").append(stats.temperature()).append('C');
            string.append(separator).append("kraken-fan=").append(stats.fanSpeedRpm()).append("rpm");
            string.append(separator).append("kraken-pump=").append(stats.pumpSpeedRpm()).append("rpm");
        }

        string.append('\n');

        uncheckIO(() -> Files.write(logPath, string.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
