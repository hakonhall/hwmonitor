package no.ion.hwmonitor.lmsensors;

public class SensorStats {
    private final float temperatureWifi;
    private final float temperatureInput;
    private final float temperatureMax;
    private final float temperature2Input;

    public SensorStats(float temperatureWifi, float temperatureInput, float temperatureMax, float temperature2Input) {
        this.temperatureWifi = temperatureWifi;
        this.temperatureInput = temperatureInput;
        this.temperatureMax = temperatureMax;
        this.temperature2Input = temperature2Input;
    }

    public float temperatureWifi() { return temperatureWifi; }
    public float temperatureInput() { return temperatureInput; }
    public float temperatureMax() { return temperatureMax; }
    public float temperature2Input() { return temperature2Input; }

    @Override
    public String toString() {
        return "SensorStats{" +
                "temperatureWifi=" + temperatureWifi +
                ", temperatureInput=" + temperatureInput +
                ", temperatureMax=" + temperatureMax +
                ", temperature2Input=" + temperature2Input +
                '}';
    }
}
