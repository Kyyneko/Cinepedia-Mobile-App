package com.example.cinepedia.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepedia.R;
import com.example.cinepedia.adapter.WatchlistAdapter;
import com.example.cinepedia.api.ApiClient;
import com.example.cinepedia.api.ApiService;
import com.example.cinepedia.helper.DatabaseHelper;
import com.example.cinepedia.models.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchlistActivity extends AppCompatActivity {

    private WatchlistAdapter watchlistAdapter;
    private List<Movie> movieList;
    private DatabaseHelper databaseHelper;
    private ApiService apiService;
    private static final String API_KEY = "e4a25dbab6f3e22322f148efa2b77218";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHelper = new DatabaseHelper(this);
        apiService = ApiClient.getApiService();
        movieList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_watchlist);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        watchlistAdapter = new WatchlistAdapter(this, movieList);
        recyclerView.setAdapter(watchlistAdapter);

        loadWatchlistData();
    }

    private void loadWatchlistData() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        // Mendapatkan ID pengguna berdasarkan nama pengguna yang login
        int userId = databaseHelper.getUserIdByUsername(username);

        // Mendapatkan data watchlist dari database berdasarkan ID pengguna
        Cursor cursor = databaseHelper.getWatchlistData(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int movieId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.getColumnMovieId()));

                // Menggunakan ID film untuk memanggil API dan mendapatkan detail film
                apiService.getMovieDetails(movieId, API_KEY).enqueue(new Callback<Movie>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Movie movie = response.body();
                            movieList.add(movie);
                            watchlistAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(WatchlistActivity.this, "Failed to get movie details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                        Toast.makeText(WatchlistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(this, "Watchlist is empty", Toast.LENGTH_SHORT).show();
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
