package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.memorylane.Adapters.GuestbookAdapter;
import com.example.memorylane.Adapters.RequestAdapter;
import com.example.memorylane.Classes.Guestbook;
import com.example.memorylane.Classes.GuestbookRequest;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initUI();
    }

    private void initUI() {
        recyclerView = findViewById(R.id.requestRecycler);
        retrieveGuestbookRequests();
    }

    private void retrieveGuestbookRequests() {
        DatabaseReference guestbooksRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Guestbooks").child(GuestbookActivity.guestbookID);
        guestbooksRef.child("joinRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GuestbookRequest> guestbookRequests = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GuestbookRequest guestbookRequest = snapshot.getValue(GuestbookRequest.class);
                    guestbookRequests.add(guestbookRequest);
                }
                requestAdapter = new RequestAdapter(RequestsActivity.this, guestbookRequests);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(requestAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}