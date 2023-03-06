package com.example.memorylane.FragmentsGuestbook;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.memorylane.Adapters.SliderAdapter;
import com.example.memorylane.Classes.GuestEntry;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.SliderItem;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private MaterialTextView guestbookName, guestBookDescription, amountOfPictures, amountOfEntries, date;
    private ShapeableImageView guestBookImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(fragmentView);
        return fragmentView;
    }

    private void initUI(View fragmentView) {
        viewPager2 = fragmentView.findViewById(R.id.view_pager_image_slider);
        guestBookImage = fragmentView.findViewById(R.id.guestbook_image);
        guestbookName = fragmentView.findViewById(R.id.guest_name);
        guestBookDescription = fragmentView.findViewById(R.id.guestbook_description);
        amountOfEntries = fragmentView.findViewById(R.id.guestbook_entry_amount);
        date = fragmentView.findViewById(R.id.guestbook_member_amount);
        amountOfPictures = fragmentView.findViewById(R.id.guestbook_picture_amount);
        iniCardView();
        initImageList(fragmentView);


    }

    private void iniCardView() {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        guestbooksRef.child(GuestbookActivity.guestbookID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Guestbook guestbook = snapshot.getValue(Guestbook.class);
                if (guestbook != null) {
                    guestbookName.setText(guestbook.getName());
                    guestBookDescription.setText(guestbook.getDescription());
                    date.setText(guestbook.getDate());
                    Glide.with(requireContext()).load(guestbook.getPictureUrl()).into(guestBookImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        guestbooksRef.child(GuestbookActivity.guestbookID).child("uploadedImages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UploadedPicture> uploadedPictures = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UploadedPicture uploadedPicture = snapshot.getValue(UploadedPicture.class);
                    uploadedPictures.add(uploadedPicture);
                }
                amountOfPictures.setText("Erinnerungen: " + uploadedPictures.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        guestbooksRef.child(GuestbookActivity.guestbookID).child("guestEntries").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GuestEntry> guestEntries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GuestEntry guestEntry = snapshot.getValue(GuestEntry.class);
                    guestEntries.add(guestEntry);
                }
                amountOfEntries.setText("Gästeeinträge: " + guestEntries.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }




    private void initImageList(View fragmentView) {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        guestbooksRef.child(GuestbookActivity.guestbookID).child("uploadedImages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SliderItem> sliderItems = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UploadedPicture uploadedPicture = snapshot.getValue(UploadedPicture.class);
                    SliderItem sliderItem = new SliderItem(uploadedPicture.getImagePath());
                    sliderItems.add(sliderItem);
                }

                SliderAdapter sliderAdapter = new SliderAdapter(getContext(), sliderItems, viewPager2);

                viewPager2.setAdapter(sliderAdapter);
                viewPager2.setClipToPadding(false);
                viewPager2.setClipChildren(false);
                viewPager2.setOffscreenPageLimit(3);
                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });

                viewPager2.setPageTransformer(compositePageTransformer);

                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        sliderHandler.removeCallbacks(sliderRunnable);
                        sliderHandler.postDelayed(sliderRunnable, 3000);
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }


    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}



