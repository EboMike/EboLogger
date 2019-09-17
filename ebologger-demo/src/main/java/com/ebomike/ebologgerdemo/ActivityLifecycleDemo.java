package com.ebomike.ebologgerdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityLifecycleDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle_demo);
    }
}
