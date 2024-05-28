package com.example.cinepedia.models;

import com.google.gson.annotations.SerializedName;

public class ProductionCompany {
    @SerializedName("id")
    private int id;
    @SerializedName("logo_path")
    private String logoPath;
    @SerializedName("name")
    private String name;
    @SerializedName("origin_country")
    private String originCountry;
    public ProductionCompany(int id, String logoPath, String name, String originCountry) {
        this.id = id;
        this.logoPath = logoPath;
        this.name = name;
        this.originCountry = originCountry;
    }
    public int getId() {
        return id;
    }
    public String getLogoPath() {
        return logoPath;
    }
    public String getName() {
        return name;
    }
    public String getOriginCountry() {
        return originCountry;
    }
}
