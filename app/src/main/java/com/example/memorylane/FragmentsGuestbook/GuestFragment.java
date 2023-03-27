package com.example.memorylane.FragmentsGuestbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.memorylane.Adapters.GuestEntryAdapter;
import com.example.memorylane.Classes.GuestEntry;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.GuestEntryCreationActivity;
import com.example.memorylane.GuestbookActivity;
import com.example.memorylane.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GuestFragment extends Fragment {

    GuestEntryAdapter guestEntryAdapter;
    private RecyclerView guestList;

    public static Palette.Swatch vibrantSwatch, lightVibrantSwatch, darkVibrantSwatch, mutedSwatch, lightMutedSwatch, darkMutedSwatch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guests, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View fragmentView) {
        initButtons(fragmentView);
        guestList = fragmentView.findViewById(R.id.guest_list);
        guestList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        retrieveGuestEntries();
    }

    private void retrieveGuestEntries() {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        guestbooksRef.child(GuestbookActivity.guestbookID).child("guestEntries").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GuestEntry> guestEntries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GuestEntry guestEntry = snapshot.getValue(GuestEntry.class);
                    guestEntries.add(guestEntry);
                }
                guestEntryAdapter = new GuestEntryAdapter(getContext(), guestEntries);
                guestList.setAdapter(guestEntryAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void initButtons(View fragmentView) {
        FloatingActionButton galleryButton = fragmentView.findViewById(R.id.add_guest_entry_button);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPictureDetailActivity();
            }
        });
    }


    private void startPictureDetailActivity() {
        Intent intent = new Intent(getActivity(), GuestEntryCreationActivity.class);
        intent.putExtra(GuestEntryCreationActivity.GUESTBOOK_ID_KEY, GuestbookActivity.guestbookID);
        startActivity(intent);
    }
}