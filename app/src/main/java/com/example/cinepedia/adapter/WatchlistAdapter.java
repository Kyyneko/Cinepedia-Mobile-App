package com.example.cinepedia.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepedia.R;
import com.example.cinepedia.activities.MovieDetailsActivity;
import com.example.cinepedia.helper.DatabaseHelper;
import com.example.cinepedia.models.Movie;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public WatchlistAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView overviewTextView;
        private ImageView posterImageView;
        private CardView cardMovie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_title);
            overviewTextView = itemView.findViewById(R.id.text_overview);
            posterImageView = itemView.findViewById(R.id.image_poster);
            cardMovie = itemView.findViewById(R.id.card);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());
            String ratingText = String.valueOf(movie.getVoteAverage());
            if (ratingText.length() > 3) {
                ratingText = ratingText.substring(0, 3);
            }
            overviewTextView.setText("Rating : " + ratingText);

            Glide.with(context).load("https://image.tmdb.org/t/p/w500"
                    + movie.getPosterPath()).into(posterImageView);

            cardMovie.setOnClickListener(v -> {
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
    }
}
