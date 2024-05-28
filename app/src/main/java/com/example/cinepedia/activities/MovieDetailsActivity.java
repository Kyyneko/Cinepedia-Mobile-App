package com.example.cinepedia.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepedia.R;
import com.example.cinepedia.adapter.BackdropAdapter;
import com.example.cinepedia.adapter.RecommendedMovieAdapter;
import com.example.cinepedia.api.ApiClient;
import com.example.cinepedia.api.ApiService;
import com.example.cinepedia.helper.DatabaseHelper;
import com.example.cinepedia.models.Backdrop;
import com.example.cinepedia.models.Genre;
import com.example.cinepedia.models.Movie;
import com.example.cinepedia.models.ProductionCompany;
import com.example.cinepedia.models.ProductionCountry;
import com.example.cinepedia.models.Video;
import com.example.cinepedia.responses.BackdropResponse;
import com.example.cinepedia.responses.MovieResponse;
import com.example.cinepedia.responses.VideoResponse;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView posterImage, no_poster, noBackdrop;
    private TextView title, overview, releaseDate, rating,
            genres, status, originalLanguage, productionCompanies,
            productionCountries, homepage, tagline, runtime;
    private RecyclerView recommendedMoviesRecyclerView;
    private RecommendedMovieAdapter recommendedMovieAdapter;
    private List<Movie> recommendedMovies;
    private RecyclerView backdropRecyclerView;
    private final DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private BackdropAdapter backdropAdapter;
    private YouTubePlayerView youtubePlayerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        youtubePlayerView = findViewById(R.id.youtube_player_view);
        runtime = findViewById(R.id.runtime);
        noBackdrop = findViewById(R.id.noBackdrops_recycler_view);
        posterImage = findViewById(R.id.poster_image);
        no_poster = findViewById(R.id.no_poster_image);
        title = findViewById(R.id.title);
        overview = findViewById(R.id.overview);
        releaseDate = findViewById(R.id.release_date);
        rating = findViewById(R.id.rating);
        genres = findViewById(R.id.genres);
        status = findViewById(R.id.status);
        originalLanguage = findViewById(R.id.original_language);
        productionCompanies = findViewById(R.id.production_companies);
        productionCountries = findViewById(R.id.production_countries);
        homepage = findViewById(R.id.homepage);
        tagline = findViewById(R.id.tagline);

        backdropRecyclerView = findViewById(R.id.backdrops_recycler_view);
        backdropRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recommendedMoviesRecyclerView = findViewById(R.id.recommended_movies_recycler_view);
        recommendedMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Button addToWatchlistButton = findViewById(R.id.add_to_watchlist_button);
        Button deleteToWatchlistButton = findViewById(R.id.delete_to_watchlist_button);
        Intent intent = getIntent();


        deleteToWatchlistButton.setOnClickListener(v -> {
            int userId = intent.getIntExtra("user_id", -1);
            int movieId = getIntent().getIntExtra("movie_id", -1);

            if (userId != -1 && movieId != -1) {
                boolean success = databaseHelper.deleteMovieFromWatchlist(userId, movieId);
                if (success) {
                    Toast.makeText(MovieDetailsActivity.this, "Movie deleted from watchlist", Toast.LENGTH_SHORT).show();
                    deleteToWatchlistButton.setVisibility(View.GONE);
                    addToWatchlistButton.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MovieDetailsActivity.this, "Failed to delete movie from watchlist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MovieDetailsActivity.this, "Invalid user ID or movie ID", Toast.LENGTH_SHORT).show();
            }
        });


        addToWatchlistButton.setOnClickListener(v -> {
            int userId = intent.getIntExtra("user_id", -1);

            if (userId != -1) {
                int movieId = intent.getIntExtra("movie_id", -1);
                String title = intent.getStringExtra("title_movie");
                String posterPath = intent.getStringExtra("poster_path");

                if (movieId != -1) {
                    addToWatchlist(movieId, userId, title, posterPath);
                    addToWatchlistButton.setVisibility(View.GONE);
                    deleteToWatchlistButton.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MovieDetailsActivity.this, "Invalid movie ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MovieDetailsActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId != -1) {
            fetchMovieDetails(movieId);
            fetchRecommendedMovies(movieId);
            fetchBackdropImages(movieId);
            fetchMovieVideos(movieId);
        }

        addToWatchlistButton.setVisibility(View.VISIBLE);
        // Dapatkan ID pengguna dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int userId = 0;
        if (username != null) {
            // Jika username ditemukan, cek user_id dari database berdasarkan username

            userId = databaseHelper.getUserIdByUsername(username);
        }

        if (userId != -1) {
            // Lakukan query ke database untuk memeriksa apakah film tersebut sudah ada di watchlist pengguna
            boolean isInWatchlist = databaseHelper.isMovieInWatchlist(userId, movieId);

            // Jika film sudah ada di watchlist, atur visibilitas tombol "Add To Watchlist" menjadi GONE
            if (isInWatchlist) {
                addToWatchlistButton.setVisibility(View.GONE);
                deleteToWatchlistButton.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToWatchlist(int movieId, int userId, String title, String posterPath) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
         boolean success = databaseHelper.addWatchlist(userId, movieId, title, posterPath);
        if (success) {
            Toast.makeText(this, "Movie added to watchlist", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add movie to watchlist", Toast.LENGTH_SHORT).show();
        }
    }



    private void fetchMovieDetails(int movieId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getMovieDetails(movieId, "e4a25dbab6f3e22322f148efa2b77218").enqueue(new Callback<Movie>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();

                    if (movieId == movie.getId()) {
                        title.setText(movie.getTitle());
                        overview.setText(movie.getOverview());



                        Boolean adult = movie.isAdult();
                        if (adult) {
                            // Mengubah format tanggal
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            try {
                                Date date = inputFormat.parse(movie.getReleaseDate());
                                String formattedDate = outputFormat.format(date);
                                releaseDate.setText("Release Date : " + formattedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                releaseDate.setText("Release Date : " + movie.getReleaseDate() + "18+ Content");
                            }
                        } else {
                            // Mengubah format tanggal
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            try {
                                Date date = inputFormat.parse(movie.getReleaseDate());
                                String formattedDate = outputFormat.format(date);
                                releaseDate.setText("Release Date : " + formattedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                releaseDate.setText("Release Date : " + movie.getReleaseDate());
                            }
                        }

                        // Menampilkan rating dengan format yang diinginkan
                        String ratingText = String.valueOf(movie.getVoteAverage());
                        if (ratingText.length() > 3) {
                            ratingText = ratingText.substring(0, 3);
                        }
                        rating.setText("Rating : " + ratingText);

                        // Memeriksa status film
                        if (movie.getStatus() != null && !movie.getStatus().isEmpty()) {
                            status.setText(movie.getStatus());
                        } else {
                            status.setText("-");
                        }

                        // Menampilkan bahasa asli
                        if (movie.getOriginalLanguage() != null && !movie.getOriginalLanguage().isEmpty()) {
                            originalLanguage.setText(movie.getOriginalLanguage());
                        } else {
                            originalLanguage.setText("-");
                        }

                        // Menampilkan tagline
                        if (movie.getTagline() != null && !movie.getTagline().isEmpty()) {
                            tagline.setText(movie.getTagline());
                        } else {
                            tagline.setText("-");
                        }

                        runtime.setText("Runtime : " + movie.getRuntime() + " minutes");

                        // Menampilkan genre
                        StringBuilder genresBuilder = new StringBuilder();
                        for (Genre genre : movie.getGenres()) {
                            genresBuilder.append(genre.getName()).append(", ");
                        }
                        String genresText = genresBuilder.toString();
                        genres.setText(genresText.isEmpty() ? "-" : "Genres : " + genresText);

                        // Menampilkan perusahaan produksi
                        StringBuilder productionCompaniesBuilder = new StringBuilder();
                        for (ProductionCompany company : movie.getProductionCompanies()) {
                            productionCompaniesBuilder.append(company.getName()).append(", ");
                        }
                        String productionCompaniesText = productionCompaniesBuilder.toString();
                        productionCompanies.setText(productionCompaniesText.isEmpty() ? "-" : productionCompaniesText);

                        // Menampilkan negara produksi
                        StringBuilder productionCountriesBuilder = new StringBuilder();
                        for (ProductionCountry country : movie.getProductionCountries()) {
                            productionCountriesBuilder.append(country.getName()).append(", ");
                        }
                        String productionCountriesText = productionCountriesBuilder.toString();
                        productionCountries.setText(productionCountriesText.isEmpty() ? "-" : productionCountriesText);

                        // Menampilkan homepage
                        if (movie.getHomepage() != null && !movie.getHomepage().isEmpty()) {
                            homepage.setText(movie.getHomepage());
                        } else {
                            homepage.setText("-");
                        }

                        // Menampilkan poster film
                        if (movie.getPosterPath() != null) {
                            no_poster.setVisibility(View.GONE);
                            Glide.with(MovieDetailsActivity.this)
                                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                                    .into(posterImage);
                        } else {
                            no_poster.setVisibility(View.VISIBLE);
                            posterImage.setVisibility(View.GONE);
                        }
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void fetchRecommendedMovies(int movieId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getRecommendedMovies(movieId, "e4a25dbab6f3e22322f148efa2b77218").enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedMovies = response.body().getMovies();
                    recommendedMovieAdapter = new RecommendedMovieAdapter(MovieDetailsActivity.this, recommendedMovies);
                    recommendedMoviesRecyclerView.setAdapter(recommendedMovieAdapter);
                } else {
                    Log.e("API Error", "Response not successful or body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void fetchBackdropImages(int movieId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getMovieBackdrops(movieId, "e4a25dbab6f3e22322f148efa2b77218").enqueue(new Callback<BackdropResponse>() {
            @Override
            public void onResponse(@NonNull Call<BackdropResponse> call, @NonNull Response<BackdropResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBackdrops() != null) {
                    List<Backdrop> backdropUrls = response.body().getBackdrops();
                    if (backdropUrls.isEmpty()) {
                        backdropRecyclerView.setVisibility(View.GONE);
                        noBackdrop.setVisibility(View.VISIBLE);
                    } else {
                        backdropAdapter = new BackdropAdapter(MovieDetailsActivity.this, backdropUrls);
                        backdropRecyclerView.setAdapter(backdropAdapter);
                        noBackdrop.setVisibility(View.GONE);
                        backdropRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    backdropRecyclerView.setVisibility(View.GONE);
                    noBackdrop.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BackdropResponse> call, @NonNull Throwable t) {
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
                backdropRecyclerView.setVisibility(View.GONE);
                noBackdrop.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchMovieVideos(int movieId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getMovieVideos(movieId, "e4a25dbab6f3e22322f148efa2b77218").enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Video> videos = response.body().getVideos();
                    for (Video video : videos) {
                        if (video.getSite().equalsIgnoreCase("YouTube")) {
                            youtubePlayerView.setVisibility(View.VISIBLE);
                            getLifecycle().addObserver(youtubePlayerView);
                            youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                @Override
                                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                    youTubePlayer.loadVideo(video.getKey(), 0);
                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Memanggil metode onBackPressed untuk kembali ke fragment sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Jika Anda ingin melakukan sesuatu saat kembali ke fragment sebelumnya, tambahkan kode di sini
    }
}
