package com.example.memorylane.FragmentsGuestbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.memorylane.Adapters.ImageAdapter;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.PictureInitActivity;
import com.example.memorylane.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {

    ImageAdapter imageAdapter;
    private RecyclerView pictureList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View fragmentView) {
        initButtons(fragmentView);
        pictureList = fragmentView.findViewById(R.id.image_list);
        pictureList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        retrieveUploadedPictures();
    }

    private void retrieveUploadedPictures() {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        guestbooksRef.child(GuestbookActivity.guestbookID).child("uploadedImages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UploadedPicture> uploadedPictures = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UploadedPicture uploadedPicture = snapshot.getValue(UploadedPicture.class);
                    uploadedPictures.add(uploadedPicture);
                }
                imageAdapter = new ImageAdapter(getContext(), uploadedPictures);
                pictureList.setAdapter(imageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void initButtons(View fragmentView) {
        FloatingActionButton galleryButton = fragmentView.findViewById(R.id.add_image_button);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPictureDetailActivity();
            }
        });
    }


    private void startPictureDetailActivity() {
        Intent intent = new Intent(getActivity(), PictureInitActivity.class);
        intent.putExtra(PictureInitActivity.GUESTBOOK_ID_KEY, GuestbookActivity.guestbookID);
        startActivity(intent);
    }
}