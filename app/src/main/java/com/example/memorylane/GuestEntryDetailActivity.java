package com.example.memorylane;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.GuestEntry;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.UploadedPicture;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.User;
import com.example.memorylane.Database.UserSession;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GuestEntryDetailActivity extends AppCompatActivity {

    //IntentKey to retrieve the current image that is handed over from the previous fragment
    public static final String KEY_GUEST_ENTRY = "GUEST ENTRY";

    ShapeableImageView signatureImage;
    AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }


    //Initialises the dispalyed UI
    private void initUI() {
        setContentView(R.layout.activity_guest_entry_detail);
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();

        signatureImage = findViewById(R.id.signature);
        TextView title = findViewById(R.id.main_header_text);
        ImageView imageView = findViewById(R.id.guestbook_image);
        TextView age = findViewById(R.id.age);
        TextView name = findViewById(R.id.guest_name);
        TextView entryText = findViewById(R.id.entryText);

        GuestEntry guestEntry = (GuestEntry) getIntent().getSerializableExtra(KEY_GUEST_ENTRY);


        entryText.setText(guestEntry.getDescription());

        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        guestbooksRef.child(GuestbookActivity.guestbookID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Guestbook guestbook = dataSnapshot.getValue(Guestbook.class);
                title.setText(guestbook.getName());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
        //Sets the displayed image depending on the intent

        //Sets UI depending on the image
        loadSignature(guestEntry.getUserID());
        DatabaseReference userRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");
        userRef.child(guestEntry.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getUsername());
                age.setText(String.valueOf(user.getAge()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


        Glide.with(getBaseContext()).load(guestEntry.getPictureURL()).into(imageView);
    }

    private void loadSignature(String userID) {
        DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(userID).child("signatureUrl");
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
    protected void onStop() {
        super.onStop();
        drawable.stop();
    }

    /*
     * Initialises the collapsing toolbar and its animation. The animation lets a title for the activity appear within the toolbar
     *
    private void initCollapsingToolbarAnimationForActivities(Context context) {
        @ColorInt int colorPrimary = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, Color.MAGENTA);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(context.getString(R.string.diary_entry_gallery_text));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(context, R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(colorPrimary);
    }
     */


}