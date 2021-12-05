package com.example.ourshop.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.ourshop.R;
import com.example.ourshop.adapter.HotelAdapter;
import com.example.ourshop.api.Api;
import com.example.ourshop.model.ModelHotel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HotelActivity extends AppCompatActivity implements HotelAdapter.onSelectData {

    RecyclerView rvHotel;
    HotelAdapter hotelAdapter;
    ProgressDialog progressDialog;
    List<ModelHotel> modelHotel = new ArrayList<>();
    Toolbar tbHotel;
    public double longitude;
    public double latitude;
    public String API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        tbHotel = findViewById(R.id.toolbar_hotel);
        tbHotel.setTitle(R.string.hotels_header);
        setSupportActionBar(tbHotel);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.wait);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.wait_description));

        rvHotel = findViewById(R.id.rvHotel);
        rvHotel.setHasFixedSize(true);
        rvHotel.setLayoutManager(new LinearLayoutManager(this));

        // Check for user's permit on Location
        if (
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("TAG IS ANYTHING", "Final API3" + API);
            // You can use the API that requires the permission.

            // Initializing the location manager
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Log.e("TAG IS ANYTHING", "LM" + lm);

            // Checking if the GPS and network are enabled
            boolean gps_enabled = false;
            boolean network_enabled = false;
            Log.e("TAG IS ANYTHING", "Final API4" + API);
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                Log.e("TAG IS ANYTHING", "GPS" + gps_enabled);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                Log.e("TAG IS ANYTHING", "Network" + network_enabled);
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
                    Log.e("TAG IS ANYTHING", "last known null");
                    // IF LAST KNOWN LOCATION IS NULL, GET RECENT LOCATION
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
                } else {
                    Log.e("TAG IS ANYTHING", "last known is not null");
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    API = Api.Hotel + "?" + "longitude=" + longitude + "&" + "latitude" + latitude + "&" + "language=" + Locale.getDefault().getLanguage();

                    getHotel();
                }
            } else {
                API = Api.Hotel + "?language=" + Locale.getDefault().getLanguage();
                getHotel();
            }
        } else {
            // User did not grant permit to acces the location
            // Use the API that did not need any permit from the user (without latitude and longitude)
            API = Api.Hotel + "?language=" + Locale.getDefault().getLanguage();
            getHotel();
        }
    }

    private void getHotel() {
        progressDialog.show();
        AndroidNetworking.get(API)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray playerArray = response.getJSONArray("data");
                            for (int i = 0; i < playerArray.length(); i++) {
                                JSONObject temp = playerArray.getJSONObject(i);
                                ModelHotel dataApi = new ModelHotel();

                                dataApi.setTxtNamaHotel(temp.getString("name"));
                                dataApi.setKoordinat(temp.getString("latitude") + ", " + temp.getString("longitude"));
                                dataApi.setGambarHotel(Api.BaseUrl + temp.getString("thumbnail"));
                                if(temp.getBoolean("locationStatus")){
                                    dataApi.setHotelDistance(getString(R.string.tourist_destination_distance) + " " + temp.getString("distance") + " " + getString(R.string.kilometer));
                                } else {
                                    dataApi.setHotelDistance(getString(R.string.location_permit_warning));
                                }

                                modelHotel.add(dataApi);
                                showHotel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HotelActivity.this,
                                    "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(HotelActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showHotel() {
        hotelAdapter = new HotelAdapter(HotelActivity.this, modelHotel, this);
        rvHotel.setAdapter(hotelAdapter);
    }

    @Override
    public void onSelected(ModelHotel modelHotel) {
        Intent intent = new Intent(HotelActivity.this, DetailHotelActivity.class);
        intent.putExtra("detailHotel", modelHotel);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.e("TAG IS ANYTHING", "setting the longitude latitude" + latitude + longitude);
            API = Api.Wisata + "?" + "longitude=" + longitude + "&" + "latitude" + latitude;

            getHotel();
        }
    };
}
