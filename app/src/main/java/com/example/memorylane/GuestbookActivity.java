package com.example.memorylane;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.FragmentsGuestbook.GalleryFragment;
import com.example.memorylane.FragmentsGuestbook.GuestFragment;
import com.example.memorylane.FragmentsGuestbook.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GuestbookActivity extends AppCompatActivity {

    public static final String GUESTBOOK_KEY = "guestbook";
    public static String guestbookID = "";

    BottomNavigationView bottomNavigationView;
    AnimationDrawable animationDrawable;
    ConstraintLayout constraintLayout;

    public static Palette.Swatch vibrantSwatch, lightVibrantSwatch, darkVibrantSwatch, mutedSwatch, lightMutedSwatch, darkMutedSwatch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guestbook);
        Bundle bundle = getIntent().getExtras();
        guestbookID = bundle.getString(GUESTBOOK_KEY);
        initUI();
        getGuestbookById(guestbookID);
    }



    private void getColorPaletteFromGBPicture(Guestbook guestbook) {

        Glide.with(getBaseContext())
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
                                // Inflate the shape drawable from the XML file
                                GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(getBaseContext(), R.drawable.round_corners_secondary);
                                // Set a new color for the shape drawable
                                shapeDrawable.setColor(darkVibrantSwatch.getRgb());
                                // Set the new shape drawable as the background of a view
                                bottomNavigationView.setBackground(shapeDrawable);


                                // Modify the start and end colors of anim1

                                    GradientDrawable gradientDrawable = new GradientDrawable();
                                    gradientDrawable.setColors(new int[]{vibrantSwatch.getRgb(),lightVibrantSwatch.getRgb()});


                                // Modify the start and end colors of anim2
                                    GradientDrawable gradientDrawable2 = new GradientDrawable();
                                    gradientDrawable2.setColors(new int[]{darkVibrantSwatch.getRgb(), vibrantSwatch.getRgb()});

                                // Modify the start and end colors of anim3
                                    GradientDrawable gradientDrawable1 = new GradientDrawable();
                                    gradientDrawable1.setColors(new int[]{darkMutedSwatch.getRgb(), lightVibrantSwatch.getRgb()});

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
                                    window.setStatusBarColor(darkVibrantSwatch.getRgb());
                                }
                            }
                        });
                    }
                });

    }

    private void initUI() {

        bottomNavigationView = findViewById(R.id.bottom_navigation_guestbook);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_newlyweds);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_newlyweds:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_guests:
                        fragment = new GuestFragment();
                        break;
                    case R.id.nav_gallery:
                        fragment = new GalleryFragment();
                        break;
                }
                assert fragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();

                return true;
            }
        });
    }


    private void getGuestbookById(String guestbookId) {
        // Get a reference to the guestbook in the database
        DatabaseReference guestbookRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(guestbookId);

        // Add a value event listener to the guestbook reference
        guestbookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Extract the guestbook data from the snapshot
                Guestbook guestbook = dataSnapshot.getValue(Guestbook.class);
                // Do something with the guestbook data
                getColorPaletteFromGBPicture(guestbook);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", "Error getting guestbook", databaseError.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GuestbookActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //animationDrawable.stop();
    }
}