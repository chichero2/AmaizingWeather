package com.amaizzzing.amaizingweather.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.functions.DateFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity(tableName = "city")
public class CityRoom {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long id_city;
    private String name;
    private int timezone;
    private long sunrise;
    private long sunset;
    private String adminName;
    private String countryName;
    private double lat;
    private double lng;
    @Ignore
    private List<WeatherRequest> forecast;
    @Ignore
    private List<WeatherRequest> dailyForecast;

    public CityRoom() {
    }

    public CityRoom(long id_city, String name, int timezone, long sunrise, long sunset, String adminName, String countryName, double lat, double lng) {
        this.id_city = id_city;
        this.name = name;
        this.timezone = timezone;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.adminName = adminName;
        this.countryName = countryName;
        this.lat = lat;
        this.lng = lng;
    }

    public CityRoom(long id_city, String name, int timezone, long sunrise, long sunset, String adminName, String countryName, double lat, double lng, List<WeatherRequest> forecast) {
        this.id_city = id_city;
        this.name = name;
        this.timezone = timezone;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.adminName = adminName;
        this.countryName = countryName;
        this.lat = lat;
        this.lng = lng;
        if (forecast == null) {
            this.forecast = new ArrayList<>();
        } else {
            this.forecast = forecast;
            this.dailyForecast = getForecastDays((ArrayList<WeatherRequest>) forecast);
        }

    }

    private ArrayList<WeatherRequest> getForecastDays(ArrayList<WeatherRequest> hourlyForecast) {
        if (hourlyForecast.size() != 0) {
            int countDay=0;
            ArrayList<WeatherRequest> dailyForecast = new ArrayList<>();
            hourlyForecast.get(0).setChecked(true);
            dailyForecast.add(hourlyForecast.get(0));
            Calendar dailyCalendar = Calendar.getInstance();
            dailyCalendar.setTime(new Date(hourlyForecast.get(0).getDt()));
            int day = dailyCalendar.get(Calendar.DAY_OF_MONTH);
            float maxTempForDay=-999;
            for (WeatherRequest wf : hourlyForecast) {
                dailyCalendar = Calendar.getInstance();
                dailyCalendar.setTime(new Date(wf.getDt()));
                if(wf.getMain().getTemp()>maxTempForDay){
                    maxTempForDay=wf.getMain().getTemp();
                }
                if (day != dailyCalendar.get(Calendar.DAY_OF_MONTH)) {
                    dailyForecast.get(countDay++).getMain().setTemp(maxTempForDay);
                    maxTempForDay=-999;
                    wf.setStrategyWeather(wf.getWeather()[0].getMain());
                    dailyForecast.add(wf);
                }
                day = dailyCalendar.get(Calendar.DAY_OF_MONTH);
            }
            if(maxTempForDay!=-999) {
                dailyForecast.get(countDay).getMain().setTemp(maxTempForDay);
            }
            return dailyForecast;
        }

        return null;
    }

    public int getCountHourlyForecastInDay(long day) {
        int count = 0;
        long startDay = DateFunctions.atStartOfDay(day);
        long endDay = DateFunctions.atEndOfDay(day);
        for (WeatherRequest wr : forecast) {
            if (wr.getDt() >= startDay && wr.getDt() <= endDay) {
                count++;
            }
        }
        return count;
    }

    public void clearWeatherForecast() {
        if (this.forecast != null) {
            for (WeatherRequest wf : this.forecast) {
                wf.setChecked(false);
            }
        }
    }

    public int getChosenForecastPosition() {
        if (this.dailyForecast == null) {
            return 0;
        }
        for (int i = 0; i < this.dailyForecast.size(); i++) {
            if (this.dailyForecast.get(i).isChecked()) {
                return i;
            }
        }
        return 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_city() {
        return id_city;
    }

    public void setId_city(long id_city) {
        this.id_city = id_city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<WeatherRequest> getForecast() {
        return forecast;
    }

    public void setForecast(List<WeatherRequest> forecast) {
        this.forecast = forecast;
    }

    public List<WeatherRequest> getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast() {
        if (this.forecast == null || this.forecast.size() == 0) {
            this.dailyForecast = new ArrayList<>();
        } else {
            this.dailyForecast = getForecastDays((ArrayList<WeatherRequest>) forecast);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
