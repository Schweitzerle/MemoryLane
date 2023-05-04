package com.example.memorylane.Database;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.memorylane.Adapters.GuestbookAdapter;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

