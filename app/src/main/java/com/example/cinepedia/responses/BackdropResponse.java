package com.example.cinepedia.responses;

import com.example.cinepedia.models.Backdrop;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BackdropResponse {
    @SerializedName("backdrops")
    private List<Backdrop> backdrops;

    public List<Backdrop> getBackdrops() {
        return backdrops;
    }
}
