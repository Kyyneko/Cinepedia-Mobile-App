package com.example.cinepedia.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cinepedia.R;
import com.example.cinepedia.activities.ExploreActivity;
import com.example.cinepedia.adapter.ComingSoonAdapter;
import com.example.cinepedia.adapter.HomeAdapter;
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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewMovies;
    private RecyclerView recyclerViewComingSoon;
    private HomeAdapter movieAdapter;
    private ComingSoonAdapter comingSoonAdapter;
    private final List<Movie> comingSoonList = new ArrayList<>();
    private final List<Movie> movieList = new ArrayList<>();
    private static final String API_KEY = "e4a25dbab6f3e22322f148efa2b77218";
    private int currentPage = 1;
    private static final int TOTAL_PAGES = 44275;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewMovies = view.findViewById(R.id.recycler_view_movies);
        recyclerViewComingSoon = view.findViewById(R.id.recycler_view_coming_soon);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonTrending = view.findViewById(R.id.button_trending);
        Button buttonTopRated = view.findViewById(R.id.button_top_rated);
        Button buttonUpcoming = view.findViewById(R.id.button_upcoming);
        Button buttonPlaying = view.findViewById(R.id.button_playing);

        buttonTrending.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExploreActivity.class);
            intent.putExtra("CATEGORY", "trending");
            startActivity(intent);
        });

        buttonTopRated.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExploreActivity.class);
            intent.putExtra("CATEGORY", "top_rated");
            startActivity(intent);
        });

        buttonUpcoming.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExploreActivity.class);
            intent.putExtra("CATEGORY", "upcoming");
            startActivity(intent);
        });

        buttonPlaying.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExploreActivity.class);
            intent.putExtra("CATEGORY", "playing_now");
            startActivity(intent);
        });

        movieAdapter = new HomeAdapter(getContext(), movieList);
        comingSoonAdapter = new ComingSoonAdapter(getContext(), comingSoonList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewMovies.setLayoutManager(gridLayoutManager);
        recyclerViewMovies.setAdapter(movieAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewComingSoon.setLayoutManager(layoutManager);
        recyclerViewComingSoon.setAdapter(comingSoonAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1; // Kembali ke halaman pertama
            movieList.clear(); // Bersihkan daftar film sebelum memuat yang baru
            loadMovies(currentPage);
        });

        recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (dy > 0 && !swipeRefreshLayout.isRefreshing()) {
                    if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                        if (currentPage < TOTAL_PAGES) {
                            currentPage++;
                            loadMovies(currentPage);
                        }
                    }
                }
            }
        });

        loadMovies(currentPage);
        loadComingSoonMovies(currentPage);
    }

    private void loadMovies(int page) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getPopularMovies(API_KEY, page).enqueue(new Callback<MovieResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.addAll(response.body().getMovies());
                    movieAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false); // Berhenti memuat saat selesai
                checkForEmptyList();
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false); // Berhenti memuat saat terjadi kesalahan
                checkForEmptyList();
            }
        });
    }

    private void loadComingSoonMovies(int page) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getUpcomingMovies(API_KEY, "en", page).enqueue(new Callback<MovieResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> newMovies = response.body().getMovies();
                    comingSoonList.addAll(newMovies);
                    comingSoonAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }


    private void checkForEmptyList() {
        new Thread(() -> {
            if (movieList.isEmpty()) {
                new Handler(Looper.getMainLooper()).post(() -> progressBar.setVisibility(View.VISIBLE));
            }
        }).start();
    }
}
