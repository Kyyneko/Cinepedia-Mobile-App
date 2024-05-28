package com.example.cinepedia.responses;

import com.example.cinepedia.models.Genre;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GenreResponse {
    @SerializedName("genres")
    private List<Genre> genre;

    public GenreResponse(List<Genre> genres) {
        this.genre = genres;
    }

    public List<Genre> getGenres() {
        return genre;
    }
}
