package com.amaizzzing.amaizingweather;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application implements Constants {
    public static Retrofit retrofit;
    public static MyApplication instance;
    private WeatherDB database;
    public static boolean isNetwork;

    public static Retrofit getRetrofitInstance(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, WeatherDB.class, NAME_WEATHER_DB).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        db.execSQL("insert into city values(1,524901,'Москва',10800,1591663602,1591726272,'Москва','Россия',55.75222,37.61556)");
                        db.execSQL("insert into city values(2,519690,'Санкт-Петербург',10800,1591663156,1591730235,'Санкт-Петербург','Россия',59.93863,30.31413)");
                        db.execSQL("insert into city values(3,1494346,'Екатеринбург',18000,1591657588,1591721252,'Свердловская область','Россия',56.8519,60.6122)");
                        db.execSQL("insert into city values(4,501175,'Ростов-на-Дону',10800,1591665929,1591722938,'Ростовская область','Россия',47.23135,39.72328)");
                        db.execSQL("insert into city values(5,542420,'Краснодар',10800,1591666647,1591722574,'Краснодарский край','Россия',45.04484,38.97603)");
                        db.execSQL("insert into city values(6,2013348,'Владивосток',36000,1591731175,1591786272,'Приморский край','Россия',43.10562,131.87353)");
                        db.execSQL("insert into city values(7,1496747,'Новосибирск',25200,1591653031,1591715087,'Новосибирская область','Россия',55.0415,82.9346)");
                        db.execSQL("insert into city values(8,520555,'Нижний Новгород',10800,1591661813,1591724995,'Нижегородская область','Россия',56.32867,44.00205)");
                        db.execSQL("insert into city values(9,551487,'Казань',10800,1591660826,1591723525,'Татарстан','Россия',55.78874,49.12214)");
                        db.execSQL("insert into city values(10,1508291,'Челябинск',18000,1591658143,1591720299,'Челябинская область','Россия',55.15402,61.42915)");
                        db.execSQL("insert into history_choose_cities values(1,1508291)");
                        db.execSQL("insert into history_choose_cities values(2,551487)");
                        db.execSQL("insert into history_choose_cities values(3,520555)");
                        db.execSQL("insert into history_choose_cities values(4,1496747)");
                        db.execSQL("insert into history_choose_cities values(5,2013348)");
                        db.execSQL("insert into history_choose_cities values(6,542420)");
                        db.execSQL("insert into history_choose_cities values(7,501175)");
                        db.execSQL("insert into history_choose_cities values(8,1494346)");
                        db.execSQL("insert into history_choose_cities values(9,519690)");
                        db.execSQL("insert into history_choose_cities values(10,524901)");

                    }
                })
                .build();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public WeatherDB getDatabase() {
        return database;
    }

}
