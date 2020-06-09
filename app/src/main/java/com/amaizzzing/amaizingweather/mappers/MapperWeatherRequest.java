package com.amaizzzing.amaizingweather.mappers;

import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.models.Main;
import com.amaizzzing.amaizingweather.models.Weather;
import com.amaizzzing.amaizingweather.models.WeatherRequestRoom;
import com.amaizzzing.amaizingweather.models.Wind;

public class MapperWeatherRequest {
    public static WeatherRequestRoom WeatherRequestToRoom(long idCity, WeatherRequest weatherRequest) {
        return new WeatherRequestRoom(
                idCity,
                (int) weatherRequest.getMain().getTemp(),
                (int) weatherRequest.getMain().getFeels_like(),
                weatherRequest.getMain().getHumidity(),
                (int) weatherRequest.getWind().getSpeed(),
                weatherRequest.getWeather()[0].getMain(),
                weatherRequest.getDt()
        );
    }

    public static WeatherRequest RoomToWeatherRequest(WeatherRequestRoom weatherRequestRoom) {
        WeatherRequest weatherRequest = new WeatherRequest();
        weatherRequest.setMain(new Main());
        weatherRequest.getMain().setTemp((float) weatherRequestRoom.getTemperature());
        weatherRequest.getMain().setFeels_like((float) weatherRequestRoom.getFeels());
        weatherRequest.getMain().setHumidity(weatherRequestRoom.getHumidity());
        weatherRequest.setWind(new Wind());
        weatherRequest.getWind().setSpeed((float) weatherRequestRoom.getWind());
        Weather[] weather = new Weather[1];
        weather[0] = new Weather();
        weather[0].setMain(weatherRequestRoom.getTypeWeather());
        weatherRequest.setWeather(weather);
        weatherRequest.setDt(weatherRequestRoom.getDate());
        weatherRequest.setStrategyWeather(weatherRequestRoom.getTypeWeather());

        return weatherRequest;
    }
}
