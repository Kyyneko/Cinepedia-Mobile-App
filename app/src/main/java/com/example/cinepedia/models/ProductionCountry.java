package com.example.cinepedia.models;

import com.google.gson.annotations.SerializedName;

public class ProductionCountry {
    @SerializedName("iso_3166_1")
    private String iso3166;

    @SerializedName("name")
    private String name;

    public ProductionCountry(String iso3166, String name) {
        this.iso3166 = iso3166;
        this.name = name;
    }
    public String getIso3166() {
        return iso3166;
    }

    public void setIso3166(String iso3166) {
        this.iso3166 = iso3166;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
