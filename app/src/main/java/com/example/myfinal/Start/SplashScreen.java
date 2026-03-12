package com.example.myfinal.Start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinal.R;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, OpenPage.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
