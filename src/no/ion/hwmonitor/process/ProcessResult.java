package no.ion.hwmonitor.process;

import java.nio.charset.StandardCharsets;

public class ProcessResult {
    private final int exitCode;
    private final byte[] outputBytes;

    public ProcessResult(int exitCode, byte[] outputBytes) {
        this.exitCode = exitCode;
        this.outputBytes = outputBytes;
    }

    public int exitCode() { return exitCode; }

    public String outputAsUtf8() {
        return new String(outputBytes, StandardCharsets.UTF_8);
    }
}
