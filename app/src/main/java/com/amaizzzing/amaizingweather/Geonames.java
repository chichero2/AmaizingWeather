package com.amaizzzing.amaizingweather;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Geonames {
    @GET("searchJSON")
    Observable<GeonamesResponse> loadGeoNames(
            @Query("name") String nameCity,
            @Query("maxRows") String maxRows,
            @Query("username") String username,
            @Query("lang") String lang
    );
}
