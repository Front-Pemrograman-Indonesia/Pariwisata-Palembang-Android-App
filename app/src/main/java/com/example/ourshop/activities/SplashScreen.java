package com.example.ourshop.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ourshop.R;

@SuppressLint("CustomSplashScreen")
@SuppressWarnings("FieldCanBeLocal")
public class SplashScreen extends AppCompatActivity {

    private final int splash_display_length = 4300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
            SplashScreen.this.startActivity(mainIntent);
            SplashScreen.this.finish();
        }, splash_display_length);
    }
}