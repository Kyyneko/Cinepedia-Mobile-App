package com.example.cinepedia.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepedia.R;
import com.example.cinepedia.activities.MovieDetailsActivity;
import com.example.cinepedia.helper.DatabaseHelper;
import com.example.cinepedia.models.Movie;

import java.util.List;

public class RecommendedMovieAdapter extends RecyclerView.Adapter<RecommendedMovieAdapter.RecommendedMovieViewHolder> {

    private final Context context;
    private final List<Movie> recommendedMovies;

    public RecommendedMovieAdapter(Context context, List<Movie> recommendedMovies) {
        this.context = context;
        this.recommendedMovies = recommendedMovies;
    }

    @NonNull
    @Override
    public RecommendedMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommended_movie, parent, false);
        return new RecommendedMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedMovieViewHolder holder, int position) {
        Movie movie = recommendedMovies.get(position);
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(holder.posterImage);

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", null);
            if (username != null) {
                // Jika username ditemukan, cek user_id dari database berdasarkan username
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                int userId = databaseHelper.getUserIdByUsername(username);

                // Jika user_id valid, buka MovieDetailsActivity dengan user_id dan movie_id sebagai extra
                if (userId != -1) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("movie_id", movie.getId());
                    intent.putExtra("poster_path", movie.getPosterPath());
                    intent.putExtra("title_movie", movie.getTitle());
                    context.startActivity(intent);
                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return recommendedMovies.size();
    }

    public static class RecommendedMovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;

        public RecommendedMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.recommended_movie_poster);
        }
    }
}
