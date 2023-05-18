package com.example.memorylane;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.ColorPaletteUtils;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.FirebaseStorageInstance;
import com.example.memorylane.Database.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.github.muddz.styleabletoast.StyleableToast;

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

    ConstraintLayout constraintLayout;
    AnimationDrawable animationDrawable;
    MaterialTextView header;

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
        constraintLayout = findViewById(R.id.constraint_layout);
        Bundle bundle = getIntent().getExtras();
        String guestbookID = bundle.getString(GUESTBOOK_ID_KEY);

        databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(guestbookID).child("uploadedImages");
        storageReference = FirebaseStorageInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(guestbookID).child("uploadedImages");

        getColorPaletteFromGBPicture();
        // Handle user input, such as when the user clicks on the save button or the user's signature is drawn
        saveEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString = editText.getText().toString();

                if (userNameString.isEmpty()) {
                    editText.setError("Beschreibung hinzufügen!");
                } else if (imageView.getDrawable() == null) {
                    StyleableToast.makeText(PictureInitActivity.this, "Bitte ein Bild hinzufügen!", R.style.customToast).show();
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

    private void getColorPaletteFromGBPicture() {

        if (ColorPaletteUtils.vibrantSwatch != null) {
            // Inflate the shape drawable from the XML file
            GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(getBaseContext(), R.drawable.bottom_corners_rounded);
            // Set a new color for the shape drawable
            shapeDrawable.setColor(ColorPaletteUtils.darkVibrantSwatch.getRgb());
            shapeDrawable.setPadding(20,20,20,20);
            // Set the new shape drawable as the background of a view

            header = findViewById(R.id.main_header_text);
            // Set a new color for the shape drawable
            header.setBackground(shapeDrawable);
            // Modify the start and end colors of anim1

            saveEntryButton.setBackgroundColor(ColorPaletteUtils.darkVibrantSwatch.getRgb());
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColors(new int[]{ColorPaletteUtils.lightVibrantSwatch.getRgb(),ColorPaletteUtils.lightMutedSwatch.getRgb()});


            // Modify the start and end colors of anim2
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setColors(new int[]{ColorPaletteUtils.vibrantSwatch.getRgb(), ColorPaletteUtils.darkMutedSwatch.getRgb()});

            // Modify the start and end colors of anim3
            GradientDrawable gradientDrawable1 = new GradientDrawable();
            gradientDrawable1.setColors(new int[]{ColorPaletteUtils.mutedSwatch.getRgb(), ColorPaletteUtils.darkVibrantSwatch.getRgb()});
            // Set the modified drawable resources as the background of the ConstraintLayout
            animationDrawable = new AnimationDrawable();
            animationDrawable.addFrame(gradientDrawable, 5000);
            animationDrawable.addFrame(gradientDrawable2, 5000);
            animationDrawable.addFrame(gradientDrawable1, 5000);
            constraintLayout.setBackground(animationDrawable);

            // Start the animation
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(2000);
            animationDrawable.start();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ColorPaletteUtils.darkVibrantSwatch.getRgb());
            }
        }
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

}