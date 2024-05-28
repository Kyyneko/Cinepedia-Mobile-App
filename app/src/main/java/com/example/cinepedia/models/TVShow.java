package com.example.cinepedia.models;

import com.google.gson.annotations.SerializedName;

public class TVShow {

    @SerializedName("name")
    private String title;

    @SerializedName("poster_path")
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return "https://image.tmdb.org/t/p/w500" + imageUrl;
    }
}
