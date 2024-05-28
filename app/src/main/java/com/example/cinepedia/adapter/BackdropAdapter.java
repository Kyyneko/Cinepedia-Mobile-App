package com.example.cinepedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepedia.R;
import com.example.cinepedia.models.Backdrop;

import java.util.List;

public class BackdropAdapter extends RecyclerView.Adapter<BackdropAdapter.BackdropViewHolder> {

    private final Context context;
    private final List<Backdrop> backdropUrls;

    public BackdropAdapter(Context context, List<Backdrop> backdropUrls) {
        this.context = context;
        this.backdropUrls = backdropUrls;
    }

    @NonNull
    @Override
    public BackdropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_backdrops, parent, false);
        return new BackdropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BackdropViewHolder holder, int position) {
        Backdrop backdrop = backdropUrls.get(position);
        if (backdrop.getFilePath() != null && !backdrop.getFilePath().isEmpty()) {
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + backdrop.getFilePath())
                    .into(holder.backdropImage);
        } else {
            holder.backdropImage.setImageResource(R.drawable.ic_launcher_background); // Placeholder image if URL is null or empty
        }
    }


    @Override
    public int getItemCount() {
        return backdropUrls.size();
    }

    public static class BackdropViewHolder extends RecyclerView.ViewHolder {
        ImageView backdropImage;

        public BackdropViewHolder(@NonNull View itemView) {
            super(itemView);
            backdropImage = itemView.findViewById(R.id.backdrop_image);
        }
    }
}
