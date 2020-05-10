package no.ion.hwmonitor.lmsensors;

import no.ion.hwmonitor.process.ProcessExecutor;

import java.nio.file.FileSystem;

public class LmSensors {
    private final FileSystem fileSystem;
    private final ProcessExecutor executor;

    public LmSensors(FileSystem fileSystem, ProcessExecutor executor) {
        this.fileSystem = fileSystem;
        this.executor = executor;
    }

    private enum State { INITIAL, TDIE_FOUND }

    public SensorStats probe() {
        String output = executor.invoke(fileSystem.getPath("/usr/bin/sensors"), "-u");
        String[] lines = output.split("\n");

        // Example output:
        //   iwlwifi_1-virtual-0
        //   Adapter: Virtual device
        //   temp1:
        //     temp1_input: 48.000
        //
        //   k10temp-pci-00c3
        //   Adapter: PCI adapter
        //   Tdie:
        //     temp1_input: 46.875
        //     temp1_max: 70.000
        //   Tctl:
        //     temp2_input: 46.875

        int i = 0;
        verifyLine("iwlwifi_1-virtual-0", lines, i++);
        verifyLine("Adapter: Virtual device", lines, i++);
        verifyLine("temp1:", lines, i++);
        float temperatureWifi = getFloatSuffix("  temp1_input: ", lines, i++);
        verifyLine("", lines, i++);
        verifyLine("k10temp-pci-00c3", lines, i++);
        verifyLine("Adapter: PCI adapter", lines, i++);
        verifyLine("Tdie:", lines, i++);
        float temperatureInput = getFloatSuffix("  temp1_input: ", lines, i++);
        float temperatureMax = getFloatSuffix("  temp1_max: ", lines, i++);
        verifyLine("Tctl:", lines, i++);
        float temperature2Input = getFloatSuffix("  temp2_input: ", lines, i++);

        // no more lines since above split() removes trailing empty elements.
        if (i != lines.length) {
            throw new IllegalArgumentException("Got more output from sensors than expected: " + output);
        }

        return new SensorStats(temperatureWifi, temperatureInput, temperatureMax, temperature2Input);
    }

    private void verifyLine(String line, String[] lines, int index) {
        if (index >= lines.length) {
            throw new IllegalArgumentException("End-of-file reached from sensors: Expected a line '" + line + "'");
        }

        if (!lines[index].equals(line)) {
            throw new IllegalArgumentException("Expected line '" + line + "' but got: " + lines[index]);
        }
    }

    private String getStringSuffix(String prefix, String[] lines, int index) {
        if (index >= lines.length) {
            throw new IllegalArgumentException("End-of-file reached from sensors: Expected a line " + prefix);
        }

        String line = lines[index];
        if (!line.startsWith(prefix)) {
            throw new IllegalArgumentException("Line does not start with the expected prefix '" + prefix + "': " + line);
        }

        return line.substring(prefix.length());
    }

    private float getFloatSuffix(String prefix, String[] lines, int index) {
        return Float.parseFloat(getStringSuffix(prefix, lines, index));
    }
}
