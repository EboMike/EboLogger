package com.ebomike.ebologgerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivityLifecycleDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle_demo);
    }
}
