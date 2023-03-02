package com.example.memorylane;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.memorylane.FragmentsMain.GuestbooksFragment;
import com.example.memorylane.FragmentsMain.MainFragment;
import com.example.memorylane.FragmentsMain.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        ConstraintLayout constraintLayout = findViewById(R.id.main_container);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MainFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_guestbooks);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_guestbooks:
                        fragment = new MainFragment();
                        break;
                    case R.id.nav_browse_guestbooks:
                        fragment = new GuestbooksFragment();
                        break;
                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                assert fragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();

                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawable.stop();
    }
}