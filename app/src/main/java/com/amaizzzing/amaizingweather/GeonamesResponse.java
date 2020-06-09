package com.amaizzzing.amaizingweather;

import com.amaizzzing.amaizingweather.models.GeonamesModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GeonamesResponse {
    @SerializedName("geonames")
    @Expose
    public ArrayList<GeonamesModel> geonames;

    public ArrayList<GeonamesModel> getGeonames() {
        return geonames;
    }

    public void setGeonames(ArrayList<GeonamesModel> geonames) {
        this.geonames = geonames;
    }
}
