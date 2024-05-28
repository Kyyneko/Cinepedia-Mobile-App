package com.example.cinepedia.models;

public class FavoritModel {
    private int id;
    private int userId;
    private int movieId;

    public FavoritModel() {
    }

    public FavoritModel(int id, int userId, int movieId) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
