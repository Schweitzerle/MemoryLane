package com.example.memorylane.Classes;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class MyDynamicColorApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}

