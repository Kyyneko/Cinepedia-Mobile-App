package com.example.cinepedia.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepedia.R;
import com.example.cinepedia.adapter.CategoryAdapter;
import com.example.cinepedia.api.ApiClient;
import com.example.cinepedia.api.ApiService;
import com.example.cinepedia.models.Movie;
import com.example.cinepedia.responses.MovieResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreActivity extends AppCompatActivity {

    private TextView title;
    private CategoryAdapter categoryAdapter;
    private final List<Movie> movieList = new ArrayList<>();
    private static final String API_KEY = "e4a25dbab6f3e22322f148efa2b77218";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Mendapatkan kategori dari intent
        String category = getIntent().getStringExtra("CATEGORY");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_explore);
        title = findViewById(R.id.title);
        categoryAdapter = new CategoryAdapter(this, movieList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);

        int currentPage = 1;
        loadMoviesByCategory(category, currentPage);
    }

    @SuppressLint("SetTextI18n")
    private void loadMoviesByCategory(String category, int page) {
        ApiService apiService = ApiClient.getApiService();
        // Metode API untuk memuat film berdasarkan kategori yang dipilih
        Call<MovieResponse> call = null;
        switch (category) {
            case "trending":
                call = apiService.getTrendingMovies("day", API_KEY);
                title.setText("Trending Movies");
                break;
            case "top_rated":
                call = apiService.getTopRatedMovies(API_KEY, "en", page);
                title.setText("Top Rated Movies");
                break;
            case "upcoming":
                call = apiService.getUpcomingMovies(API_KEY, "en", page);
                title.setText("Upcoming Movies");
                break;
            case "playing_now":
                call = apiService.getNowPlayingMovies(API_KEY, "en", page);
                title.setText("Now Playing Movies");
                break;
        }

        if (call != null) {
            call.enqueue(new Callback<MovieResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> newMovies = response.body().getMovies();
                        movieList.addAll(newMovies);
                        categoryAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                    Log.e("API Error", t.getMessage());
                }
            });
        }
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
