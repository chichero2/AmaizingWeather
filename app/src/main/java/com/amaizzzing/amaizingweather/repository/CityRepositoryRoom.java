package com.amaizzzing.amaizingweather.repository;

import android.content.Context;

import com.amaizzzing.amaizingweather.MyApplication;
import com.amaizzzing.amaizingweather.WeatherDB;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.observer.Publisher;
import com.amaizzzing.amaizingweather.observer.PublisherGetter;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class CityRepositoryRoom implements Repository {
    private Publisher publisher;
    private WeatherDB weatherDB;

    CityRepositoryRoom(Context ctx) {
        this.weatherDB = MyApplication.getInstance().getDatabase();
        try {
            publisher = ((PublisherGetter) ctx).getPublisher();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Single<Long> insert(CityRoom city) {
        return weatherDB.CityDao().insert(city);
    }

    @Override
    public Maybe<List<CityRoom>> getCityList() {
        return weatherDB.CityDao().getCityList();
    }

    @Override
    public List<CityRoom> searchCities(String searchText) {
        return weatherDB.CityDao().searchCities(searchText);
    }

    @Override
    public long getIdByName(String nameCity) {
        return weatherDB.CityDao().getIdByName(nameCity);
    }

    @Override
    public String getNameById(long id) {
        return weatherDB.CityDao().getNameById(id);
    }

    @Override
    public CityRoom getCityById(long id) {
        return weatherDB.CityDao().getCityById(id);
    }

    @Override
    public Maybe<CityRoom> getCityByName(String nameCity) {
        return weatherDB.CityDao().getCityByName(nameCity);
    }

    @Override
    public Maybe<CityRoom> getSelectedCity() {
        return null;
    }

    @Override
    public long getIdSelectedCity() {
        return 0;
    }


    @Override
    public Maybe<List<CityRoom>> getLastHistoryIdCities(int limit1) {
        return weatherDB.CityDao().getLastHistoryIdCities(limit1);
    }

    @Override
    public void publisherNotify(String nameCity) {
        publisher.notify(nameCity);
    }

    @Override
    public Single<Integer> update(CityRoom city) {
        return weatherDB.CityDao().update(city);
    }

    @Override
    public Single<Integer> updateCityIdSunriceSunset(CityRoom city) {
        return weatherDB.CityDao().updateCityIdSunriceSunset(city.getId_city(), city.getSunrise(), city.getSunset(),city.getTimezone());
    }

    @Override
    public Maybe<CityRoom> getLastHistoryCity() {
        return weatherDB.CityDao().getLastHistoryCity();
    }

    @Override
    public Maybe<CityRoom> getLastAddedCity() {
        return weatherDB.CityDao().getLastAddedCity();
    }

    @Override
    public Flowable<List<CityRoom>> getCitiesFlow() {
        return weatherDB.CityDao().getCitiesFlow();
    }
}
