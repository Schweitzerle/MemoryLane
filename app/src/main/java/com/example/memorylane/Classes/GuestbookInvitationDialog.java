package com.example.memorylane.Classes;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;


public class GuestbookInvitationDialog extends AppCompatDialogFragment {

    private final Context context;
    private final Guestbook guestbook;
    private final User invitedUser;


    //Retrieves an ID of the matching diary entry, the EntryPictureAdapter and EntryPictureManager
    public GuestbookInvitationDialog(Context context, Guestbook guestbook, User invitedUser) {
        this.context = context;
        this.guestbook = guestbook;
        this.invitedUser = invitedUser;
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
                .setTitle("Einladung")
                .setMessage("Einladung für dieses Gästebuch schicken?")
                .setNegativeButton("Nein", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Ja", (dialogInterface, i) -> {

                    sendJoinRequest();

                    Toast.makeText(context, "Anfrage erfolgreich versendet", Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }

    private void sendJoinRequest() {
        // Create a new join request
        GuestbookInvitation guestbookInvitation = new GuestbookInvitation(guestbook, UserSession.getInstance(), invitedUser);

        // Save the join request to the database
       FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference().child("Users").child(invitedUser.getUserSessionID()).child("invitations").child(guestbookInvitation.getInvitationID()).setValue(guestbookInvitation);

        // Send a notification to the guestbook owner
    }


}
