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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorylane.Adapters.InvitationAdapter;
import com.example.memorylane.Adapters.RequestAdapter;
import com.example.memorylane.Classes.GuestbookInvitation;
import com.example.memorylane.Classes.GuestbookRequest;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.LoginActivity;
import com.example.memorylane.R;
import com.example.memorylane.RequestsActivity;
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

    MaterialTextView statusTextView, emailTextView, uidTextView;
    MaterialButton signOutButton;
    ShapeableImageView profileImage, signatureImage;
    RecyclerView invitationRecycler;
    InvitationAdapter invitationAdapter;


    String name = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        invitationRecycler = view.findViewById(R.id.invitationRecycler);

        if (UserSession.getInstance().getCurrentUser() != null) {
            initSignIn(view);
            loadSignature();
            loadProfileImage();
            retrieveGuestbookInvitations();
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

    private void retrieveGuestbookInvitations() {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid());
        guestbooksRef.child("invitations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GuestbookInvitation> guestbookInvitations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GuestbookInvitation guestbookInvitation = snapshot.getValue(GuestbookInvitation.class);
                    guestbookInvitations.add(guestbookInvitation);
                }
                invitationAdapter = new InvitationAdapter(getContext(), guestbookInvitations);
                invitationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                invitationRecycler.setAdapter(invitationAdapter);
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
        uidTextView = view.findViewById(R.id.uid_text_view);

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
            DatabaseReference dataRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("shortUID");
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String signature = dataSnapshot.getValue(String.class);
                    if (signature!= null) {
                        uidTextView.setText("UID: " + signature);
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








    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_out_button) {
            signOut();
        }
    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        StyleableToast.makeText(requireContext(), "Auf Wiedersehen " + name + "!", R.style.customToast).show();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }



}