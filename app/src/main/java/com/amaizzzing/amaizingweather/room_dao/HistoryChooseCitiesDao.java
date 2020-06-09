package com.amaizzzing.amaizingweather.room_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.amaizzzing.amaizingweather.models.HistoryChooseCities;

import io.reactivex.Single;

@Dao
public interface HistoryChooseCitiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(HistoryChooseCities historyChooseCities);

    @Query("update history_choose_cities set id_city=:idCity where id=(select id from history_choose_cities order by id desc limit 1)")
    Single<Integer> updateLastByCityId(long idCity);

    @Query("update history_choose_cities set id_city=:idCity where id_city=0")
    Single<Integer> updateByCityIdZeroIdCity(long idCity);
}
