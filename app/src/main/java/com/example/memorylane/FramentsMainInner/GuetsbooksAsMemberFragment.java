package com.example.memorylane.FramentsMainInner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
        String userId = UserSession.getInstance().getCurrentUser().getUid(); // replace with the user ID you want to search for

        DatabaseReference databaseReference = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks");
        Query query = databaseReference.orderByChild("Members/" + userId).equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Guestbook> guestbooks = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Guestbook guestbook = dataSnapshot.getValue(Guestbook.class);
                    guestbooks.add(guestbook);
                }
                Toast.makeText(getContext(), String.valueOf(guestbooks.size()), Toast.LENGTH_SHORT).show();
                // Do something with the list of guestbooks where the user is a member
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });


    }
}