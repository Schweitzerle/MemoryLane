package com.example.memorylane.Classes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.google.firebase.database.DatabaseReference;


public class RequestDecisionDialog extends AppCompatDialogFragment {

    private final Context context;
    private final GuestbookRequest guestbookRequest;


    //Retrieves an ID of the matching diary entry, the EntryPictureAdapter and EntryPictureManager
    public RequestDecisionDialog(Context context, GuestbookRequest guestbookRequest) {
        this.context = context;
        this.guestbookRequest = guestbookRequest;
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
                .setTitle("Beitrittsanfrage")
                .setMessage("Anfrage für dieses Gästebuch annehmen?")
                .setNegativeButton("Nein", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
                    databaseReference.child(guestbookRequest.getGuestbookId()).child("joinRequests").child(guestbookRequest.getRequestID()).removeValue();
                    Toast.makeText(context, "Anfrage abgelehnt", Toast.LENGTH_SHORT).show();

                    //TODO: Notification an User mit Ablehnung

                })
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
                    databaseReference.child(guestbookRequest.getGuestbookId()).child("Members").child(guestbookRequest.getSenderId()).setValue(guestbookRequest.getSenderId());

                    databaseReference.child(guestbookRequest.getGuestbookId()).child("joinRequests").child(guestbookRequest.getRequestID()).removeValue();
                    Toast.makeText(context, "Anfrage angenommen", Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }



}
