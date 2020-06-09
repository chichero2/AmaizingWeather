package com.amaizzzing.amaizingweather.mappers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.functions.strategyWeather.ContextStrategyWeather;
import com.amaizzzing.amaizingweather.models.CityRoom;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class UpdatedDataForActivity implements Constants {
    private String originalTemp = "-";
    private String originalFeels = "-";
    private String originalHumidity = "-";
    private String originalWind = "-";
    private String originalTypeWeather = "-";
    private String originalSunrice = "-";
    private String originalSunset = "-";
    private Drawable weatherImage = null;

    public UpdatedDataForActivity(CityRoom city, int forecastPos, Context ctx) {
        if (city != null && city.getDailyForecast() != null && city.getDailyForecast().size() != 0) {
            SimpleDateFormat sunriceSdf = new SimpleDateFormat(HH_MM, Locale.ENGLISH);
            sunriceSdf.setTimeZone(TimeZone.getTimeZone(GMT));
            this.originalTemp = String.format((int) city.getDailyForecast().get(forecastPos).getMain().getTemp() > 0 ? "+%d" : "%d", (int) city.getDailyForecast().get(forecastPos).getMain().getTemp());
            this.originalFeels = String.format((int) city.getDailyForecast().get(forecastPos).getMain().getFeels_like() > 0 ? ctx.getResources().getString(R.string.plus_info11) : ctx.getResources().getString(R.string.info11), (int) city.getDailyForecast().get(forecastPos).getMain().getFeels_like());
            this.originalHumidity = String.format(ctx.getResources().getString(R.string.info22), city.getDailyForecast().get(forecastPos).getMain().getHumidity());
            this.originalWind = String.format(ctx.getResources().getString(R.string.info33), (int) city.getDailyForecast().get(forecastPos).getWind().getSpeed());
            this.originalTypeWeather = city.getDailyForecast().get(forecastPos).getWeather()[0].getMain();
            this.originalSunrice = String.valueOf(sunriceSdf.format((city.getSunrise() + city.getTimezone()) * 1000));
            this.originalSunset = String.valueOf(sunriceSdf.format((city.getSunset() + city.getTimezone()) * 1000));
            this.weatherImage = ContextStrategyWeather.getStrategyWeatherInterface(ctx, city.getDailyForecast().get(forecastPos).getStrategyWeather()).weatherImage(ctx);
        }
    }

    public String getOriginalTemp() {
        return originalTemp;
    }

    public String getOriginalFeels() {
        return originalFeels;
    }

    public String getOriginalHumidity() {
        return originalHumidity;
    }

    public String getOriginalWind() {
        return originalWind;
    }

    public String getOriginalTypeWeather() {
        return originalTypeWeather;
    }

    public String getOriginalSunrice() {
        return originalSunrice;
    }

    public String getOriginalSunset() {
        return originalSunset;
    }

    public Drawable getWeatherImage() {
        return weatherImage;
    }
}
