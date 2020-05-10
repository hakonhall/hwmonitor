package no.ion.hwmonitor.nvidia;

import no.ion.hwmonitor.process.ProcessExecutor;
import no.ion.hwmonitor.process.ProcessResult;

import java.nio.file.FileSystem;
import java.nio.file.Files;

public class NvidiaSmi {
    private static final String NVIDIA_SMI_PATH = "/usr/bin/nvidia-smi";

    private FileSystem fileSystem;
    private ProcessExecutor executor;

    public NvidiaSmi(FileSystem fileSystem, ProcessExecutor executor) {
        this.fileSystem = fileSystem;
        this.executor = executor;
    }

    public boolean usrBinNvidiaSmiIsInstalled() {
        return Files.isExecutable(fileSystem.getPath(NVIDIA_SMI_PATH));
    }

    public NvidiaSmiStats probeNvidiaDevice() {
        if (!usrBinNvidiaSmiIsInstalled()) {
            throw new IllegalStateException(NVIDIA_SMI_PATH + " is not installed");
        }

        ProcessResult result = executor.exec(
                NVIDIA_SMI_PATH,
                "--format=csv,noheader,nounits",
                "--query-gpu=count,fan.speed,memory.used,utilization.gpu,encoder.stats.averageFps,temperature.gpu,power.draw,power.limit");

        if (result.exitCode() != 0) {
            throw new RuntimeException("nvidia-smi failed with exit code " + result.exitCode() + ": " + result.outputAsUtf8());
        }

        String[] tokens = result.outputAsUtf8().split(", ", -1);
        // "1, 0, 1041, 20, 0, 46, 24.47, 190.00"
        final int expectedArguments = 8;
        if (tokens.length != expectedArguments) {
            throw new IllegalStateException("Expected " + expectedArguments + " but got " + tokens.length +
                    ": " + result.outputAsUtf8());
        }

        int deviceCount = parseInt(tokens[0]);
        switch (deviceCount) {
            case 0: throw new IllegalStateException("Failed to find any NVidia devices");
            case 1: break;
            default: throw new IllegalStateException("Found more than one NVidia device with nvidia-smi: not yet supported");
        }

        return new NvidiaSmiStats(
                parseInt(tokens[1]),
                parseInt(tokens[2]),
                parseInt(tokens[3]),
                parseInt(tokens[4]),
                parseInt(tokens[5]),
                parseFloat(tokens[6]),
                parseFloat(tokens[7]));
    }

    private int parseInt(String token) {
        return Integer.parseInt(token);
    }

    private float parseFloat(String token) {
        return Float.parseFloat(token);
    }
}
