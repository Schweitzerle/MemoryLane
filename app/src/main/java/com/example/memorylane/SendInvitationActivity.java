package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.memorylane.Adapters.SearchResultAdapter;
import com.example.memorylane.Classes.ColorPaletteUtils;
import com.example.memorylane.Classes.User;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SendInvitationActivity extends AppCompatActivity {

    EditText searchEdit;
    RecyclerView matchingUsersRecycler;
    List<User> matchingUsers = new ArrayList<>();
    SearchResultAdapter adapter;

    LinearLayout linearLayout;
    AnimationDrawable animationDrawable;
    MaterialTextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invitation);
        initUI();
        getColorPaletteFromGBPicture();
    }

    private void getColorPaletteFromGBPicture() {

        if (ColorPaletteUtils.vibrantSwatch != null) {
            // Inflate the shape drawable from the XML file
            GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(getBaseContext(), R.drawable.bottom_corners_rounded);
            // Set a new color for the shape drawable
            shapeDrawable.setColor(ColorPaletteUtils.darkVibrantSwatch.getRgb());
            shapeDrawable.setPadding(20,20,20,20);
            // Set the new shape drawable as the background of a view

            header = findViewById(R.id.main_header_text);
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
            linearLayout = findViewById(R.id.main_container);
            animationDrawable = new AnimationDrawable();
            animationDrawable.addFrame(gradientDrawable, 5000);
            animationDrawable.addFrame(gradientDrawable2, 5000);
            animationDrawable.addFrame(gradientDrawable1, 5000);
            linearLayout.setBackground(animationDrawable);

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

    private void initUI() {
        searchEdit = findViewById(R.id.searchEditText);
        matchingUsersRecycler = findViewById(R.id.searchResultsRecyclerView);
        adapter = new SearchResultAdapter(SendInvitationActivity.this, matchingUsers);
        matchingUsersRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        matchingUsersRecycler.setAdapter(adapter);


        DatabaseReference usersRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }



                searchEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // Not needed in this case, but required by TextWatcher interface
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String searchText = charSequence.toString();

                        if (!searchText.isEmpty()) {
                            matchingUsers.clear();
                            matchingUsers.addAll(filterUsers(searchText, userList));
                        } else {
                            matchingUsers.clear();
                        }
                        adapter.notifyDataSetChanged();
                     }


                    @Override
                    public void afterTextChanged(Editable editable) {
                        // Not needed in this case, but required by TextWatcher interface
                    }

                    private List<User> filterUsers(String searchText, List<User> allUsers) {
                        List<User> matchingUsers = new ArrayList<>();

                        for (User user : allUsers) {
                            if (user.getShortUID().contains(searchText)) {
                                matchingUsers.add(user);
                            }
                        }
                        return matchingUsers;
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}