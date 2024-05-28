package com.example.cinepedia.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Movie {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("video")
    private boolean video;

    @SerializedName("budget")
    private long budget;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("homepage")
    private String homepage;

    @SerializedName("imdb_id")
    private String imdbId;

    @SerializedName("origin_country")
    private List<String> originCountry;

    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies;

    @SerializedName("production_countries")
    private List<ProductionCountry> productionCountries;

    @SerializedName("revenue")
    private long revenue;

    @SerializedName("runtime")
    private int runtime;
    @SerializedName("status")
    private String status;

    @SerializedName("tagline")
    private String tagline;

    public Movie(int id, String title, String overview, String releaseDate, String posterPath, double voteAverage, int voteCount, String backdropPath, List<Integer> genreIds, boolean adult, String originalLanguage, String originalTitle, double popularity, boolean video, long budget, List<Genre> genres, String homepage, String imdbId, List<String> originCountry, List<ProductionCompany> productionCompanies, List<ProductionCountry> productionCountries, long revenue, int runtime , String status, String tagline) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.backdropPath = backdropPath;
        this.genreIds = genreIds;
        this.adult = adult;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.popularity = popularity;
        this.video = video;
        this.budget = budget;
        this.genres = genres;
        this.homepage = homepage;
        this.imdbId = imdbId;
        this.originCountry = originCountry;
        this.productionCompanies = productionCompanies;
        this.productionCountries = productionCountries;
        this.revenue = revenue;
        this.runtime = runtime;
        this.status = status;
        this.tagline = tagline;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getOverview() {
        return overview;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public double getVoteAverage() {
        return voteAverage;
    }
    public String getBackdropPath() {
        return backdropPath;
    }
    public boolean isAdult() {
        return adult;
    }
    public String getOriginalLanguage() {
        return originalLanguage;
    }
    public List<Genre> getGenres() {
        return genres;
    }
    public String getHomepage() {
        return homepage;
    }
    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }
    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }
    public int getRuntime() {
        return runtime;
    }
    public String getStatus() {
        return status;
    }
    public String getTagline() {
        return tagline;
    }

}