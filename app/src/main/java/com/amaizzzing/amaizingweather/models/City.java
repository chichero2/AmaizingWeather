package com.amaizzzing.amaizingweather.models;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import com.amaizzzing.amaizingweather.WeatherRequest;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class City {
    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("timezone")
    @Expose
    private int timezone;

    @SerializedName("sunrise")
    @Expose
    private long sunrise;

    @SerializedName("sunset")
    @Expose
    private long sunset;

    private String adminName;
    private String countryName;
    private double lat;
    private double lng;

    @Ignore
    private List<WeatherRequest> forecast;

    public City() {
    }

    public City(String nameCity, String countryName, String adminName, long id_city, double lat, double lng, List<WeatherRequest> forecast) {
        this.name = nameCity;
        this.countryName = countryName;
        this.adminName = adminName;
        if (forecast == null) {
            this.forecast = new ArrayList<>();
        } else {
            this.forecast = forecast;
        }
        this.id = id_city;
        this.lat = lat;
        this.lng = lng;
    }

    private ArrayList<WeatherRequest> getForecastDays(ArrayList<WeatherRequest> hourlyForecast) {
        if (hourlyForecast.size() != 0) {
            ArrayList<WeatherRequest> dailyForecast = new ArrayList<>();
            hourlyForecast.get(0).setChecked(true);
            dailyForecast.add(hourlyForecast.get(0));
            Calendar dailyCalendar = Calendar.getInstance();
            dailyCalendar.setTime(new Date(hourlyForecast.get(0).getDt()));
            int day = dailyCalendar.get(Calendar.DAY_OF_MONTH);
            for (WeatherRequest wf : hourlyForecast) {
                dailyCalendar = Calendar.getInstance();
                dailyCalendar.setTime(new Date(wf.getDt()));
                if (day != dailyCalendar.get(Calendar.DAY_OF_MONTH)) {
                    wf.setStrategyWeather(wf.getWeather()[0].getMain());
                    dailyForecast.add(wf);
                }
                day = dailyCalendar.get(Calendar.DAY_OF_MONTH);
            }
            return dailyForecast;
        }

        return null;
    }

    public void clearWeatherForecast() {
        if (this.forecast != null) {
            for (WeatherRequest wf : this.forecast) {
                wf.setChecked(false);
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<WeatherRequest> getForecast() {
        return forecast;
    }

    public void setForecast(List<WeatherRequest> forecast) {
        this.forecast = forecast;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
