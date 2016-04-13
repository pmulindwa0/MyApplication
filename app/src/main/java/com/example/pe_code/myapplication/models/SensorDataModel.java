package com.example.pe_code.myapplication.models;

/**
 * Created by PE-CODE on 3/9/2016.
 */
public class SensorDataModel {
    private float temperature;
    private float humidity;
    private float pH;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getpH() {
        return pH;
    }

    public void setpH(float pH) {
        this.pH = pH;
    }
}
