package com.example.cinepedia.models;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("key")
    private String key;
    @SerializedName("site")
    private String site;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
