package no.ion.hwmonitor.liquidctl;

public class KrakenStats {
    private final float temperature;
    private final int fanSpeedRpm;
    private final int pumpSpeedRpm;

    public KrakenStats(float temperature, int fanSpeedRpm, int pumpSpeedRpm) {
        this.temperature = temperature;
        this.fanSpeedRpm = fanSpeedRpm;
        this.pumpSpeedRpm = pumpSpeedRpm;
    }

    public float temperature() { return temperature; }
    public int fanSpeedRpm() { return fanSpeedRpm; }
    public int pumpSpeedRpm() { return pumpSpeedRpm; }

    @Override
    public String toString() {
        return "NzxtKrakenX{" +
                "temperature=" + temperature +
                ", fanSpeedRpm=" + fanSpeedRpm +
                ", pumpSpeedRpm=" + pumpSpeedRpm +
                '}';
    }
}
