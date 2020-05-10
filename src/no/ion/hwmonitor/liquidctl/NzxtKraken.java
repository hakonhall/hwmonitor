package no.ion.hwmonitor.liquidctl;

import no.ion.hwmonitor.process.ProcessExecutor;
import no.ion.hwmonitor.process.ProcessResult;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NzxtKraken {
    private static final String LIQUIDCTL_PATH = "/usr/local/bin/liquidctl";

    private static final Pattern LIQUID_TEMPERATURE_PATTERN = Pattern.compile("Liquid temperature[ \\t]*([0-9.]+)[ \\t]");
    private static final Pattern FAN_SPEED_PATTERN = Pattern.compile("Fan speed[ \\t]*([0-9]+)[ \\t]+rpm");
    private static final Pattern PUMP_SPEED_PATTERN = Pattern.compile("Pump speed[ \\t]*([0-9]+)[ \\t]+rpm");

    private final FileSystem fileSystem;
    private final ProcessExecutor executor;

    public NzxtKraken(FileSystem fileSystem, ProcessExecutor executor) {
        this.fileSystem = fileSystem;
        this.executor = executor;
    }

    public boolean liquidctlIsInstalled() {
        return Files.isExecutable(fileSystem.getPath(LIQUIDCTL_PATH));
    }

    public KrakenStats probe() {
        if (!liquidctlIsInstalled()) {
            throw new IllegalStateException(LIQUIDCTL_PATH + " is not installed");
        }

        ProcessResult result = executor.exec(LIQUIDCTL_PATH, "status");
        if (result.exitCode() != 0) {
            throw new RuntimeException("liquidctl failed with exit code " + result.exitCode() + ": " + result.outputAsUtf8());
        }

        // output of the format:
        //   NZXT Kraken X (X42, X52, X62 or X72)
        //   ├── Liquid temperature     30.2  °C
        //   ├── Fan speed               460  rpm
        //   ├── Pump speed             1959  rpm
        //   └── Firmware version      4.0.2

        String output = result.outputAsUtf8();

        Matcher liquidMatcher = LIQUID_TEMPERATURE_PATTERN.matcher(output);
        if (!liquidMatcher.find()) {
            throw new IllegalArgumentException("Failed to find liquid temperature in output from " + LIQUIDCTL_PATH + ": " + output);
        }

        Matcher fanSpeedMatcher = FAN_SPEED_PATTERN.matcher(output);
        if (!fanSpeedMatcher.find()) {
            throw new IllegalArgumentException("Failed to find fan speed in output from " + LIQUIDCTL_PATH + ": " + output);
        }

        Matcher pumpSpeedMatcher = PUMP_SPEED_PATTERN.matcher(output);
        if (!pumpSpeedMatcher.find()) {
            throw new IllegalArgumentException("Failed to find pump speed in output from " + LIQUIDCTL_PATH + ": " + output);
        }

        return new KrakenStats(
                Float.parseFloat(liquidMatcher.group(1)),
                Integer.parseInt(fanSpeedMatcher.group(1)),
                Integer.parseInt(pumpSpeedMatcher.group(1)));
    }
}
