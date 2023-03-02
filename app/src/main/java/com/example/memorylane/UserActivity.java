package com.example.memorylane;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.User;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.FragmentsMain.SignaturePadFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_SELECT_PICTURE = 201;

    TextInputEditText nameEditText, ageEditText;
    ShapeableImageView profilePictureImageView, signature;
    MaterialButton saveButton;

    ProgressDialog progressDialog;

    AnimationDrawable drawable;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initUI();
    }

    private void initUI() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        progressDialog = new ProgressDialog(UserActivity.this);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();
        nameEditText = findViewById(R.id.username);
        ageEditText = findViewById(R.id.email);
        profilePictureImageView = findViewById(R.id.user_profile);
        signature = findViewById(R.id.signature);
        saveButton = findViewById(R.id.confirm_button);

        signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignaturePad();
            }
        });

        if (UserSession.getInstance().getCurrentUser() != null) {
            loadSignature();
        }

        profilePictureImageView.setOnClickListener(this::choosePicture);

        // Handle user input, such as when the user clicks on the save button or the user's signature is drawn
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString = nameEditText.getText().toString();
                String ageString = ageEditText.getText().toString();

                if (userNameString.isEmpty() || nameEditText.length() < 2) {
                    nameEditText.setError("Name ist zu kurz!");
                } else if (ageString.isEmpty()) {
                    ageEditText.setError("Alter ist leer!");
                } else if (profilePictureImageView.getDrawable() == null) {
                    Toast.makeText(UserActivity.this, "Bitte ein Profilbild auswählen!", Toast.LENGTH_SHORT).show();
                } else if (signature.getDrawable() == null) {
                    Toast.makeText(UserActivity.this, "Bitte eine Signatur hinzufügen!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Bitte warten während User Erstellung...");
                    progressDialog.setTitle("User wird erstellt");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    // Get the user's input from the UI elements
                    String name = nameEditText.getText().toString();
                    int age = Integer.parseInt(ageEditText.getText().toString());
                    String signatureString = getStringFromImage(signature);
                    String profilePictureString = getStringFromImage(profilePictureImageView);
                    // Create a new instance of the User class and set the user's information
                    user = new User(name, signatureString, age, profilePictureString);
                    // Store the user's information in the database
                    storeUserInformation(user);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    drawable.stop();
                    progressDialog.dismiss();
                    startActivity(intent);
                }

            }
        });
    }

    private void storeUserInformation(User user) {
        DatabaseReference userRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");
        userRef.child(UserSession.getInstance().getCurrentUser().getUid()).removeValue();
        // Store the user object in the database
        userRef.child(UserSession.getInstance().getCurrentUser().getUid()).setValue(user);
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
                    .into(profilePictureImageView);

        } else if (requestCode == REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // Update the ImageView
                Glide.with(this)
                        .load(imageBitmap)
                        .into(profilePictureImageView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openSignaturePad() {
        // Create a new DialogFragment to display the signature pad
        DialogFragment signaturePadFragment = new SignaturePadFragment();
        // Show the DialogFragment
        signaturePadFragment.show(getSupportFragmentManager(), "SignaturePadFragment");
    }

    private void loadSignature() {
        DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("signatureUrl");
        signatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String signatureString = dataSnapshot.getValue(String.class);
                if (signatureString!= null) {
                    byte[] decodedString = Base64.decode(signatureString, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    // set the signature to the ImageView
                    signature.setImageBitmap(decodedByte);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}