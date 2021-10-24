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
    WisataAdapter kulinerAdapter;
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

        if (
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
        ) {
            // You can use the API that requires the permission.
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if(gps_enabled && network_enabled) {
                if (
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    finish();
                }

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            } else {
                API = Api.Wisata;
                getWisata();
            }
        } else {
            Log.e("TAG IS ANYTHING", "YOUR MESSAGE" + "only here");
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
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

                                dataApi.setKategoriWisata("random dulu");

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
        kulinerAdapter = new WisataAdapter(WisataActivity.this, modelWisata, this);
        rvWisata.setAdapter(kulinerAdapter);
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

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // You can use the API that requires the permission.
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                        finish();
                    }

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

                    API = Api.Wisata + "?" + "longitude="+ longitude + "&" + "latitude" + latitude;

                    getWisata();
                } else {
                    API = Api.Wisata;
                    getWisata();
                }
            });
}
