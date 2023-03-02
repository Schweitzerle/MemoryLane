package com.example.memorylane;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_table")
public class ImageFromGallery {

    @PrimaryKey
    @NonNull
    private Uri imageUri;

    public ImageFromGallery(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @NonNull
    public Uri getImageUri() {
        return imageUri;
    }

    public ImageFromGallery copy() {
        return new ImageFromGallery(imageUri);
    }
}
