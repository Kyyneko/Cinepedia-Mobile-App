package com.example.cinepedia.responses;

import com.example.cinepedia.models.TVShow;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowResponse {

    @SerializedName("results")
    private List<TVShow> tvShows;

    public List<TVShow> getTvShows() {
        return tvShows;
    }
}
