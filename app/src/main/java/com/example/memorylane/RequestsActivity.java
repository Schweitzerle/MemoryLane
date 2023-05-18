package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.memorylane.Adapters.RequestAdapter;
import com.example.memorylane.Classes.ColorPaletteUtils;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.GuestbookRequest;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private FloatingActionButton sendInviteButton;

    private ConstraintLayout constraintLayout;

    private AnimationDrawable animationDrawable;

    private MaterialTextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initUI();
    }

    private void initUI() {
        sendInviteButton = findViewById(R.id.send_invitation_float_button);
        sendInviteButton.setOnClickListener(view -> startActivity(new Intent(RequestsActivity.this, SendInvitationActivity.class)));
        recyclerView = findViewById(R.id.requestRecycler);
        header = findViewById(R.id.header);
        retrieveGuestbookRequests();
        getColorPaletteFromGBPicture();
    }

    private void retrieveGuestbookRequests() {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(GuestbookActivity.guestbookID);
        guestbooksRef.child("joinRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GuestbookRequest> guestbookRequests = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GuestbookRequest guestbookRequest = snapshot.getValue(GuestbookRequest.class);
                    guestbookRequests.add(guestbookRequest);
                }
                requestAdapter = new RequestAdapter(RequestsActivity.this, guestbookRequests);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(requestAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void getColorPaletteFromGBPicture() {



                        if (ColorPaletteUtils.vibrantSwatch != null) {
                            // Inflate the shape drawable from the XML file
                            GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(getBaseContext(), R.drawable.bottom_corners_rounded);
                            // Set a new color for the shape drawable
                            shapeDrawable.setColor(ColorPaletteUtils.darkVibrantSwatch.getRgb());
                            shapeDrawable.setPadding(20,20,20,20);

                            // Set the new shape drawable as the background of a view

                            // Set a new color for the shape drawable
                            header.setBackground(shapeDrawable);
                            // Modify the start and end colors of anim1

                            GradientDrawable gradientDrawable = new GradientDrawable();
                            gradientDrawable.setColors(new int[]{ColorPaletteUtils.lightVibrantSwatch.getRgb(),ColorPaletteUtils.lightMutedSwatch.getRgb()});


                            // Modify the start and end colors of anim2
                            GradientDrawable gradientDrawable2 = new GradientDrawable();
                            gradientDrawable2.setColors(new int[]{ColorPaletteUtils.vibrantSwatch.getRgb(), ColorPaletteUtils.darkMutedSwatch.getRgb()});

                            // Modify the start and end colors of anim3
                            GradientDrawable gradientDrawable1 = new GradientDrawable();
                            gradientDrawable1.setColors(new int[]{ColorPaletteUtils.mutedSwatch.getRgb(), ColorPaletteUtils.darkVibrantSwatch.getRgb()});
                            // Set the modified drawable resources as the background of the ConstraintLayout
                            constraintLayout = findViewById(R.id.main_container);
                            animationDrawable = new AnimationDrawable();
                            animationDrawable.addFrame(gradientDrawable, 5000);
                            animationDrawable.addFrame(gradientDrawable2, 5000);
                            animationDrawable.addFrame(gradientDrawable1, 5000);
                            constraintLayout.setBackground(animationDrawable);

                            // Start the animation
                            animationDrawable.setEnterFadeDuration(2000);
                            animationDrawable.setExitFadeDuration(2000);
                            animationDrawable.start();


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(ColorPaletteUtils.darkVibrantSwatch.getRgb());
                            }
                        }
                    }

    }