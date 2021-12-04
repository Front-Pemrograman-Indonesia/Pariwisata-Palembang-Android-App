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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.ourshop.adapter.WisataAdapter;
import com.example.ourshop.api.Api;
import com.example.ourshop.decoration.LayoutMarginDecoration;
import com.example.ourshop.model.ModelWisata;
import com.example.ourshop.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WisataActivity extends AppCompatActivity implements WisataAdapter.onSelectData {

    RecyclerView rvWisata;
    LayoutMarginDecoration gridMargin;
    WisataAdapter wisataAdapter;
    ProgressDialog progressDialog;
    List<ModelWisata> modelWisata = new ArrayList<>();
    Toolbar tbWisata;
    public double longitude;
    public double latitude;
    public String API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisata);
        Log.e("TAG IS ANYTHING", "Final AP1I" + API);
        tbWisata = findViewById(R.id.toolbar_wisata);
        tbWisata.setTitle("Daftar Wisata Palembang");
        setSupportActionBar(tbWisata);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan data...");

        rvWisata = findViewById(R.id.rvWisata);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                2, RecyclerView.VERTICAL, false);
        rvWisata.setLayoutManager(mLayoutManager);
        gridMargin = new LayoutMarginDecoration(2, Tools.dp2px(this, 4));
        rvWisata.addItemDecoration(gridMargin);
        rvWisata.setHasFixedSize(true);
        Log.e("TAG IS ANYTHING", "Final API2" + API);

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
                    API = Api.Wisata + "?" + "longitude=" + longitude + "&" + "latitude" + latitude;

                    getWisata();
                }
            } else {
                API = Api.Wisata;
                getWisata();
            }
        } else {
            // User did not grant permit to acces the location
            // Use the API that did not need any permit from the user (without latitude and longitude)
            API = Api.Wisata;
            getWisata();
        }
    }

    private void getWisata() {
        progressDialog.show();
        Log.e("TAG IS ANYTHING", "Final API" + API);
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
                                ModelWisata dataApi = new ModelWisata();

                                dataApi.setIdWisata(temp.getString("id"));
                                dataApi.setTxtNamaWisata(temp.getString("name"));
                                dataApi.setGambarWisata(Api.BaseUrl + temp.getString("thumbnail"));
                                dataApi.setKoordinatWisata(temp.getString("latitude") + ',' + temp.getString("longitude"));
                                if(temp.getBoolean("locationStatus")){
                                    dataApi.setJarakWisata("jarak anda ke tempat wisata: " + temp.getString("distance") + " km");
                                } else {
                                    dataApi.setJarakWisata("Izinkan dan hidupkan lokasi untuk dapat mengetahui jarak ke tempat wisata");
                                }
                                modelWisata.add(dataApi);
                                showWisata();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WisataActivity.this,
                                    "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(WisataActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.e("TAG IS ANYTHING", "setting the longitude latitude" + latitude + longitude);
            API = Api.Wisata + "?" + "longitude=" + longitude + "&" + "latitude" + latitude;

            getWisata();
        }
    };

    private void showWisata() {
        wisataAdapter = new WisataAdapter(WisataActivity.this, modelWisata, this);
        rvWisata.setAdapter(wisataAdapter);
    }

    @Override
    public void onSelected(ModelWisata modelWisata) {
        Intent intent = new Intent(WisataActivity.this, DetailWisataActivity.class);
        intent.putExtra("detailWisata", modelWisata);
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
}
