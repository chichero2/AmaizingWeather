package com.amaizzzing.amaizingweather.repository;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.models.City;

import java.util.ArrayList;

class CitiesData implements Constants {
    private static volatile CitiesData instance;
    private ArrayList<City> cities;
    CitiesRequests citiesRequests;

    private CitiesData() {
        cities = new ArrayList<>();
        this.citiesRequests = new CitiesRequests(cities);
    }

    static CitiesData getInstance() {
        CitiesData localInstance = instance;
        if (localInstance == null) {
            synchronized (CitiesData.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new CitiesData();
                }
            }
        }
        return localInstance;
    }

    void addToCities(City city) {
        this.cities.add(city);
    }
}
