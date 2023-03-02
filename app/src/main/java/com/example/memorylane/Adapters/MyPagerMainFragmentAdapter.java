package com.example.memorylane.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.memorylane.FramentsMainInner.GuetsbooksAsCreatorFragment;
import com.example.memorylane.FramentsMainInner.GuetsbooksAsMemberFragment;


public class MyPagerMainFragmentAdapter extends FragmentStateAdapter {


    public MyPagerMainFragmentAdapter(FragmentActivity fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GuetsbooksAsCreatorFragment();
            case 1:
                return new GuetsbooksAsMemberFragment();
            // weitere Fälle für weitere Fragmente hinzufügen
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}


