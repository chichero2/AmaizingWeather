package com.amaizzzing.amaizingweather.repository;

import com.amaizzzing.amaizingweather.MyApplication;
import com.amaizzzing.amaizingweather.WeatherDB;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.models.HistoryChooseCities;

import io.reactivex.Single;

public class HistoryChooseCitiesRepository {
    private WeatherDB weatherDB;

    public HistoryChooseCitiesRepository() {
        this.weatherDB = MyApplication.getInstance().getDatabase();
    }

    public Single<Long> addToHistory(CityRoom city) {
        return weatherDB.historyChooseCitiesDao().insert(new HistoryChooseCities(city.getId_city()));
    }

    public Single<Integer> updateLastByCityId(long idCity) {
        return weatherDB.historyChooseCitiesDao().updateLastByCityId(idCity);
    }

    public Single<Integer> updateByCityIdZeroIdCity(long idCity) {
        return weatherDB.historyChooseCitiesDao().updateByCityIdZeroIdCity(idCity);
    }

    ;
}
