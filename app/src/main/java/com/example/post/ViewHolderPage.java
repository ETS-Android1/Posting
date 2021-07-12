package com.example.post;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class ViewHolderPage extends RecyclerView.ViewHolder {
    private ImageView tv_img;
    DataPage data;

    ViewHolderPage(View itemView) {
        super(itemView);
        tv_img = itemView.findViewById(R.id.tv_img);
    }

    public void onBind(Context context, DataPage data){
        this.data = data;
        Glide.with(context).load(data.getImgResource()).into(tv_img);
    }
}
