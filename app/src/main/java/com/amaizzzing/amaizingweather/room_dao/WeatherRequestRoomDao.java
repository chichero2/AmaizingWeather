package com.amaizzzing.amaizingweather.room_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.amaizzzing.amaizingweather.models.WeatherRequestRoom;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;


@Dao
public interface WeatherRequestRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(WeatherRequestRoom weatherRequestRoom);

    @Query("select * from weather_request where date>=(strftime('%s','now')*1000) order by date")
    Flowable<List<WeatherRequestRoom>> getWeatherForCityFlow();

    @Query("select * from weather_request where id_city=:idCity and date=:date1")
    Maybe<WeatherRequestRoom> getWeatherByIdCityAndDate(long idCity, long date1);

    @Query("select * from weather_request where id_city=:idCity and date>=(strftime('%s','now')*1000) order by date")
    Maybe<List<WeatherRequestRoom>> getWeatherForCity(long idCity);

    @Query(" select * " +
            "from weather_request " +
            "where id_city=(select id_city from city where name=:nameCity) and (" +
            "      date between :date1 and :date2) " +
            "order by date")
    Maybe<List<WeatherRequestRoom>> getWeatherByNameCityBetweenDates(String nameCity, long date1, long date2);

    @Query(" select * " +
            "from weather_request " +
            "where id_city=(select id_city from city where name=:nameCity) and " +
            "      (date between :date1 and :date2)  and " +
            "      (temperature between :temp1 and :temp2) " +
            "order by date")
    Maybe<List<WeatherRequestRoom>> getWeatherByNameCityBetweenDatesBetweenTemp(String nameCity, long date1, long date2, int temp1, int temp2);

}
