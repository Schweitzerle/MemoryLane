package com.example.memorylane;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.FragmentsGuestbook.GalleryFragment;
import com.example.memorylane.FragmentsGuestbook.GuestFragment;
import com.example.memorylane.FragmentsGuestbook.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GuestbookActivity extends AppCompatActivity {

    public static final String GUESTBOOK_KEY = "guestbook";
    public static String guestbookID = "";

    BottomNavigationView bottomNavigationView;
    AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guestbook);
        Bundle bundle = getIntent().getExtras();
        guestbookID = bundle.getString(GUESTBOOK_KEY);
        initUI();
        getGuestbookById(guestbookID);
    }

    private void initUI() {
        ConstraintLayout constraintLayout = findViewById(R.id.main_container);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();
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
        drawable.stop();
    }
}