package com.example.memorylane.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.DeleteDialogEntry;
import com.example.memorylane.Classes.GuestEntry;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Classes.User;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GuestEntryAdapter extends RecyclerView.Adapter<GuestEntryAdapter.ViewHolder> {

    private Context context;
    private List<GuestEntry> guestEntries;


    public GuestEntryAdapter(Context context, List<GuestEntry> guestEntries) {
        this.guestEntries = guestEntries;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.guest_entry_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuestEntry guestEntry = guestEntries.get(position);
        holder.bind(guestEntry);

        holder.itemView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.entry_detail_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loadUI(dialog, guestEntry);
            dialog.show();
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (UserSession.getInstance().getCurrentUser().getUid().equals(guestEntry.getUserID())) {
                DeleteDialogEntry deleteDialogEntry = new DeleteDialogEntry(context, guestEntry);
                deleteDialogEntry.show(((AppCompatActivity) context).getSupportFragmentManager(), "Entfernen");
                return false;
            } else Toast.makeText(context, "Nur eigene Einträge sind bearbeitbar", Toast.LENGTH_SHORT).show();

            return true;

        });
    }

    private void loadUI(Dialog dialog, GuestEntry guestEntry) {
        ShapeableImageView signatureImage = dialog.findViewById(R.id.signature);
        ImageView imageView = dialog.findViewById(R.id.user_image);
        TextView age = dialog.findViewById(R.id.age);
        TextView name = dialog.findViewById(R.id.guest_name);
        TextView entryText = dialog.findViewById(R.id.entryText);
        entryText.setText(guestEntry.getDescription());
        loadSignature(guestEntry.getUserID(), signatureImage);
        DatabaseReference userRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");
        userRef.child(guestEntry.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getUsername());
                age.setText(String.valueOf(user.getAge()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


        Glide.with(context).load(guestEntry.getPictureURL()).into(imageView);
    }

    private void loadSignature(String userID, ShapeableImageView signatureImage) {
        DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(userID).child("signatureUrl");
        signatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String signature = dataSnapshot.getValue(String.class);
                if (signature!= null) {
                    byte[] decodedString = Base64.decode(signature, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    // set the signature to the ImageView
                    signatureImage.setImageBitmap(decodedByte);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public int getItemCount() {
        return guestEntries.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView mGuestbookImage;
        private final MaterialTextView mGuestbookName;

        public ViewHolder(View itemView) {
            super(itemView);
            mGuestbookImage = itemView.findViewById(R.id.user_image);
            mGuestbookName = itemView.findViewById(R.id.guest_name);
        }

        public void bind(GuestEntry guestEntry) {
            DatabaseReference guestbookRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(guestEntry.getUserID());

            guestbookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Extract the guestbook data from the snapshot
                User user = dataSnapshot.getValue(User.class);
                Glide.with(itemView.getContext()).load(guestEntry.getPictureURL()).into(mGuestbookImage);
                mGuestbookName.setText(user.getUsername());
                // Do something with the guestbook data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", "Error getting guestbook", databaseError.toException());
            }
        });

        }


    }

    public interface OnGuestEntryClickListener {
        void onGuestbookClick(GuestEntry guestbook);
    }
}
