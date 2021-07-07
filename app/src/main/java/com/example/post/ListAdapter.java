package com.example.post;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder> implements ItemTouchHelperListener {

    ArrayList<SelectedImage> images = new ArrayList<>();

    public ListAdapter() {
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void addItem(SelectedImage selectedimage) {
        Log.i("Add Item: ", selectedimage.toString());
        images.add(selectedimage);
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        SelectedImage selectedimage = images.get(from_position);
        images.remove(from_position);
        images.add(to_position,selectedimage);
        notifyItemMoved(from_position, to_position);
        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        images.remove(position);
        notifyItemRemoved(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView list_image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            list_image = itemView.findViewById(R.id.list_image);
        }

        public void onBind(SelectedImage selectedimage) {
            list_image.setImageURI(selectedimage.getImage());
        }
    }
}
