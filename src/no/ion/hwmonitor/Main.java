package no.ion.hwmonitor;

import no.ion.hwmonitor.liquidctl.KrakenStats;
import no.ion.hwmonitor.liquidctl.NzxtKraken;
import no.ion.hwmonitor.lmsensors.LmSensors;
import no.ion.hwmonitor.lmsensors.SensorStats;
import no.ion.hwmonitor.nvidia.NvidiaSmi;
import no.ion.hwmonitor.nvidia.NvidiaSmiStats;
import no.ion.hwmonitor.process.ProcessExecutor;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public class Main {
    public static void main(String... args) {
        FileSystem fileSystem = FileSystems.getDefault();
        ProcessExecutor executor = new ProcessExecutor();
        NvidiaSmi nvidiaSmi = new NvidiaSmi(fileSystem, executor);
        NvidiaSmiStats nvidiaSmiStats = nvidiaSmi.probeNvidiaDevice();
        System.out.println(nvidiaSmiStats);

        NzxtKraken nzxtKraken = new NzxtKraken(fileSystem, executor);
        KrakenStats krakenStats = nzxtKraken.probe();
        System.out.println(krakenStats);

        LmSensors sensors = new LmSensors(fileSystem, executor);
        SensorStats sensorStats = sensors.probe();
        System.out.println(sensorStats);
    }
}
