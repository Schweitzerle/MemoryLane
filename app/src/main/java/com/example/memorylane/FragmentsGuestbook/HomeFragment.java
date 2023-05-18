package com.example.memorylane.FragmentsGuestbook;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.memorylane.Adapters.SliderAdapter;
import com.example.memorylane.Classes.GuestEntry;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.SliderItem;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.R;
import com.example.memorylane.RequestsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private MaterialTextView guestbookName, guestBookDescription, amountOfPictures, amountOfEntries, date, amountOfMembers;
    private ShapeableImageView guestBookImage;
    private FloatingActionButton floatingActionButton;
    private Palette.Swatch vibrantSwatch, lightVibrantSwatch, darkVibrantSwatch, mutedSwatch, lightMutedSwatch, darkMutedSwatch;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(fragmentView);
        return fragmentView;
    }

    private void initUI(View fragmentView) {
        viewPager2 = fragmentView.findViewById(R.id.view_pager_image_slider);
        guestBookImage = fragmentView.findViewById(R.id.user_image);
        guestbookName = fragmentView.findViewById(R.id.guest_name);
        guestBookDescription = fragmentView.findViewById(R.id.guestbook_description);
        amountOfEntries = fragmentView.findViewById(R.id.guestbook_entry_amount);
        amountOfMembers = fragmentView.findViewById(R.id.guestbook_members_amount);
        date = fragmentView.findViewById(R.id.guestbook_date);
        amountOfPictures = fragmentView.findViewById(R.id.guestbook_picture_amount);
        floatingActionButton = fragmentView.findViewById(R.id.requestsButton);
        iniCardView(fragmentView);
        initImageList(fragmentView);
    }


    private void initNumberAnim(String prefix, MaterialTextView materialTextView,int end) {
        int start = 0;
        int endBricks = end;


        if (endBricks != 0) {
            ValueAnimator animatorBricks = ValueAnimator.ofInt(start, endBricks);
            animatorBricks.setDuration(1000);
            animatorBricks.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                materialTextView.setText(prefix + String.valueOf(value));
            });
            animatorBricks.start();
        } else {
            materialTextView.setText("0");
        }
    }






private void iniCardView(View fragmentView) {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        guestbooksRef.child(GuestbookActivity.guestbookID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Guestbook guestbook = snapshot.getValue(Guestbook.class);
                if (guestbook != null) {
                    Activity activity = getActivity();
                    if (isAdded() && activity != null) {
                        if (UserSession.getInstance().getCurrentUser().getUid().equals(guestbook.getCreatorId())) {
                            floatingActionButton.setVisibility(View.VISIBLE);
                            floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), RequestsActivity.class)));
                        } else floatingActionButton.setVisibility(View.INVISIBLE);

                        guestbookName.setText(guestbook.getName());
                        guestBookDescription.setText(guestbook.getDescription());
                        date.setText(guestbook.getDate());
                        Glide.with(requireActivity()).load(guestbook.getPictureUrl()).into(guestBookImage);
                        Glide.with(requireActivity())
                                .asBitmap()
                                .load(guestbook.getPictureUrl())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        // do something with the Bitmap
                                        Palette.from(resource).maximumColorCount(32).generate(palette -> {
                                            vibrantSwatch = palette.getVibrantSwatch();
                                            lightVibrantSwatch = palette.getLightVibrantSwatch();
                                            darkVibrantSwatch = palette.getDarkVibrantSwatch();
                                            mutedSwatch = palette.getMutedSwatch();
                                            lightMutedSwatch = palette.getLightMutedSwatch();
                                            darkMutedSwatch = palette.getDarkMutedSwatch();
                                            if (vibrantSwatch != null) {

                                            }
                                        });
                                    }
                                });

                    }

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
                //amountOfPictures.setText("Erinnerungen: " + uploadedPictures.size());
                initNumberAnim("Erinnerungen: ", amountOfPictures, uploadedPictures.size());
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
                //amountOfEntries.setText("Gästeeinträge: " + guestEntries.size());
                initNumberAnim("Gästeeinträge: ", amountOfEntries, guestEntries.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    guestbooksRef.child(GuestbookActivity.guestbookID).child("Members").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<String> guestEntries = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String guestEntry = snapshot.getValue(String.class);
                guestEntries.add(guestEntry);
            }
            //amountOfMembers.setText("Gästeeinträge: " + guestEntries.size());
            initNumberAnim("Mitglieder: ", amountOfMembers, guestEntries.size());
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




        private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

            private Exception exception;

            @Override
            protected Bitmap doInBackground(String... urls) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();

                    // Calculate inSampleSize
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(input, null, options);
                    int imageWidth = options.outWidth;
                    int imageHeight = options.outHeight;
                    int reqWidth = 500; // Your desired width
                    int reqHeight = 500; // Your desired height
                    options.inSampleSize = calculateInSampleSize(imageWidth, imageHeight, reqWidth, reqHeight);
                    options.inJustDecodeBounds = false;

                    // Decode the bitmap with reduced size
                    input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
                    return bitmap;
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap generatedBitmap) {
                if (exception != null) {
                    // Handle the exception
                } else {
                        // Use the bitmap
                        Palette.from(generatedBitmap).generate(palette -> {
                            vibrantSwatch = palette.getVibrantSwatch();
                            lightVibrantSwatch = palette.getLightVibrantSwatch();
                            darkVibrantSwatch = palette.getDarkVibrantSwatch();
                            mutedSwatch = palette.getMutedSwatch();
                            lightMutedSwatch = palette.getLightMutedSwatch();
                            darkMutedSwatch = palette.getDarkMutedSwatch();
                        });


                        ShapeDrawable shapeDrawable = (ShapeDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.round_corners_secondary);
                        shapeDrawable.getPaint().setColor(vibrantSwatch.getBodyTextColor());



                }
            }

            private int calculateInSampleSize(int imageWidth, int imageHeight, int reqWidth, int reqHeight) {
                int inSampleSize = 1;
                if (imageHeight > reqHeight || imageWidth > reqWidth) {
                    final int halfHeight = imageHeight / 2;
                    final int halfWidth = imageWidth / 2;
                    while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                }
                return inSampleSize;
            }
        }


}



