package com.example.cinepedia.models;

import com.google.gson.annotations.SerializedName;

public class Backdrop {
    @SerializedName("file_path")
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

