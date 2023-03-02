package com.example.memorylane.Database;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseInstance {
        private static FirebaseDatabaseInstance instance;
        private final FirebaseDatabase firebaseDatabase;

        private FirebaseDatabaseInstance() {
            firebaseDatabase = FirebaseDatabase.getInstance("https://memorylane-2739f-default-rtdb.europe-west1.firebasedatabase.app/");
        }



        public static FirebaseDatabaseInstance getInstance() {
            if (instance == null) {
                instance = new FirebaseDatabaseInstance();
            }
            return instance;
        }

        public FirebaseDatabase getFirebaseDatabase() {
            return firebaseDatabase;
        }
    }

