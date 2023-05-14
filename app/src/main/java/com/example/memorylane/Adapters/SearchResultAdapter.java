package com.example.memorylane.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorylane.Classes.GuestEntry;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.GuestbookInvitationDialog;
import com.example.memorylane.Classes.JoinGuestbookDialog;
import com.example.memorylane.Classes.User;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<User> userList;
    private Context context;

    public SearchResultAdapter(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
                guestbooksRef.child(GuestbookActivity.guestbookID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Guestbook guestbook = dataSnapshot.getValue(Guestbook.class);
                        GuestbookInvitationDialog guestbookInvitationDialog = new GuestbookInvitationDialog(context, guestbook, user);
                        guestbookInvitationDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Anfragen");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, uIDTextView;
        private ShapeableImageView shapeableImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.guest_name);
            uIDTextView = itemView.findViewById(R.id.guest_id);
            shapeableImageView = itemView.findViewById(R.id.user_image);
        }

        public void bind(User user) {
            nameTextView.setText(user.getUsername());
            uIDTextView.setText(user.getShortUID());
            byte[] decodedString = Base64.decode(user.getImageUrl(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            // set the signature to the ImageView
            shapeableImageView.setImageBitmap(decodedByte);
        }
    }
}
