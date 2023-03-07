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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class DeleteDialogGuestbook extends AppCompatDialogFragment {

    private final Context context;
    private final Guestbook guestbook;


    //Retrieves an ID of the matching diary entry, the EntryPictureAdapter and EntryPictureManager
    public DeleteDialogGuestbook(Context context, Guestbook guestbook) {
        this.context = context;
        this.guestbook = guestbook;
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
                .setMessage("Gästebuch entfernen?")
                .setNegativeButton("Nein", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
                    databaseReference.child(guestbook.getId()).removeValue();
                    StorageReference storageReference = FirebaseStorageInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
                    storageReference.child(guestbook.getId()).listAll().addOnSuccessListener(listResult -> {
                                storageReference.child(guestbook.getId()).child("uploadedImages").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        List<StorageReference> files = listResult.getItems();
                                        for (StorageReference file : files) {
                                            file.delete().addOnSuccessListener(success -> {
                                                // Datei wurde gelöscht
                                            }).addOnFailureListener(exception -> {
                                                // Fehler beim Löschen der Datei
                                            });
                                        }
                                    }
                                });

                                storageReference.child(guestbook.getId()).child("guestEntries").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        List<StorageReference> files = listResult.getItems();
                                        for (StorageReference file : files) {
                                            file.delete().addOnSuccessListener(success -> {
                                                // Datei wurde gelöscht
                                            }).addOnFailureListener(exception -> {
                                                // Fehler beim Löschen der Datei
                                            });
                                        }
                                    }
                                });

                                List<StorageReference> files = listResult.getItems();
                                for (StorageReference file : files) {
                                    file.delete().addOnSuccessListener(success -> {
                                        // Datei wurde gelöscht
                                        Toast.makeText(context, "Gästebuch erfolgreich entfernt", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(exception -> {
                                        // Fehler beim Löschen der Datei
                                    });
                                }
                            })
                            .addOnFailureListener(exception -> {
                                // Fehler beim Lesen des Ordners
                            });
                    ;
                });
        return builder.create();
    }

}
