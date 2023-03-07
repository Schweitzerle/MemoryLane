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


public class DeleteDialogEntry extends AppCompatDialogFragment {

    private final Context context;
    private final GuestEntry guestEntry;


    //Retrieves an ID of the matching diary entry, the EntryPictureAdapter and EntryPictureManager
    public DeleteDialogEntry(Context context, GuestEntry guestEntry) {
        this.context = context;
        this.guestEntry = guestEntry;
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
                .setMessage("Eintrag entfernen?")
                .setNegativeButton("Nein", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(GuestbookActivity.guestbookID).child("guestEntries");
                    databaseReference.child(guestEntry.getEntryID()).removeValue();
                    StorageReference storageReference = FirebaseStorageInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(GuestbookActivity.guestbookID).child("guestEntries");
                    storageReference.child(guestEntry.getEntryID()).delete();
                    Toast.makeText(context, "Eintrag erfolgreich entfernt", Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }
}
