package com.amaizzzing.amaizingweather.repository;

import android.content.Context;

import com.amaizzzing.amaizingweather.observer.Publisher;

public class ContextCity {
    private Repository repository;

    public ContextCity(Context ctx) {
        this.repository = new CityRepositoryRoom(ctx);
    }

    public Repository getRepository() {
        return repository;
    }
}
