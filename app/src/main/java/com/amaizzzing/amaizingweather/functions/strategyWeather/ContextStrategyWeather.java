package com.amaizzzing.amaizingweather.functions.strategyWeather;

import android.content.Context;
import android.graphics.drawable.Drawable;

import static com.amaizzzing.amaizingweather.R.string.weather_clouds;
import static com.amaizzzing.amaizingweather.R.string.weather_fog;
import static com.amaizzzing.amaizingweather.R.string.weather_rain;

public class ContextStrategyWeather {
    private StrategyWeatherInterface strategy;

    public ContextStrategyWeather(StrategyWeatherInterface strategy) {
        this.strategy = strategy;
    }

    public Drawable getDrawable(Context ctx) {
        return strategy.weatherImage(ctx);
    }

    public static StrategyWeatherInterface getStrategyWeatherInterface(Context ctx, String typeWeather) {
        if (typeWeather == null) {
            return new ClearWeatherStrategy();
        }
        if (typeWeather.equals(ctx.getResources().getString(weather_clouds))) {
            return new CloudsWeatherStrategy();
        }
        if (typeWeather.equals(ctx.getResources().getString(weather_fog))) {
            return new CloudsWeatherStrategy();
        }
        if (typeWeather.equals(ctx.getResources().getString(weather_rain))) {
            return new RainWeatherStrategy();
        }
        return new ClearWeatherStrategy();
    }

    public static String getUrlForPicasso(Context ctx, String typeWeather) {
        if (typeWeather.equals(ctx.getResources().getString(weather_clouds))) {
            return "https://images.unsplash.com/photo-1419833173245-f59e1b93f9ee?w=400";
        }
        if (typeWeather.equals(ctx.getResources().getString(weather_fog))) {
            return "https://images.unsplash.com/photo-1419833173245-f59e1b93f9ee?w=400";
        }
        if (typeWeather.equals(ctx.getResources().getString(weather_rain))) {
            return "https://images.unsplash.com/photo-1534274988757-a28bf1a57c17?w=400";
        }
        return "https://proza.ru/pics/2017/06/04/2206.jpg";
    }
}
