package com.amaizzzing.amaizingweather.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeonamesModel {
    @SerializedName("toponymName")
    @Expose
    private String toponymName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    @SerializedName("geonameId")
    @Expose
    private long geonameId;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("fcl")
    @Expose
    private String fcl;
    @SerializedName("fcode")
    @Expose
    private String fcode;
    @SerializedName("adminName1")
    @Expose
    private String adminName1;

    public String getToponymName() {
        return toponymName;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public long getGeonameId() {
        return geonameId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getFcl() {
        return fcl;
    }

    public String getFcode() {
        return fcode;
    }

    public void setToponymName(String toponymName) {
        this.toponymName = toponymName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setGeonameId(long geonameId) {
        this.geonameId = geonameId;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setFcl(String fcl) {
        this.fcl = fcl;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getAdminName1() {
        return adminName1;
    }

    public void setAdminName1(String adminName1) {
        this.adminName1 = adminName1;
    }
}
