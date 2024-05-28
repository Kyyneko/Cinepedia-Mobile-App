package com.example.cinepedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepedia.R;
import com.example.cinepedia.models.TVShow;

import java.util.List;

public class TVShowAdapter extends RecyclerView.Adapter<TVShowAdapter.TVViewHolder> {

    private final Context context;
    private final List<TVShow> tvShowList;

    public TVShowAdapter(Context context, List<TVShow> tvShowList) {
        this.context = context;
        this.tvShowList = tvShowList;
    }

    @NonNull
    @Override
    public TVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv, parent, false);
        return new TVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TVViewHolder holder, int position) {
        TVShow tvShow = tvShowList.get(position);
        holder.tvTitle.setText(tvShow.getTitle());
        Glide.with(context).load(tvShow.getImageUrl()).into(holder.tvImage);
    }

    @Override
    public int getItemCount() {
        return tvShowList.size();
    }

    static class TVViewHolder extends RecyclerView.ViewHolder {
        ImageView tvImage;
        TextView tvTitle;

        TVViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImage = itemView.findViewById(R.id.tv_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
