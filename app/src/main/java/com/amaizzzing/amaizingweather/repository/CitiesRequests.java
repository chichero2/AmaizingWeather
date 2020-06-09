package com.amaizzzing.amaizingweather.repository;

import com.amaizzzing.amaizingweather.models.City;

import java.util.ArrayList;

class CitiesRequests {
    private ArrayList<City> cities;

    CitiesRequests(ArrayList<City> cities) {
        this.cities = cities;
    }

    ArrayList<City> getCities() {
        return this.cities;
    }

    ArrayList<City> getCitiesLike(String findStr) {
        ArrayList<City> findCities = new ArrayList<>();
        for (City ct : cities) {
            if (ct.getName().toLowerCase().contains(findStr.toLowerCase())) {
                findCities.add(ct);
            }
        }
        return findCities;
    }

    int getRealPosByName(String nameCity) {
        int i = 0;
        for (City ct : cities) {
            if (ct.getName().equals(nameCity)) {
                break;
            }
            i++;
        }
        return i;
    }

    City getCity(int pos) {
        return cities.get(pos);
    }
}
