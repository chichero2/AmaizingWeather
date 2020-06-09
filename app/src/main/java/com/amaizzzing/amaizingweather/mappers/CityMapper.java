package com.amaizzzing.amaizingweather.mappers;

import com.amaizzzing.amaizingweather.models.City;
import com.amaizzzing.amaizingweather.models.CityRoom;

import java.util.ArrayList;

public class CityMapper {
    public static CityRoom CityToRoom(City city){
        return new CityRoom(
                city.getId(),
                city.getName(),
                city.getTimezone(),
                city.getSunrise(),
                city.getSunset(),
                city.getAdminName(),
                city.getCountryName(),
                city.getLat(),
                city.getLng(),
                city.getForecast()
        );
    }

    public static City CityRoomToCity(CityRoom cityRoom){
        return new City(
                cityRoom.getName(),
                cityRoom.getCountryName(),
                cityRoom.getAdminName(),
                cityRoom.getId_city(),
                cityRoom.getLat(),
                cityRoom.getLng(),
                cityRoom.getForecast()
        );
    }
}
