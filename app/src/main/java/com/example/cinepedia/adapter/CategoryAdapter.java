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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepedia.R;
import com.example.cinepedia.activities.MovieDetailsActivity;
import com.example.cinepedia.helper.DatabaseHelper;
import com.example.cinepedia.models.Movie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> movieList;

    public CategoryAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.bind(movie, position + 1);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImage;
        private final TextView title;
        private final TextView release;
        private final TextView rating;
        private final TextView number;
        private final CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.image_view_poster);
            title = itemView.findViewById(R.id.text_view_title);
            release = itemView.findViewById(R.id.text_view_release);
            rating = itemView.findViewById(R.id.text_view_rating);
            number = itemView.findViewById(R.id.text_view_number);
            card = itemView.findViewById(R.id.card_category);


        }

        @SuppressLint("SetTextI18n")
        public void bind(Movie movie, int position) {
            title.setText(movie.getTitle());
            String releaaseDate = formatDate(movie.getReleaseDate());
            release.setText("Release Date : " + releaaseDate);

            String ratingText = String.valueOf(movie.getVoteAverage());
            if (ratingText.length() > 3) {
                ratingText = ratingText.substring(0, 3);
            }
            rating.setText("Rating : " + ratingText);

            number.setText(String.valueOf(position));

            if (movie.getBackdropPath() != null) {
                Glide.with(context)
                        .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                        .into(posterImage);
            }

            card.setOnClickListener(v -> {
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
    }

    private String formatDate(String originalDate) {
        try {
            SimpleDateFormat sdfOriginal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdfOriginal.parse(originalDate);

            SimpleDateFormat sdfNew = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sdfNew.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return originalDate;
        }
    }

}
