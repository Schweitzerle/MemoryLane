package com.example.memorylane;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class AuthenticationActivity extends AppCompatActivity {

    MaterialTextView existingAccount;
    TextInputEditText email, password, passwordConfirm, username;
    MaterialButton confirmButton;
    String emailValPattern = "[a-zA-Z0-9._-]+@[a-z-]+\\.+[a-z]+";

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    AnimationDrawable drawable;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initUI();
    }

    private void initUI() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordConfirm = findViewById(R.id.password_confirm);
        confirmButton = findViewById(R.id.confirm_button);
        existingAccount = findViewById(R.id.existing_acc);
        username = findViewById(R.id.username);

        progressDialog = new ProgressDialog(AuthenticationActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        initButtons();
    }

    private void initButtons() {
        existingAccount.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), MainActivity.class)));

        confirmButton.setOnClickListener(v -> PerformAuthentication());
    }

    private void PerformAuthentication() {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        String passwordConfirmString = passwordConfirm.getText().toString();


        if (!emailString.matches(emailValPattern)) {
            email.setError("Keine korrekte Email");
        } else if (passwordString.isEmpty() || passwordString.length() < 6) {
            password.setError("Password erfüllt nicht den Anforderungen");
        } else if (!passwordString.matches(passwordConfirmString)) {
            passwordConfirm.setError("Passwörter stimmen nicht überein");
        } else {
            progressDialog.setMessage("Bitte warten während Registrierung...");
            progressDialog.setTitle("Registrierung");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();
                    UserSession.getInstance().setCurrentUser(user);
                    DatabaseReference favoritesRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("User");

                    DatabaseReference legoSetRef = favoritesRef.push();

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("User_Name", Objects.requireNonNull(username.getText()).toString());
                    legoSetRef.setValue(userData);
                    progressDialog.dismiss();


                    if (UserSession.getInstance().getCurrentUser() != null) {

                        favoritesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                name = "";
                                for (DataSnapshot legoSetSnapshot : dataSnapshot.getChildren()) {
                                    name = legoSetSnapshot.child("username").getValue(String.class);
                                    StyleableToast.makeText(getBaseContext(), "Wilkommen " + name + "!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error
                            }
                        });
                    }
                    sendUserToNextActivity();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Registrierung fehlgeschlagen:" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void sendUserToNextActivity() {

            // Get a reference to the Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");


            userRef.orderByValue().equalTo(UserSession.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // If the user's information does not exist, start the ProfileInformationActivity
                        Intent intent = new Intent(getBaseContext(), UserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        drawable.stop();
                        startActivity(intent);
                    }
                    else {
                        // If the user's information exists, continue to the main activity
                        // ...
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        drawable.stop();
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });


    }
}