package com.example.memorylane.FragmentsMain;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.LoginActivity;
import com.example.memorylane.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    MaterialTextView setsOwned, setsFavored, bricksOwned, statusTextView, emailTextView;
    MaterialButton signOutButton;
    ShapeableImageView profileImage, signatureImage;

    String name = "";
    int start = 0;

    List<String> favoriteSetNames = new ArrayList<>();

    List<Integer> mySetNamesList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        initUI(view);
        if (UserSession.getInstance().getCurrentUser() != null) {
            initSignIn(view);
            DatabaseReference favoritesRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("Favorites");
            getFavorites(favoritesRef, favoriteSetNames);
            DatabaseReference myRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("My_Sets");
            getMySets(myRef, mySetNamesList);
            loadSignature();
            loadProfileImage();
        }

        return view;
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

    private void loadProfileImage() {
        DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("imageUrl");
        signatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String signature = dataSnapshot.getValue(String.class);
                if (signature!= null) {
                    byte[] decodedString = Base64.decode(signature, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    // set the signature to the ImageView
                    profileImage.setImageBitmap(decodedByte);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void initSignIn(View view) {
        signatureImage = view.findViewById(R.id.signature);
        profileImage = view.findViewById(R.id.user_profile);
        statusTextView = view.findViewById(R.id.name_view);
        signOutButton = view.findViewById(R.id.sign_out_button);
        emailTextView = view.findViewById(R.id.email_text_view);

        signatureImage.setOnClickListener(v -> openSignaturePad());


        

        signOutButton.setOnClickListener(this);



        if (UserSession.getInstance().getCurrentUser() != null) {
            emailTextView.setText(UserSession.getInstance().getCurrentUser().getEmail());
            DatabaseReference favoritesRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("username");
            favoritesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String signature = dataSnapshot.getValue(String.class);
                    if (signature!= null) {
                        statusTextView.setText(signature);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

    }

    private void openSignaturePad() {
        // Create a new DialogFragment to display the signature pad
        DialogFragment signaturePadFragment = new SignaturePadFragment();
        // Show the DialogFragment
        signaturePadFragment.show(getFragmentManager(), "SignaturePadFragment");
    }

    private void getMySets(DatabaseReference myReference, final List<Integer> mySetNames) {
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mySetNames.clear();
                for (DataSnapshot legoSetSnapshot : dataSnapshot.getChildren()) {
                    Integer legoSetName = legoSetSnapshot.child("Number_Of_Bricks").getValue(Integer.class);
                    mySetNames.add(legoSetName);
                }
                initMyAnim(mySetNames);
                initAnimators(mySetNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void initMyAnim(List<Integer> mySets) {
        int endMy = mySets.size();
        if (endMy != 0) {
            ValueAnimator animatorMy = ValueAnimator.ofInt(start, endMy);
            animatorMy.setDuration(1000);
            animatorMy.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                setsOwned.setText(String.valueOf(value));
            });
            animatorMy.start();
        } else {
            setsOwned.setText("0");
        }
    }

    private void getFavorites(DatabaseReference favoritesRef, final List<String> favoriteSetNames) {
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteSetNames.clear();
                for (DataSnapshot legoSetSnapshot : dataSnapshot.getChildren()) {
                    String legoSetName = legoSetSnapshot.child("Set_Number").getValue(String.class);
                    favoriteSetNames.add(legoSetName);

                }
                initFavAnim(favoriteSetNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void initFavAnim(List<String> favoriteSetNames) {
        int endFav = favoriteSetNames.size();
        if (endFav != 0) {
            ValueAnimator animatorFav = ValueAnimator.ofInt(start, endFav);
            animatorFav.setDuration(1000);
            animatorFav.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                setsFavored.setText(String.valueOf(value));
            });
            animatorFav.start();
        } else {
            setsFavored.setText("0");
        }

    }

    private void initAnimators(List<Integer> mySetBricks) {
        int endBricks = 0;

        for (Integer integer : mySetBricks) {
            endBricks += integer;
        }

        if (endBricks != 0) {
            ValueAnimator animatorBricks = ValueAnimator.ofInt(start, endBricks);
            animatorBricks.setDuration(1000);
            animatorBricks.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                bricksOwned.setText(String.valueOf(value));
            });
            animatorBricks.start();
        } else {
            bricksOwned.setText("0");
        }
    }

    private void initUI(View view) {
        setsFavored = view.findViewById(R.id.sets_favored);
        setsOwned = view.findViewById(R.id.sets_owned);
        bricksOwned = view.findViewById(R.id.bricks_owned_in_sets);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_out_button) {
            signOut();
        }
    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        StyleableToast.makeText(requireContext(), "Auf Wiedersehen " + name + "!", R.style.customToastLoggedIn).show();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }



}