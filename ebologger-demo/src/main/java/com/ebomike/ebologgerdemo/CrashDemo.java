package com.ebomike.ebologgerdemo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CrashDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_demo);
    }

    public void crash(View view) {
        throw new RuntimeException("This is an exception to deliberately crash the app.");
    }
}
