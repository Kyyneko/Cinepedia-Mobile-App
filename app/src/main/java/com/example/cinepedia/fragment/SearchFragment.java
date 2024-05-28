package com.example.cinepedia.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepedia.R;
import com.example.cinepedia.adapter.SearchAdapter;
import com.example.cinepedia.api.ApiClient;
import com.example.cinepedia.api.ApiService;
import com.example.cinepedia.models.Movie;
import com.example.cinepedia.responses.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerViewMovies;
    private SearchAdapter searchAdapter;
    private final List<Movie> allMovies = new ArrayList<>();
    private final List<Movie> filteredMovies = new ArrayList<>();
    private TextView noFilmTextView;
    private final Handler handler = new Handler();
    private static final long DEBOUNCE_TIMEOUT = 1000; // Waktu debouncing dalam milidetik
    private Runnable runnable;

    private static final String API_KEY = "e4a25dbab6f3e22322f148efa2b77218";
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private ProgressBar progressBar;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewMovies = view.findViewById(R.id.recycler_view_movies);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        noFilmTextView = view.findViewById(R.id.text_no_film);
        searchAdapter = new SearchAdapter(getContext(), filteredMovies);
        recyclerViewMovies.setAdapter(searchAdapter);
        recyclerViewMovies.setVisibility(View.GONE);

        initScrollListener();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }

                runnable = () -> {
                    if (TextUtils.isEmpty(newText)) {
                        recyclerViewMovies.setVisibility(View.GONE);
                        noFilmTextView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        recyclerViewMovies.setVisibility(View.GONE); // Sembunyikan RecyclerView saat query dimulai
                        progressBar.setVisibility(View.VISIBLE);
                        currentPage = 1;
                        allMovies.clear();
                        fetchMoviesFromApi(newText, currentPage);
                    }
                };

                handler.postDelayed(runnable, DEBOUNCE_TIMEOUT);

                return true;
            }
        });
    }

    private void initScrollListener() {
        recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && !isLastPage) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == allMovies.size() - 1) {
                        currentPage++;
                        fetchMoviesFromApi(((SearchView) getView().findViewById(R.id.search_view)).getQuery().toString(), currentPage);
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void fetchMoviesFromApi(final String query, final int page) {
        ApiService apiService = ApiClient.getApiService();
        apiService.searchMovies(API_KEY, query, page).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieResponse movieResponse = response.body();
                    List<Movie> movies = movieResponse.getMovies();
                    if (movies != null) {
                        allMovies.addAll(movies);
                        filterAndDisplayMovies(query); // Filter dan tampilkan hasil pencarian
                    }
                    isLoading = false;
                    if (page >= movieResponse.getTotalPages()) {
                        isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Penanganan kesalahan saat gagal mengambil data dari API
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                noFilmTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterAndDisplayMovies(String query) {
        filteredMovies.clear();
        if (TextUtils.isEmpty(query)) {
            filteredMovies.addAll(allMovies);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Movie movie : allMovies) {
                String lowerCaseTitle = movie.getTitle().toLowerCase();
                // Cek apakah query adalah substring dari judul atau sebaliknya
                if (lowerCaseTitle.contains(lowerCaseQuery) || lowerCaseQuery.contains(lowerCaseTitle)) {
                    filteredMovies.add(movie);
                }
            }
        }
        searchAdapter.notifyDataSetChanged(); // Perbarui tampilan dengan hasil pencarian yang difilter

        // Tampilkan pesan "No Film" jika tidak ada hasil pencarian
        if (filteredMovies.isEmpty()) {
            noFilmTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerViewMovies.setVisibility(View.VISIBLE); // Tampilkan RecyclerView setelah data dimuat
            noFilmTextView.setVisibility(View.GONE);
        }
    }
}
