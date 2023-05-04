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

import com.example.memorylane.Classes.GuestbookRequest;
import com.example.memorylane.Classes.RequestDecisionDialog;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private List<GuestbookRequest> guestbookRequests;
    private OnGuestbookRequestClickListener mListener;

    public RequestAdapter(List<GuestbookRequest> guestbookRequests, OnGuestbookRequestClickListener listener) {
        this.guestbookRequests = guestbookRequests;
        this.mListener = listener;
    }

    public RequestAdapter(Context context, List<GuestbookRequest> guestbookRequests) {
        this.guestbookRequests = guestbookRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.guestbook_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuestbookRequest guestbookRequest = guestbookRequests.get(position);
        holder.bind(guestbookRequest);

        holder.itemView.setOnClickListener(v -> {
                RequestDecisionDialog requestDecisionDialog = new RequestDecisionDialog(context, guestbookRequest);
                requestDecisionDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Entfernen");
        });

    }

    @Override
    public int getItemCount() {
        return guestbookRequests.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ShapeableImageView mGuestbookImage;
        private final MaterialTextView mGuestbookName;

        public ViewHolder(View itemView) {
            super(itemView);
            mGuestbookImage = itemView.findViewById(R.id.user_image);
            mGuestbookName = itemView.findViewById(R.id.guest_name);
            itemView.setOnClickListener(this);
        }

        public void bind(GuestbookRequest guestbookRequest) {
            DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(guestbookRequest.getSenderId()).child("imageUrl");
            signatureRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String signature = dataSnapshot.getValue(String.class);
                    if (signature!= null) {
                        byte[] decodedString = Base64.decode(signature, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        // set the signature to the ImageView
                        mGuestbookImage.setImageBitmap(decodedByte);

                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
            DatabaseReference favoritesRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(guestbookRequest.getSenderId()).child("username");
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
            mListener.onGuestbookRequestClick(guestbookRequests.get(getAdapterPosition()));
        }
    }

    public interface OnGuestbookRequestClickListener {
        void onGuestbookRequestClick(GuestbookRequest guestbookRequest);
    }
}
