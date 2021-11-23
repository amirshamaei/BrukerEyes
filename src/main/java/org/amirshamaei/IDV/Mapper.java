package org.amirshamaei.IDV;

public class Mapper {
    private static Mapper instance = new Mapper();

    public double getInitValuelow() {
        return initValuelow;
    }

    public double getInitValuehigh() {
        return initValuehigh;
    }

    public void setInitValue(double initValuelow, double initValuehigh) {
        this.initValuelow = initValuelow/255;
        this.initValuehigh = initValuehigh/255;
    }

    private double initValuelow = 0;
    private double initValuehigh = 1;
    private boolean LogScale;

    public void setLogScale(boolean logScale) {
        LogScale = logScale;
    }

    public static Mapper getInstance() {
        return instance;
    }
    private Mapper() {

    }
    public boolean isLogScale () {
        return LogScale;
    }
    public double mapValue(double value) {
        double newValue = initValuelow + (initValuehigh-initValuelow) * value;
        return newValue;
    }
}
