package no.ion.hwmonitor.process;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.ion.hwmonitor.util.Exceptions.uncheck;
import static no.ion.hwmonitor.util.Exceptions.uncheckIO;

public class ProcessExecutor {
    public ProcessResult exec(String program, String... arguments) {
        List<String> argumentVector = new ArrayList<>(1 + arguments.length);
        argumentVector.add(program);
        argumentVector.addAll(Arrays.asList(arguments));
        ProcessBuilder builder = new ProcessBuilder(argumentVector)
                .redirectErrorStream(true);
        Process process = uncheckIO(builder::start);
        InputStream inputStream = process.getInputStream();
        byte[] outputBytes = uncheckIO(inputStream::readAllBytes);
        int exitCode = uncheck(() -> process.waitFor());

        return new ProcessResult(exitCode, outputBytes);
    }

    /** Same as {@link #exec}, but demands exit code 0 and returns output. */
    public String invoke(Path program, String... arguments) {
        if (!Files.isExecutable(program)) {
            throw new IllegalStateException(program + " is not installed");
        }

        ProcessResult result = exec(program.toString(), arguments);
        if (result.exitCode() != 0) {
            throw new RuntimeException(program + " failed with exit code " + result.exitCode() + ": " + result.outputAsUtf8());
        }

        return result.outputAsUtf8();
    }
}
