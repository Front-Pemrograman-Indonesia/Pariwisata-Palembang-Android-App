package com.example.ourshop.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourshop.R;
import com.example.ourshop.adapter.MainAdapter;
import com.example.ourshop.api.Api;
import com.example.ourshop.decoration.LayoutMarginDecoration;
import com.example.ourshop.model.ModelMain;
import com.example.ourshop.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    TextView textView;
    Geocoder geocoder;
    List<Address> addresses;

    Double latitude = -0.789275;
    Double longtitude = 113.921327;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.user_location);

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longtitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String area = addresses.get(0).getLocality();
            String city = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalcode = addresses.get(0).getPostalCode();

            String fullAddress = area+", "+city;

            textView.setText(fullAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        // set tanggal di home
        Calendar calendar = Calendar.getInstance();
        String currentDate = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL).format(calendar.getTime());

        // ini adalah komen yang dibutuhkan
        TextView textViewDate = findViewById(R.id.tvDate);
        textViewDate.setText(currentDate);

        // Check for user's permit on Location
        if (
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
        ) {
            // if the location permit did not granted yet, the app will ask for it
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    public void WisataActivity(View view) {
        startActivity(new Intent(MainActivity.this, WisataActivity.class));
    }

    public void HotelActivity(View view) {
        startActivity(new Intent(MainActivity.this, HotelActivity.class));
    }

    public void KulinerActivity(View view) {
        Log.e("TAG IS ANYTHING", "setting the longitude latitude" + "mainnn");
        startActivity(new Intent(MainActivity.this, KulinerActivity.class));
    }

    public void PrayPlaceActivity(View view) {
        Log.e("TAG IS ANYTHING", "setting the longitude latitude" + "mainnn");
        startActivity(new Intent(MainActivity.this, PrayPlaceActivity.class));
    }

    //set Transparent Status bar
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {

                }
            });
}
