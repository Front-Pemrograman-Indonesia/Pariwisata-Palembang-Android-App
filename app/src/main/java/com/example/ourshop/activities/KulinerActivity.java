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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.ourshop.R;
import com.example.ourshop.adapter.KulinerAdapter;
import com.example.ourshop.api.Api;
import com.example.ourshop.decoration.LayoutMarginDecoration;
import com.example.ourshop.model.ModelKuliner;
import com.example.ourshop.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KulinerActivity extends AppCompatActivity implements KulinerAdapter.onSelectData {

    RecyclerView rvKuliner;
    LayoutMarginDecoration gridMargin;
    KulinerAdapter kulinerAdapter;
    ProgressDialog progressDialog;
    List<ModelKuliner> modelKuliner = new ArrayList<>();
    Toolbar tbKuliner;
    public double longitude;
    public double latitude;
    private String API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuliner);

        // set the screen header
        tbKuliner = findViewById(R.id.toolbar_kuliner);
        tbKuliner.setTitle(R.string.restaurant_header);

        setSupportActionBar(tbKuliner);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.wait);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.wait_description));

        rvKuliner = findViewById(R.id.rvKuliner);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                2, RecyclerView.VERTICAL, false);
        rvKuliner.setLayoutManager(mLayoutManager);
        gridMargin = new LayoutMarginDecoration(2, Tools.dp2px(this, 4));
        rvKuliner.addItemDecoration(gridMargin);
        rvKuliner.setHasFixedSize(true);

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
                    API = Api.Kuliner + "?" + "longitude=" + longitude + "&" + "latitude" + latitude + "&" + "language=" + Locale.getDefault().getLanguage();

                    getKuliner();
                }
            } else {
                API = Api.Kuliner + "?language=" + Locale.getDefault().getLanguage();
                getKuliner();
            }
        } else {
            // User did not grant permit to acces the location
            // Use the API that did not need any permit from the user (without latitude and longitude)
            API = Api.Kuliner + "?language=" + Locale.getDefault().getLanguage();
            getKuliner();
        }
        getKuliner();
    }

    private void getKuliner() {
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
                                ModelKuliner dataApi = new ModelKuliner();

                                dataApi.setIdKuliner(temp.getString("id"));
                                dataApi.setTxtNamaKuliner(temp.getString("name"));
                                dataApi.setKoordinat(temp.getString("latitude") + ", " + temp.getString("longitude"));
                                dataApi.setGambarKuliner(Api.BaseUrl + temp.getString("thumbnail"));
                                dataApi.setRestaurantDistance(temp.getString("distance"));
                                if(temp.getBoolean("locationStatus")){
                                    dataApi.setRestaurantDistance(getString(R.string.tourist_destination_distance) + " " + temp.getString("distance") + " " + getString(R.string.kilometer));
                                } else {
                                    dataApi.setRestaurantDistance(getString(R.string.location_permit_warning));
                                }

                                modelKuliner.add(dataApi);
                                showKuliner();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(KulinerActivity.this,
                                    "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(KulinerActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showKuliner() {
        kulinerAdapter = new KulinerAdapter(KulinerActivity.this, modelKuliner, this);
        rvKuliner.setAdapter(kulinerAdapter);
    }

    @Override
    public void onSelected(ModelKuliner modelKuliner) {
        Intent intent = new Intent(KulinerActivity.this, DetailKulinerActivity.class);
        intent.putExtra("detailKuliner", modelKuliner);
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

            getKuliner();
        }
    };
}
