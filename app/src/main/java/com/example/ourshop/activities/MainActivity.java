package com.example.ourshop.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ourshop.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    TextView textView;
    Geocoder geocoder;
    List<Address> addresses;

    public Double latitude;
    public Double longitude;

    public static boolean mIsNightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set switch night mode di home
        SwitchCompat switchCompat = findViewById(R.id.switch_dark_mode);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsNightMode = isChecked;
                int delayTime = 200;
                buttonView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsNightMode){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                    }
                }, delayTime);
            }
        });

        textView = (TextView) findViewById(R.id.user_location);

        checkUserLocationPermit();

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
        startActivity(new Intent(MainActivity.this, KulinerActivity.class));
    }

    public void PrayPlaceActivity(View view) {
        startActivity(new Intent(MainActivity.this, PrayPlaceActivity.class));
    }

    // check the user location permit
    public void checkUserLocationPermit() {
        // Check for user's permit on Location
        if (
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            // You can use the API that requires the permission.

            // Initializing the location manager
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Checking if the GPS and network are enabled
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            // CHECKING IF THE GPS OR NETWORK ENABLED, IF NOT THEN USE API THAT DID NOT NEED LATLONG
            if(gps_enabled && network_enabled) {
                if (
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    finish();
                }

                // GET THE LAST KNOWN LOCATION OF YOUR DEVICE
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location == null) {
                    // IF LAST KNOWN LOCATION IS NULL, GET RECENT LOCATION
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
                } else {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();

                    getUserLocation();
                }
            } else {
                textView.setText("hidupkan lokasi untuk dapat mengetahui lokasi anda sekarang");
            }
        } else {
            // User did not grant permit to acces the location
            // Use the API that did not need any permit from the user (without latitude and longitude)
            textView.setText("izinkan pendeteksian lokasi untuk dapat mengetahui lokasi anda sekarang");
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            getUserLocation();
        }
    };

    // get the user location
    public void getUserLocation() {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addresses.get(0);
            String street = address.getThoroughfare() == null? "unnamed road": address.getThoroughfare();
            String village = address.getFeatureName();
            String subDistrict = address.getLocality();
            textView.setText(street  + ", " + village + ", " + subDistrict);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @SuppressLint("MissingPermission")
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Initializing the location manager
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    // Checking if the GPS and network are enabled
                    boolean gps_enabled = false;
                    boolean network_enabled = false;
                    try {
                        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    } catch(Exception ex) {}

                    try {
                        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    } catch(Exception ex) {}

                    // CHECKING IF THE GPS OR NETWORK ENABLED, IF NOT THEN USE API THAT DID NOT NEED LATLONG
                    if(gps_enabled && network_enabled) {
                        if (
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        ) {
                            finish();
                        }

                        // GET THE LAST KNOWN LOCATION OF YOUR DEVICE
                        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location == null) {
                            // IF LAST KNOWN LOCATION IS NULL, GET RECENT LOCATION
                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
                        } else {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();

                            getUserLocation();
                        }
                    } else {
                        textView.setText("hidupkan lokasi untuk dapat mengetahui lokasi anda sekarang");
                    }
                } else {

                }
            });
}
