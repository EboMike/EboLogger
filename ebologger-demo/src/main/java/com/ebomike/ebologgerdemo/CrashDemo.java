package com.ebomike.ebologgerdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
