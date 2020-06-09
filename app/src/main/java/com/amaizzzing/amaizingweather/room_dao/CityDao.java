package com.amaizzzing.amaizingweather.room_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.amaizzzing.amaizingweather.models.CityRoom;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(CityRoom cityRoom);

    @Update
    Single<Integer> update(CityRoom cityRoom);

    @Query("update city set id_city=:id1,sunrise=:sunrise,sunset=:sunset,timezone=:timeZone where id_city=-1")
    Single<Integer> updateCityIdSunriceSunset(long id1, long sunrise, long sunset, long timeZone);

    @Query("select * from city where lower(name)=lower(:cityName)")
    Maybe<CityRoom> getCityByName(String cityName);

    @Query("select * from city")
    Maybe<List<CityRoom>> getCityList();

    @Query("select * from city where lower(name) like lower(:searchName)")
    List<CityRoom> searchCities(String searchName);

    @Query("select id_city from city where lower(name)=lower(:searchName)")
    long getIdByName(String searchName);

    @Query("select name from city where id_city=:id")
    String getNameById(long id);

    @Query("select * from city where id_city=:id")
    CityRoom getCityById(long id);

    @Query(" select distinct c.* " +
            "from history_choose_cities as hcc,city as c " +
            "where hcc.id_city=c.id_city " +
            "order by hcc.id desc limit :limit1")
    Maybe<List<CityRoom>> getLastHistoryIdCities(int limit1);

    @Query(" select distinct * " +
            "from city " +
            "where id_city=(select id_city from history_choose_cities order by id desc limit 1) limit 1")
    Maybe<CityRoom> getLastHistoryCity();

    @Query("select * from city order by id desc limit 1")
    Maybe<CityRoom> getLastAddedCity();

    @Query("select * from city order by id")
    Flowable<List<CityRoom>> getCitiesFlow();
}
