package com.example.memorylane.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSession {
    private static UserSession instance;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private UserSession(){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser(); }

    public static UserSession getInstance(){
        if(instance == null)
            instance = new UserSession();
        return instance;
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }
}

