package com.amaizzzing.amaizingweather;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    @GET("data/2.5/forecast")
    Observable<WeatherRequestForecast> loadWeather(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("units") String units,
            @Query("appid") String keyApi
    );
}
