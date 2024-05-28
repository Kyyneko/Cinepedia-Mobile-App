package com.example.cinepedia.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cinepedia.R;
import com.example.cinepedia.adapter.TVShowAdapter;
import com.example.cinepedia.api.ApiClient;
import com.example.cinepedia.api.ApiService;
import com.example.cinepedia.models.TVShow;
import com.example.cinepedia.responses.TVShowResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowFragment extends Fragment {

    private static final String API_KEY = "e4a25dbab6f3e22322f148efa2b77218";
    private static final int TOTAL_PAGES = 44275;
    private SwipeRefreshLayout swipeRefreshLayoutTv;
    private RecyclerView recyclerViewTv;
    private TVShowAdapter tvAdapter;
    private final List<TVShow> tvShowList = new ArrayList<>();
    private int currentTvPage = 1;
    private boolean isLoadingTvShows = false;

    public TVShowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tv_show, container, false);
        swipeRefreshLayoutTv = view.findViewById(R.id.swipe_refresh_layout_tv);
        recyclerViewTv = view.findViewById(R.id.recycler_view_tv);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayoutTv.setOnRefreshListener(() -> {
            tvShowList.clear();
            currentTvPage = 1;
            loadTvShows();
        });

        tvAdapter = new TVShowAdapter(getContext(), tvShowList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewTv.setLayoutManager(gridLayoutManager);
        recyclerViewTv.setAdapter(tvAdapter);


        recyclerViewTv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {
                    if (!isLoadingTvShows && (visibleItemCount + pastVisibleItems >= totalItemCount)) {
                        if (currentTvPage < TOTAL_PAGES) {
                            currentTvPage++;
                            loadTvShows();
                        }
                    }
                }
            }
        });
        loadTvShows();
    }


    private void loadTvShows() {
        isLoadingTvShows = true;
        ApiService apiService = ApiClient.getApiService();
        apiService.getPopularTVShows(API_KEY, "en", currentTvPage).enqueue(new Callback<TVShowResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<TVShowResponse> call, @NonNull Response<TVShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TVShow> newTvShows = response.body().getTvShows();
                    tvShowList.addAll(newTvShows);
                    tvAdapter.notifyDataSetChanged();
                }
                swipeRefreshLayoutTv.setRefreshing(false);
                isLoadingTvShows = false;
            }

            @Override
            public void onFailure(@NonNull Call<TVShowResponse> call, @NonNull Throwable t) {
                Log.e("API Error", Objects.requireNonNull(t.getMessage()));
                swipeRefreshLayoutTv.setRefreshing(false);
                isLoadingTvShows = false;
            }
        });
    }
}
