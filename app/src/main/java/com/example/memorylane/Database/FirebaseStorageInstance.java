package com.example.memorylane.Database;

import com.google.firebase.storage.FirebaseStorage;

public class FirebaseStorageInstance {

    private static FirebaseStorageInstance instance;
    private final FirebaseStorage firebaseStorage;

    private FirebaseStorageInstance() {
        firebaseStorage = FirebaseStorage.getInstance("gs://memorylane-2739f.appspot.com");
    }



    public static FirebaseStorageInstance getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageInstance();
        }
        return instance;
    }

    public FirebaseStorage getFirebaseDatabase() {
        return firebaseStorage;
    }
}

