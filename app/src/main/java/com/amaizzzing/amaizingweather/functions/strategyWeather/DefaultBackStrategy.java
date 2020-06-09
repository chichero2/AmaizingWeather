package com.amaizzzing.amaizingweather.functions.strategyWeather;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.amaizzzing.amaizingweather.R;

public class DefaultBackStrategy implements StrategyWeatherInterface {
    @Override
    public Drawable weatherImage(Context ctx) {
        if(ctx==null)
            return null;
        return ContextCompat.getDrawable(ctx, R.drawable.nav_header_image);
    }
}
