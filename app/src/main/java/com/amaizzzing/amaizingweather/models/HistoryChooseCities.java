package com.amaizzzing.amaizingweather.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_choose_cities")
public class HistoryChooseCities {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long id_city;

    public HistoryChooseCities(long id_city) {
        this.id_city = id_city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_city() {
        return id_city;
    }

    public void setId_city(long id_city) {
        this.id_city = id_city;
    }
}
