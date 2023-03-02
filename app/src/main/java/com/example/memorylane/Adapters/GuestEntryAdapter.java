package com.example.memorylane.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.GuestEntry;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.User;
import com.example.memorylane.GuestEntryDetailActivity;
import com.example.memorylane.PictureDetailActivity;
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
            Intent intent = new Intent(v.getContext(), GuestEntryDetailActivity.class);
            intent.putExtra(GuestEntryDetailActivity.KEY_GUEST_ENTRY, guestEntry);
            v.getContext().startActivity(intent);
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
            mGuestbookImage = itemView.findViewById(R.id.guestbook_image);
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
