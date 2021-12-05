package com.example.ourshop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.ourshop.R;
import com.example.ourshop.adapter.PrayPlaceAdapter;
import com.example.ourshop.api.Api;
import com.example.ourshop.model.ModelHotel;
import com.example.ourshop.model.ModelPrayPlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailPrayPlaceActivity extends AppCompatActivity {

    RecyclerView rvPrayPlace;
    PrayPlaceAdapter prayPlaceAdapter;
    ProgressDialog progressDialog;
    List<ModelPrayPlace> modelPrayPlace = new ArrayList<>();
    ModelHotel modelHotel;
    Toolbar tbPlace;
    String id;
    public double longitude;
    public double latitude;
    private String API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pray_place);

        // set header
        tbPlace = findViewById(R.id.toolbar_place);
        tbPlace.setTitle("Daftar Tempat Ibadah");

        setSupportActionBar(tbPlace);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.wait);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.wait_description));

        rvPrayPlace = findViewById(R.id.rvPrayPlace);
        rvPrayPlace.setHasFixedSize(true);
        rvPrayPlace.setLayoutManager(new LinearLayoutManager(this));

        modelHotel = (ModelHotel) getIntent().getSerializableExtra("detailHotel");
        if (modelHotel != null) {
            tbPlace.setTitle(modelHotel.getTxtNamaHotel());
            //get String
            id = modelHotel.get_id();
        }

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
                    API = Api.TempatIbadah + "?" + "longitude=" + longitude + "&" + "latitude" + latitude + "&" + "language=" + Locale.getDefault().getLanguage();

                    getPrayPlace();
                }
            } else {
                API = Api.TempatIbadah + "?language=" + Locale.getDefault().getLanguage();
                getPrayPlace();
            }
        } else {
            // User did not grant permit to acces the location
            // Use the API that did not need any permit from the user (without latitude and longitude)
            API = Api.TempatIbadah + "?language=" + Locale.getDefault().getLanguage();
            getPrayPlace();
        }
    }

    private void getPrayPlace() {
        progressDialog.show();
        AndroidNetworking.get(API)
                .addPathParameter("id", id)
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
                                ModelPrayPlace dataApi = new ModelPrayPlace();

                                dataApi.setTxtTempatIbadah(temp.getString("name"));
                                dataApi.setLatitude(temp.getDouble("latitude"));
                                dataApi.setLongitude(temp.getDouble("longitude"));
                                if(temp.getBoolean("locationStatus")){
                                    dataApi.setWorshipPlaceDistance(getString(R.string.tourist_destination_distance) + " " + temp.getString("distance") + " " + getString(R.string.kilometer));
                                } else {
                                    dataApi.setWorshipPlaceDistance(getString(R.string.location_permit_warning));
                                }

                                modelPrayPlace.add(dataApi);
                                showPrayPlace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailPrayPlaceActivity.this,
                                    "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(DetailPrayPlaceActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPrayPlace() {
        prayPlaceAdapter = new PrayPlaceAdapter(modelPrayPlace);
        rvPrayPlace.setAdapter(prayPlaceAdapter);
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

            getPrayPlace();
        }
    };
}
