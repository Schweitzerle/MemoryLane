package com.example.memorylane.FragmentsGuestbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorylane.Adapters.ImageAdapter;
import com.example.memorylane.R;


public class HomeFragment extends Fragment {


    ImageAdapter imageAdapter;
    private RecyclerView pictureList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(fragmentView);
        return fragmentView;
    }

    private void initUI(View fragmentView) {
        initButtons(fragmentView);
    }



    private void initButtons(View fragmentView) {

    }




}



