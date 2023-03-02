package com.example.memorylane.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.PictureDetailActivity;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;

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
            Intent intent = new Intent(v.getContext(), PictureDetailActivity.class);
            intent.putExtra(PictureDetailActivity.KEY_GALLERY_IMAGE, uploadedPicture);
            v.getContext().startActivity(intent);
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
