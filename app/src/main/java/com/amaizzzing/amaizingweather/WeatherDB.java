package com.amaizzzing.amaizingweather;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.models.HistoryChooseCities;
import com.amaizzzing.amaizingweather.models.WeatherRequestRoom;
import com.amaizzzing.amaizingweather.room_dao.CityDao;
import com.amaizzzing.amaizingweather.room_dao.HistoryChooseCitiesDao;
import com.amaizzzing.amaizingweather.room_dao.WeatherRequestRoomDao;

@Database(
        entities = {
                CityRoom.class,
                HistoryChooseCities.class,
                WeatherRequestRoom.class
        },
        version = 2)
public abstract class WeatherDB extends RoomDatabase {
    public abstract CityDao CityDao();

    public abstract HistoryChooseCitiesDao historyChooseCitiesDao();

    public abstract WeatherRequestRoomDao weatherRequestRoomDao();
}
