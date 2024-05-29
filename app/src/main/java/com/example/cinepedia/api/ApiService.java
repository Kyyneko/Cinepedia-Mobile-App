package com.example.cinepedia.api;

import com.example.cinepedia.responses.BackdropResponse;
import com.example.cinepedia.responses.GenreResponse;
import com.example.cinepedia.models.Movie;
import com.example.cinepedia.responses.MovieResponse;
import com.example.cinepedia.responses.TVShowResponse;
import com.example.cinepedia.responses.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("discover/movie")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/{movie_id}")
    Call<Movie>getMovieDetails(@Path("movie_id") int movieId,
                                @Query("api_key")
                                String apiKey
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page);

    @GET("movie/{movie_id}/recommendations")
    Call<MovieResponse> getRecommendedMovies(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("trending/movie/{time_window}")
    Call<MovieResponse> getTrendingMovies(
            @Path("time_window") String timeWindow,
            @Query("api_key") String apiKey
    );

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
    @Query("page") int page
    );


    @GET("movie/{movie_id}/images")
    Call<BackdropResponse> getMovieBackdrops(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<TVShowResponse> getPopularTVShows(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

}
