package com.amaizzzing.amaizingweather;

import com.amaizzzing.amaizingweather.models.City;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherRequestForecast {
    @SerializedName("city")
    @Expose
    private City city;
    @SerializedName("list")
    @Expose
    private ArrayList<WeatherRequest> list = new ArrayList<>();

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public ArrayList<WeatherRequest> getWeatherRequestList() {
        return list;
    }

    public void setWeatherRequestList(ArrayList<WeatherRequest> weatherRequestList) {
        this.list = weatherRequestList;
    }
}
