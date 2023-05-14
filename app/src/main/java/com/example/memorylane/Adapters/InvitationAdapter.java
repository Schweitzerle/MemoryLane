package com.example.memorylane.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.GuestbookInvitation;
import com.example.memorylane.Classes.InvitationDecisionDialog;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {

    private Context context;
    private final List<GuestbookInvitation> guestbookInvitations;
    private OnGuestbookInvitationClickListener mListener;

    public InvitationAdapter(List<GuestbookInvitation> guestbookInvitations, OnGuestbookInvitationClickListener listener) {
        this.guestbookInvitations = guestbookInvitations;
        this.mListener = listener;
    }

    public InvitationAdapter(Context context, List<GuestbookInvitation> guestbookInvitations) {
        this.guestbookInvitations = guestbookInvitations;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invitation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuestbookInvitation guestbookInvitation = guestbookInvitations.get(position);
        holder.bind(guestbookInvitation);

        holder.itemView.setOnClickListener(v -> {
                InvitationDecisionDialog invitationDecisionDialog = new InvitationDecisionDialog(context, guestbookInvitation);
                invitationDecisionDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Entfernen");
        });

    }

    @Override
    public int getItemCount() {
        return guestbookInvitations.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ShapeableImageView mGuestbookImage;
        private final MaterialTextView mGuestbookName;

        public ViewHolder(View itemView) {
            super(itemView);
            mGuestbookImage = itemView.findViewById(R.id.guestbook_image);
            mGuestbookName = itemView.findViewById(R.id.guestbook_name);
            itemView.setOnClickListener(this);
        }

        public void bind(GuestbookInvitation guestbookInvitation) {
            DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(guestbookInvitation.getGuestbookId()).child("pictureUrl");
            signatureRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String signature = dataSnapshot.getValue(String.class);
                    if (signature!= null) {
                        Glide.with(context).load(signature).into(mGuestbookImage);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
            DatabaseReference favoritesRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(guestbookInvitation.getGuestbookId()).child("name");
            favoritesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String signature = dataSnapshot.getValue(String.class);
                    if (signature!= null) {
                        mGuestbookName.setText(signature);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onGuestbookRequestClick(guestbookInvitations.get(getAdapterPosition()));
        }
    }

    public interface OnGuestbookInvitationClickListener {
        void onGuestbookRequestClick(GuestbookInvitation guestbookInvitation);
    }
}
