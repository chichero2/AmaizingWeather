package com.amaizzzing.amaizingweather.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_request")
public class WeatherRequestRoom {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long id_city;

    private int temperature;
    private int feels;
    private int humidity;
    private int wind;
    private String typeWeather;
    private long date;

    public WeatherRequestRoom(long id_city,int temperature, int feels, int humidity, int wind, String typeWeather, long date) {
        this.id_city=id_city;
        this.temperature = temperature;
        this.feels = feels;
        this.humidity = humidity;
        this.wind = wind;
        this.typeWeather = typeWeather;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getFeels() {
        return feels;
    }

    public void setFeels(int feels) {
        this.feels = feels;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public String getTypeWeather() {
        return typeWeather;
    }

    public void setTypeWeather(String typeWeather) {
        this.typeWeather = typeWeather;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getId_city() {
        return id_city;
    }

    public void setId_city(long id_city) {
        this.id_city = id_city;
    }
}
