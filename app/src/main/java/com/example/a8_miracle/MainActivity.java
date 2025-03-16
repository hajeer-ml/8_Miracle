package com.example.a8_miracle;


import android.animation.ObjectAnimator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds
    private ProgressBar progressBar;
    private ImageView logoImage;
    private TextView titleText, subtitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        progressBar = findViewById(R.id.splashProgress);
        logoImage = findViewById(R.id.splashLogo);
        titleText = findViewById(R.id.splashTitle);
        subtitleText = findViewById(R.id.splashSubtitle);

        // Animations
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Apply animations
        logoImage.startAnimation(zoomIn);
        titleText.startAnimation(fadeIn);
        subtitleText.startAnimation(fadeIn);

        // Progress Bar Animation
        animateProgressBar();


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String userID = sharedPreferences.getString("userID", "");

        // Navigate to the appropriate activity after SPLASH_DURATION
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn && !userID.isEmpty()) {

                    startActivity(new Intent(MainActivity.this, Navbar.class));
                } else {

                    startActivity(new Intent(MainActivity.this, spltwo.class));
                }
                finish();
            }
        }, SPLASH_DURATION);
    }

    private void animateProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int progress = 0; progress <= 100; progress += 5) {
                    try {
                        Thread.sleep(SPLASH_DURATION / 20);
                        int finalProgress = progress;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(finalProgress);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}