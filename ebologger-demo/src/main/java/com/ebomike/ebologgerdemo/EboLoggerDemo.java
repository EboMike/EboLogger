package com.ebomike.ebologgerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ebomike.ebologger.EboLogger;

public class EboLoggerDemo extends AppCompatActivity {
    private EboLogger logger = EboLogger.get(this);

    static {
        // Fire up a new thread to demonstrate multiple threads in the UI.
        new ThreadDemo().start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebo_logger_demo);
    }

    public void crashHandler(View view) {
        Intent intent = new Intent(this, CrashDemo.class);
        startActivity(intent);
    }

    public void lifecycleDemo(View view) {
        Intent intent = new Intent(this, ActivityLifecycleDemo.class);
        startActivity(intent);
    }
}
