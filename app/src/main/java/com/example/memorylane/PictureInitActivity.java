package com.example.memorylane;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.CustomProgressBarDialog;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.FirebaseStorageInstance;
import com.example.memorylane.Database.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PictureInitActivity extends AppCompatActivity {

    public static final String GUESTBOOK_ID_KEY = "KEY GUESTBOOK";
    ShapeableImageView imageView;
    TextInputEditText editText;
    MaterialButton saveEntryButton;

    AnimationDrawable drawable;

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_SELECT_PICTURE = 201;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    ProgressDialog progressDialog; //TODO: CUSTOM PROGRESS DIALOG


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }


    private void initUI() {
        setContentView(R.layout.activity_diary_picture_init);
        saveEntryButton = findViewById(R.id.save_entry_button);
        editText = findViewById(R.id.image_description_input);
        imageView = findViewById(R.id.init_gallery_image_preview);
        imageView.setOnClickListener(this::choosePicture);
        progressDialog = new ProgressDialog(PictureInitActivity.this);
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();
        Bundle bundle = getIntent().getExtras();
        String guestbookID = bundle.getString(GUESTBOOK_ID_KEY);

        databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(guestbookID).child("uploadedImages");
        storageReference = FirebaseStorageInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(guestbookID).child("uploadedImages");

        // Handle user input, such as when the user clicks on the save button or the user's signature is drawn
        saveEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString = editText.getText().toString();

                if (userNameString.isEmpty()) {
                    editText.setError("Beschreibung hinzufügen!");
                } else if (imageView.getDrawable() == null) {
                    Toast.makeText(PictureInitActivity.this, "Bitte ein Bild hinzufügen!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Bitte warten während Bild eingetragen wird...");
                    progressDialog.setTitle("Bild wird eingetragen...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    // Get the user's input from the UI elements
                    String name = editText.getText().toString();
                    String profilePictureString = getStringFromImage(imageView);
                    // Create a new instance of the User class and set the user's information
                    UploadedPicture uploadedPicture = new UploadedPicture(profilePictureString, name, UserSession.getInstance().getCurrentUser().getUid());
                    // Store the user's information in the database
                    storeUserInformation(uploadedPicture);

                }

            }
        });
    }


    private void storeUserInformation(UploadedPicture uploadedPicture) {

        // Get the data from the picture
        byte[] data = Base64.decode(uploadedPicture.getImagePath(), Base64.DEFAULT);
        // Create a reference to the file in the storage
        StorageReference imageRef = storageReference.child(uploadedPicture.getId());
        // Upload the picture to the storage
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download url for the picture
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Create a new entry for the picture in the real-time database
                        UploadedPicture uploadedPictureWithUrl = new UploadedPicture(uri.toString(), uploadedPicture.getDescription(), UserSession.getInstance().getCurrentUser().getUid());
                        uploadedPictureWithUrl.setId(uploadedPicture.getId());
                        databaseReference.child(uploadedPictureWithUrl.getId()).setValue(uploadedPictureWithUrl);

                        progressDialog.dismiss();
                        if (getFragmentManager().getBackStackEntryCount() > 0) {
                            getFragmentManager().popBackStack();
                        } else {
                            PictureInitActivity.super.onBackPressed();
                        }
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        });
    }

    private String getStringFromImage(ShapeableImageView view) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public void choosePicture(View view) {
        // Create a dialog to choose the source of the picture
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source")
                .setItems(R.array.picture_sources, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Camera
                                dispatchTakePictureIntent();
                                break;
                            case 1: // Gallery
                                dispatchSelectPictureIntent();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchSelectPictureIntent() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (selectPictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(selectPictureIntent, REQUEST_SELECT_PICTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Glide.with(this)
                    .load(imageBitmap)
                    .into(imageView);

        } else if (requestCode == REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // Update the ImageView
                Glide.with(this)
                        .load(imageBitmap)
                        .into(imageView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawable.stop();
    }
}