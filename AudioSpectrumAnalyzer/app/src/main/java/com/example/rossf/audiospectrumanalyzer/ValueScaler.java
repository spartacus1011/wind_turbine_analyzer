package com.example.rossf.audiospectrumanalyzer;

public class ValueScaler {

    double oldMin;
    double newMin;
    double oldMax;
    double newMax;

    public ValueScaler(double oldMin, double oldMax, double newMin, double newMax) {
        this.oldMin = oldMin;
        this.oldMax = oldMax;
        this.newMin = newMin;
        this.newMax = newMax;
    }

    public double ScaleValue(double value) {

        return (((newMax - newMin)*(value - oldMin)) / (oldMax - oldMin)) + newMin;

    }
}
