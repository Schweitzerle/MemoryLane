package com.example.memorylane.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.DeleteDialogGuestbook;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.JoinGuestbookDialog;
import com.example.memorylane.Classes.User;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GuestbookAdapter extends RecyclerView.Adapter<GuestbookAdapter.ViewHolder> {

    private Context context;
    private List<Guestbook> mGuestbooks;
    private OnGuestbookClickListener mListener;

    public GuestbookAdapter(List<Guestbook> guestbooks, OnGuestbookClickListener listener) {
        this.mGuestbooks = guestbooks;
        this.mListener = listener;
    }

    public GuestbookAdapter(Context context, List<Guestbook> guestbooks) {
        this.mGuestbooks = guestbooks;
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
        Guestbook guestbook = mGuestbooks.get(position);
        holder.bind(guestbook);

        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        guestbooksRef.child(guestbook.getId()).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isMember = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String memberId = snapshot.getKey();
                    if (memberId != null && (memberId.equals(UserSession.getInstance().getCurrentUser().getUid()))) {
                        isMember = true;
                        break;
                    }
                }

                boolean isCreator = UserSession.getInstance().getCurrentUser().getUid().equals(guestbook.creatorId);

                holder.itemView.setClickable(isMember || isCreator);
                holder.itemView.setOnClickListener(isMember || isCreator? v -> {
                    Intent intent = new Intent(v.getContext(), GuestbookActivity.class);
                    intent.putExtra(GuestbookActivity.GUESTBOOK_KEY, guestbook.getId());
                    v.getContext().startActivity(intent);
                } : null);


                boolean finalIsMember = isMember;
                holder.itemView.setOnLongClickListener(v -> {
                    if (UserSession.getInstance().getCurrentUser().getUid().equals(guestbook.getCreatorId())) {
                        DeleteDialogGuestbook deleteDialogGuestbook = new DeleteDialogGuestbook(context, guestbook);
                        deleteDialogGuestbook.show(((AppCompatActivity) context).getSupportFragmentManager(), "Entfernen");
                        return false;
                    } else if (!finalIsMember){
                        DatabaseReference favoritesRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid());
                        favoritesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User userSender = dataSnapshot.getValue(User.class);
                                JoinGuestbookDialog joinGuestbookDialog = new JoinGuestbookDialog(context, guestbook, userSender);
                                joinGuestbookDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Anfragen");
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle error
                            }
                        });

                    }
                    return true;
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return mGuestbooks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ShapeableImageView mGuestbookImage;
        private final MaterialTextView mGuestbookName;
        private final MaterialTextView mGuestbookDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            mGuestbookImage = itemView.findViewById(R.id.user_image);
            mGuestbookName = itemView.findViewById(R.id.guest_name);
            mGuestbookDescription = itemView.findViewById(R.id.guestbook_description);
            itemView.setOnClickListener(this);
        }

        public void bind(Guestbook guestbook) {
            Glide.with(itemView.getContext()).load(guestbook.getPictureUrl()).into(mGuestbookImage);
            mGuestbookName.setText(guestbook.getName());
            mGuestbookDescription.setText(guestbook.getDescription());
        }

        @Override
        public void onClick(View v) {
            mListener.onGuestbookClick(mGuestbooks.get(getAdapterPosition()));
        }
    }

    public interface OnGuestbookClickListener {
        void onGuestbookClick(Guestbook guestbook);
    }
}
