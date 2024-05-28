package com.example.cinepedia.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepedia.R;
import com.example.cinepedia.activities.MovieDetailsActivity;
import com.example.cinepedia.helper.DatabaseHelper;
import com.example.cinepedia.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MovieViewHolder> {

    private final Context context;
    private final List<Movie> movieList;

    public HomeAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList != null ? movieList : new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        String ratingText = String.valueOf(movie.getVoteAverage());
        if (ratingText.length() > 3) {
            ratingText = ratingText.substring(0, 3);
        }
        holder.rating.setText("Rating : " + ratingText);

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
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView posterImage;
        TextView title;
        TextView rating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            title = itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.rating); // Added rating TextView
        }
    }
}
