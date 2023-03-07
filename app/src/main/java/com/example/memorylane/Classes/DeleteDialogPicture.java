package com.example.memorylane.Classes;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.FirebaseStorageInstance;
import com.example.memorylane.GuestbookActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;


public class DeleteDialogPicture extends AppCompatDialogFragment {

    private final Context context;
    private final UploadedPicture uploadedPicture;


    //Retrieves an ID of the matching diary entry, the EntryPictureAdapter and EntryPictureManager
    public DeleteDialogPicture(Context context, UploadedPicture uploadedPicture) {
        this.context = context;
        this.uploadedPicture = uploadedPicture;
    }


    /*
     * A dialog is opened up, which lets the user delete a specific picture when he clicks on the positive button
     * Adapter and manager of the images is here used to delete the specific image from the database and from the UI
     * */
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setCancelable(true)
                .setTitle("Entfernen")
                .setMessage("Bild entfernen?")
                .setNegativeButton("Nein", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(GuestbookActivity.guestbookID).child("uploadedImages");
                    databaseReference.child(uploadedPicture.getId()).removeValue();
                    StorageReference storageReference = FirebaseStorageInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(GuestbookActivity.guestbookID).child("uploadedImages");
                    storageReference.child(uploadedPicture.getId()).delete();
                    Toast.makeText(context, "Bild erfolgreich entfernt", Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }
}
