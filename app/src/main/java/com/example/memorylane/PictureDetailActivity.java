package com.example.memorylane;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
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

public class PictureDetailActivity extends AppCompatActivity {

    //IntentKey to retrieve the current image that is handed over from the previous fragment
    public static final String KEY_GALLERY_IMAGE = "gallery-image";

    ShapeableImageView signatureImage;
    CardView signatureCardView;
    AnimationDrawable drawable;
    ConstraintLayout conn;
    LinearLayout lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }


    //Initialises the dispalyed UI
    private void initUI() {
        setContentView(R.layout.activity_diary_picture_detail);
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        drawable = (AnimationDrawable) constraintLayout.getBackground();
        drawable.setEnterFadeDuration(2500);
        drawable.setExitFadeDuration(5000);
        drawable.start();

        conn = findViewById(R.id.conn);
        lin = findViewById(R.id.lin);
        signatureImage = findViewById(R.id.signature);
        signatureCardView = findViewById(R.id.signatureCardView);
        TextView title = findViewById(R.id.main_header_text);
        ImageView imageView = findViewById(R.id.gallery_image_view);
        TextView imageDescription = findViewById(R.id.gallery_image_description);
        TextView photographerID = findViewById(R.id.gallery_image_photographer);

        conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conn.setVisibility(View.INVISIBLE);
                lin.setVisibility(View.VISIBLE);
            }
        });

        lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin.setVisibility(View.INVISIBLE);
                conn.setVisibility(View.VISIBLE);
            }
        });

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
        UploadedPicture uploadedPicture = (UploadedPicture) getIntent().getSerializableExtra(KEY_GALLERY_IMAGE);

        //Sets UI depending on the image
        loadSignature();
        DatabaseReference userRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users");
        userRef.child(uploadedPicture.getUploaderID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                photographerID.setText(user.getUsername());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
        photographerID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photographerID.setVisibility(View.INVISIBLE);
                signatureCardView.setVisibility(View.VISIBLE);
            }
        });
        signatureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photographerID.setVisibility(View.VISIBLE);
                signatureCardView.setVisibility(View.INVISIBLE);
            }
        });

        imageDescription.setText(uploadedPicture.getDescription());
        Glide.with(getBaseContext()).load(uploadedPicture.getImagePath()).into(imageView);
    }

    private void loadSignature() {
        DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("signatureUrl");
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