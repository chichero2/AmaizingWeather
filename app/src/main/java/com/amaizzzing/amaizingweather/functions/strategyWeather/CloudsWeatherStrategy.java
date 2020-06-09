package com.amaizzzing.amaizingweather.functions.strategyWeather;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.amaizzzing.amaizingweather.R;

public class CloudsWeatherStrategy implements StrategyWeatherInterface {
    @Override
    public Drawable weatherImage(Context ctx) {
        return ContextCompat.getDrawable(ctx, R.drawable.weather_img);
    }
}
