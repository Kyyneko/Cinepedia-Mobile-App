package com.example.cinepedia.adapter;

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

public class ComingSoonAdapter extends RecyclerView.Adapter<ComingSoonAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> comingSoonList;

    public ComingSoonAdapter(Context context, List<Movie> comingSoonList) {
        this.context = context;
        this.comingSoonList = comingSoonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_comingsoon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = comingSoonList.get(position);
        holder.title.setText(movie.getTitle());

        String releaseDate = formatDate(movie.getReleaseDate());
        holder.release.setText(releaseDate);

        if (movie.getBackdropPath() != null) {
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + movie.getBackdropPath())
                    .into(holder.posterImage);
        }

        holder.cardView.setOnClickListener(v -> {
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
        return comingSoonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView title;
        TextView release;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            title = itemView.findViewById(R.id.title);
            release = itemView.findViewById(R.id.release);
            cardView = itemView.findViewById(R.id.coming_soon);
        }
    }

    // Method untuk mengubah format tanggal
    private String formatDate(String originalDate) {
        try {
            // Ubah tanggal ke format Date
            SimpleDateFormat sdfOriginal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdfOriginal.parse(originalDate);

            // Format tanggal menjadi "dd-MM-yyyy"
            SimpleDateFormat sdfNew = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sdfNew.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return originalDate; // Jika gagal, kembalikan format asli
        }
    }
}
