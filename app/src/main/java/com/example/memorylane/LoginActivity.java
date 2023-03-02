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
import com.google.android.gms.common.SignInButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    MaterialTextView newAccount;

    TextInputEditText email, password;
    MaterialButton confirmButton;
    SignInButton googleLogin;
    String emailValPattern = "[a-zA-Z0-9._-]+@[a-z-]+\\.+[a-z]+";

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String name;

    AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        confirmButton = findViewById(R.id.confirm_button);
        progressDialog = new ProgressDialog(LoginActivity.this);
        newAccount = findViewById(R.id.new_acc);
        googleLogin = findViewById(R.id.google_button);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        UserSession.getInstance().setCurrentUser(mUser);

        if (UserSession.getInstance().isUserLoggedIn()) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        } else {
            initButtons();
        }
    }

    private void initButtons() {
        confirmButton.setOnClickListener(v -> performLogin());

        googleLogin.setOnClickListener(v -> {
            Intent intent= new Intent(getBaseContext(), LoginGoogleActivity.class);
            startActivity(intent);
        });

        newAccount.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AuthenticationActivity.class)));
    }

    private void performLogin() {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();


        if (!emailString.matches(emailValPattern)) {
            email.setError("Keine korrekte Email");
        } else if (passwordString.isEmpty() || passwordString.length() < 6) {
            password.setError("Password erfüllt nicht den Anforderungen");

        } else {
            progressDialog.setMessage("Bitte warten während Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    sendUserToNextActivity();
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserSession.getInstance().setCurrentUser(user);

                    if (UserSession.getInstance().getCurrentUser() != null) {
                        DatabaseReference favoritesRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("User");

                        favoritesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                name = "";
                                for (DataSnapshot legoSetSnapshot : dataSnapshot.getChildren()) {
                                    name = legoSetSnapshot.child("User_Name").getValue(String.class);
                                    StyleableToast.makeText(getBaseContext(), "Wilkommen " + name + "!", R.style.customToastLoggedIn).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error
                            }
                        });
                    }


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Login fehlgeschlagen:" + task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
            );
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        drawable.stop();
        startActivity(intent);
    }

}