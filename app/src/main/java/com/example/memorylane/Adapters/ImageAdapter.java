package com.example.memorylane.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private List<UploadedPicture> uploadedPictures;



    public ImageAdapter(Context context, List<UploadedPicture> uploadedPictures) {
        this.uploadedPictures = uploadedPictures;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.picture_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UploadedPicture uploadedPicture = uploadedPictures.get(position);
        holder.bind(uploadedPicture);

        holder.itemView.setOnClickListener(v -> {

            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.picture_detail_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loadUI(dialog, uploadedPicture);


            dialog.show();
        });
    }

    private void loadUI(Dialog dialog, UploadedPicture uploadedPicture) {
        ShapeableImageView shapeableImageView = dialog.findViewById(R.id.gallery_image_view);
        ShapeableImageView signatureImage = dialog.findViewById(R.id.signature);
        TextView imageDescription = dialog.findViewById(R.id.gallery_image_description);


        Glide.with(context).load(uploadedPicture.getImagePath()).into(shapeableImageView);

        loadSignature(signatureImage);

        imageDescription.setText(uploadedPicture.getDescription());
    }

    private void loadSignature(ShapeableImageView signatureImage) {
        DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("signatureUrl");
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
        return uploadedPictures.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView mGuestbookImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mGuestbookImage = itemView.findViewById(R.id.staggered_item_image_view);
        }

        public void bind(UploadedPicture guestbook) {
            Glide.with(itemView.getContext()).load(guestbook.getImagePath()).into(mGuestbookImage);
        }


    }

}
