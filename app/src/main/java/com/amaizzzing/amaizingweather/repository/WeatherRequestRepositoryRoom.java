package com.amaizzzing.amaizingweather.repository;

import com.amaizzzing.amaizingweather.MyApplication;
import com.amaizzzing.amaizingweather.WeatherDB;
import com.amaizzzing.amaizingweather.models.WeatherRequestRoom;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class WeatherRequestRepositoryRoom {
    private WeatherDB weatherDB;

    public WeatherRequestRepositoryRoom() {
        this.weatherDB = MyApplication.getInstance().getDatabase();
    }

    public Single<Long> insert(WeatherRequestRoom weatherRequest) {
        return weatherDB.weatherRequestRoomDao().insert(weatherRequest);
    }

    public Maybe<List<WeatherRequestRoom>> getWeatherForCity(long idCity) {
        return weatherDB.weatherRequestRoomDao().getWeatherForCity(idCity);
    }

    public Maybe<WeatherRequestRoom> getWeatherByIdCityAndDate(long idCity, long date1) {
        return weatherDB.weatherRequestRoomDao().getWeatherByIdCityAndDate(idCity, date1);
    }

    public Maybe<List<WeatherRequestRoom>> getWeatherByNameCityBetweenDates(String nameCity, long date1, long date2) {
        return weatherDB.weatherRequestRoomDao().getWeatherByNameCityBetweenDates(nameCity, date1, date2);
    }

    public Maybe<List<WeatherRequestRoom>> getWeatherByNameCityBetweenDatesBetweenTemp(String nameCity, long date1, long date2, int temp1, int temp2) {
        return weatherDB.weatherRequestRoomDao().getWeatherByNameCityBetweenDatesBetweenTemp(nameCity, date1, date2, temp1, temp2);
    }

    public Flowable<List<WeatherRequestRoom>> getWeatherForCityFlow() {
        return weatherDB.weatherRequestRoomDao().getWeatherForCityFlow();
    }
}
