package com.amaizzzing.amaizingweather;

import com.amaizzzing.amaizingweather.models.Clouds;
import com.amaizzzing.amaizingweather.models.Coord;
import com.amaizzzing.amaizingweather.models.Main;
import com.amaizzzing.amaizingweather.models.Sys;
import com.amaizzzing.amaizingweather.models.Weather;
import com.amaizzzing.amaizingweather.models.Wind;

public class WeatherRequest {
    private Coord coord;
    private Weather[] weather;
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private Sys sys;
    private String name;
    private int timezone;
    private long dt;
    private String strategyWeather;
    private boolean isChecked;

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public String getStrategyWeather() {
        return strategyWeather;
    }

    public void setStrategyWeather(String strategyWeather) {
        this.strategyWeather = strategyWeather;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
