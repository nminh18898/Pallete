package com.nhatminh.example.pallete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    Context context;
    List<Integer> resName;

    public ImageAdapter(Context context, List<Integer> resName) {
        this.context = context;
        this.resName = resName;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.bind(resName.get(position));
    }

    @Override
    public int getItemCount() {
        return resName.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView ivThumbnail;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);

        }

        public void bind(int resourceName){
            Glide.with(context)
                    .load(resourceName)
                    .centerInside()
                    .into(ivThumbnail);
        }


    }

}
