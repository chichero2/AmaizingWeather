package com.amaizzzing.amaizingweather.repository;

import com.amaizzzing.amaizingweather.models.CityRoom;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface Repository {
    Single<Long> insert(CityRoom city);

    Maybe<List<CityRoom>> getCityList();

    List<CityRoom> searchCities(String searchText);

    long getIdByName(String nameCity);

    String getNameById(long id);

    CityRoom getCityById(long id);

    Maybe<CityRoom> getCityByName(String nameCity);

    Maybe<CityRoom> getSelectedCity();

    long getIdSelectedCity();

    Maybe<List<CityRoom>> getLastHistoryIdCities(int limit1);

    void publisherNotify(String nameCity);

    Single<Integer> update(CityRoom city);

    Single<Integer> updateCityIdSunriceSunset(CityRoom city);

    Maybe<CityRoom> getLastHistoryCity();

    Maybe<CityRoom> getLastAddedCity();

    Flowable<List<CityRoom>> getCitiesFlow();
}
