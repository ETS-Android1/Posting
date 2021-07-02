package com.example.post;

import android.net.Uri;

public class SelectedImage {
    Uri image;

    public SelectedImage(Uri image) {
        this.image = image;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
