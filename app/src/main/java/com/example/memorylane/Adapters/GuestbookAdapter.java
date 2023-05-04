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
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), GuestbookActivity.class);
            intent.putExtra(GuestbookActivity.GUESTBOOK_KEY, guestbook.getId());
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (UserSession.getInstance().getCurrentUser().getUid().equals(guestbook.getCreatorId())) {
                DeleteDialogGuestbook deleteDialogGuestbook = new DeleteDialogGuestbook(context, guestbook);
                deleteDialogGuestbook.show(((AppCompatActivity) context).getSupportFragmentManager(), "Entfernen");
                return false;
            } else {
                JoinGuestbookDialog joinGuestbookDialog = new JoinGuestbookDialog(context, guestbook);
                joinGuestbookDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Anfragen");
                Toast.makeText(context, "Nur eigene Gästebücher sind bearbeitbar", Toast.LENGTH_SHORT).show();
                return true;
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
