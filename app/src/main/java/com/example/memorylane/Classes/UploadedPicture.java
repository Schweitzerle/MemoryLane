package com.example.memorylane.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class UploadedPicture implements Serializable {
   private String imagePath;
   private String description;
   private String id;
   private String uploaderID;


    public UploadedPicture() {
        // Default constructor required for calls to DataSnapshot.getValue(Guestbook.class)
    }

    public UploadedPicture(String imagePath, String description, String uploaderID) {
        this.id = UUID.randomUUID().toString();
        this.imagePath = imagePath;
        this.description = description;
        this.uploaderID = uploaderID;
    }

    public void setUploaderID(String uploaderID) {
        this.uploaderID = uploaderID;
    }

    public String getUploaderID() {
        return uploaderID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }


    public String getDescription() {
        return description;
    }
    //Gives back the bitmap of the image
    public Bitmap getBitmap(Context context) {
        Bitmap btmap = null;
        try {
            btmap = BitmapFactory.decodeStream(context.openFileInput(this.imagePath));
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return btmap;
    }

    //Is used to store the bitmap in an file using a string representation
    private String saveBitmapInFile(Bitmap bitmap, Context context) {
        String fileName = this.id;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

}
