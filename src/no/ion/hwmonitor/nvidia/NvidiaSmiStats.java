package no.ion.hwmonitor.nvidia;

public class NvidiaSmiStats {
    private final int fanSpeedInPercent;
    private final int memoryUsedInMb;
    private final int utilizationGpuInPercent;
    private final int averageFps;
    private final int temperatureGpu;
    private final float powerDraw;
    private final float powerLimit;

    public NvidiaSmiStats(int fanSpeedInPercent,
                          int memoryUsedInMb,
                          int utilizationGpuInPercent,
                          int averageFps,
                          int temperatureGpu,
                          float powerDraw,
                          float powerLimit) {

        this.fanSpeedInPercent = fanSpeedInPercent;
        this.memoryUsedInMb = memoryUsedInMb;
        this.utilizationGpuInPercent = utilizationGpuInPercent;
        this.averageFps = averageFps;
        this.temperatureGpu = temperatureGpu;
        this.powerDraw = powerDraw;
        this.powerLimit = powerLimit;
    }

    public int fanSpeedInPercent() { return fanSpeedInPercent; }
    public int memoryUsedInMb() { return memoryUsedInMb; }
    public int utilizationGpuInPercent() { return utilizationGpuInPercent; }
    public int averageFps() { return averageFps; }
    public int temperatureGpu() { return temperatureGpu; }
    public float powerDraw() { return powerDraw; }
    public float powerLimit() { return powerLimit; }

    @Override
    public String toString() {
        return "NvidiaSmiResult{" +
                "fanSpeedInPercent=" + fanSpeedInPercent +
                ", memoryUsedInMb=" + memoryUsedInMb +
                ", utilizationGpuInPercent=" + utilizationGpuInPercent +
                ", averageFps=" + averageFps +
                ", temperatureGpu=" + temperatureGpu +
                ", powerDraw=" + powerDraw +
                ", powerLimit=" + powerLimit +
                '}';
    }
}
