package com.amaizzzing.amaizingweather.models;

import com.amaizzzing.amaizingweather.WeatherRequest;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main implements Cloneable{
    @SerializedName("temp")
    @Expose
    private float temp;
    @SerializedName("feels_like")
    @Expose
    private float feels_like;
    @SerializedName("pressure")
    @Expose
    private int pressure;
    @SerializedName("humidity")
    @Expose
    private int humidity;

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public float getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(float feels_like) {
        this.feels_like = feels_like;
    }

    public Main clone() throws CloneNotSupportedException{

        return (Main) super.clone();
    }
}

