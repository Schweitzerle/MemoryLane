package com.example.memorylane;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.FirebaseStorageInstance;
import com.example.memorylane.Database.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class GuestbookCreationActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_SELECT_PICTURE = 201;
    MaterialButton createButton;
    ShapeableImageView imageView;
    TextInputEditText name, description;
    MaterialButton date;
    SwitchMaterial switchMaterial;

    ProgressDialog progressDialog;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guestbook_creation);
        initUI();
    }

    private void initUI() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();
        progressDialog = new ProgressDialog(GuestbookCreationActivity.this);
        switchMaterial = findViewById(R.id.guestbook_public_switch);
        name = findViewById(R.id.username);
        description = findViewById(R.id.email);
        date = findViewById(R.id.date);
        imageView = findViewById(R.id.guestbook_picture_input);
        imageView.setOnClickListener(this::choosePicture);
        createButton = findViewById(R.id.confirm_button);
        databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        storageReference = FirebaseStorageInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(GuestbookCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String dateString = day+"."+month+"."+year;
                        date.setText(dateString);
                    }
                }, year, month, day);
                dialog.show();
            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameString = name.getText().toString();
                String ageString = description.getText().toString();

                if (userNameString.isEmpty() || name.length() < 2) {
                    name.setError("Gästebuchname ist zu kurz!");
                } else if (ageString.isEmpty() || ageString.length() < 2) {
                    description.setError("Beschreibung ist zu kurz!");
                } else if (imageView.getDrawable() == null) {
                    Toast.makeText(GuestbookCreationActivity.this, "Bitte ein Profilbild auswählen!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Bitte warten während Gästebuch Erstellung...");
                    progressDialog.setTitle("Gästebuch wird erstellt");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    String imageString = getStringFromImage(imageView);

                    storeUserInformation(imageString, Objects.requireNonNull(name.getText()).toString(), imageString, description.getText().toString(), switchMaterial.isChecked());

                }
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


    private void storeUserInformation(String imageString, String name, String pictureUrl, String description, boolean isPublic) {

        // Create a unique file name for the picture
        String fileName = UUID.randomUUID().toString();
        // Get the data from the picture
        byte[] data = Base64.decode(imageString, Base64.DEFAULT);
        // Create a reference to the file in the storage
        StorageReference imageRef = storageReference.child(fileName).child("guestbookProfileImage");
        DatabaseReference gbRef = databaseReference.child(fileName);
        // Upload the picture to the storage
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        // Create a new guestbook with the user's information
                        Guestbook guestbook = new Guestbook(fileName,  name, imageUrl, description, UserSession.getInstance().getCurrentUser().getUid(), date.getText().toString(), isPublic);
                        // Store the guestbook in the database
                        gbRef.setValue(guestbook);
                        // Dismiss the progress dialog
                        progressDialog.dismiss();
                        // Navigate to the guestbook activity
                        if(getFragmentManager().getBackStackEntryCount() > 0) {
                            getFragmentManager().popBackStack();
                        }
                        else {
                            GuestbookCreationActivity.super.onBackPressed();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(GuestbookCreationActivity.this, "Fehler beim Upload in Database", Toast.LENGTH_SHORT).show();
                // Handle unsuccessful uploads
            }
        });
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
            // Update the ImageView
            imageView.setImageBitmap(imageBitmap);

        } else if (requestCode == REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // Update the ImageView
                imageView.setImageBitmap(imageBitmap);

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