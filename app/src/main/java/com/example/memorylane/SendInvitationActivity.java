package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.memorylane.Adapters.SearchResultAdapter;
import com.example.memorylane.Classes.User;
import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SendInvitationActivity extends AppCompatActivity {

    EditText searchEdit;
    RecyclerView matchingUsersRecycler;
    List<User> matchingUsers = new ArrayList<>();
    SearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invitation);
        initUI();
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