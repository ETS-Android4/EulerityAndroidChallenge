package com.example.eulerityandroidchallenge.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eulerityandroidchallenge.R;
import com.example.eulerityandroidchallenge.models.ImageObject;
import com.example.eulerityandroidchallenge.photoediting.EditImageActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *      Adapter for the RecyclerView that holds the images on MainActivity
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainAdapterHolder> {

    Context parentContext;
    List<ImageObject> list;

    public MainAdapter (Context parentContext, List<ImageObject> list) {
        this.parentContext = parentContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MainAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.recycler_adapter, parent, false);
        return new MainAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapterHolder holder, int position) {

        Picasso.get().load(list.get(position).getUrl()).resize(1000, 1000).centerInside().into(holder.image);

        holder.editImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(parentContext, EditImageActivity.class);
            intent.putExtra("imageObject", list.get(holder.getAdapterPosition()));
            parentContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MainAdapterHolder extends RecyclerView.ViewHolder {

        ImageView image;
        Button editImageButton;

        public MainAdapterHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.id_recycler_image);
            editImageButton = itemView.findViewById(R.id.id_recycler_editButton);
        }
    }

}
