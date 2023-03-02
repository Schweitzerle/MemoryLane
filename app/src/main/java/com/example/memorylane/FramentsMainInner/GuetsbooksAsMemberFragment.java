package com.example.memorylane.FramentsMainInner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorylane.Adapters.GuestbookAdapter;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GuetsbooksAsMemberFragment extends Fragment {

    GuestbookAdapter guestbookAdapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guetsbooks_as_creator, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.my_guestbooks_recycler);
        retrieveUserGuestbooks();
    }

    private void retrieveUserGuestbooks() {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("guestbooks");
        guestbooksRef.orderByChild("members/" + UserSession.getInstance().getCurrentUser().getUid()).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Guestbook> userGuestbooks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Guestbook guestbook = snapshot.getValue(Guestbook.class);
                    userGuestbooks.add(guestbook);
                }
                guestbookAdapter = new GuestbookAdapter(getContext(), userGuestbooks);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(guestbookAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}