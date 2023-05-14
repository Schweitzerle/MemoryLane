package com.example.memorylane.Classes;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.google.firebase.database.DatabaseReference;


public class InvitationDecisionDialog extends AppCompatDialogFragment {

    private final Context context;
    private final GuestbookInvitation guestbookInvitation;


    //Retrieves an ID of the matching diary entry, the EntryPictureAdapter and EntryPictureManager
    public InvitationDecisionDialog(Context context, GuestbookInvitation guestbookInvitation) {
        this.context = context;
        this.guestbookInvitation = guestbookInvitation;
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
                    DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");
                    databaseReference.child(guestbookInvitation.getRecipientId()).child("invitations").child(guestbookInvitation.getInvitationID()).removeValue();
                    Toast.makeText(context, "Einladung abgelehnt", Toast.LENGTH_SHORT).show();

                    //TODO: Notification an User mit Ablehnung

                })
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");
                    DatabaseReference guestbookReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");

                    databaseReference.child(guestbookInvitation.getRecipientId()).child("MemberIn").child(guestbookInvitation.getGuestbookId()).setValue(guestbookInvitation.getGuestbookName());
                    databaseReference.child(guestbookInvitation.getRecipientId()).child("invitations").child(guestbookInvitation.getInvitationID()).removeValue();
                    guestbookReference.child(guestbookInvitation.getGuestbookId()).child("Members").child(guestbookInvitation.getRecipientId()).setValue(guestbookInvitation.getRecipientName());

                    Toast.makeText(context, "Anfrage angenommen", Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }



}
