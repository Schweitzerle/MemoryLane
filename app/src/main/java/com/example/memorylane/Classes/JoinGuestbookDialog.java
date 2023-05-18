package com.example.memorylane.Classes;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.FirebaseStorageInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.GuestbookActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class JoinGuestbookDialog extends AppCompatDialogFragment {

    private final Context context;
    private final Guestbook guestbook;

    private final User userSender;


    //Retrieves an ID of the matching diary entry, the EntryPictureAdapter and EntryPictureManager
    public JoinGuestbookDialog(Context context, Guestbook guestbook, User userSender) {
        this.context = context;
        this.guestbook = guestbook;
        this.userSender = userSender;
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
                .setMessage("Anfrage für dieses Gästebuch schicken?")
                .setNegativeButton("Nein", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Ja", (dialogInterface, i) -> {

                    sendJoinRequest(guestbook.getId(), guestbook.getCreatorId());

                    Toast.makeText(context, "Anfrage erfolgreich versendet", Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }

    private void sendJoinRequest(String guestbookId, String guestbookOwnerId) {
        // Create a new join request
        GuestbookRequest joinRequest = new GuestbookRequest(UserSession.getInstance(), guestbook, userSender);

        // Save the join request to the database
       FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference().child("Guestbooks").child(guestbookId).child("joinRequests").child(joinRequest.getRequestID()).setValue(joinRequest);

        // Send a notification to the guestbook owner
    }


}
