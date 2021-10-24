package com.example.ourshop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.ourshop.R;
import com.example.ourshop.api.Api;
import com.example.ourshop.model.ModelKuliner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailKulinerActivity extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar tbDetailKuliner;
    GoogleMap googleMaps;
    TextView tvNamaKuliner, tvAddressKuliner, tvPhoneKuliner, tvOpenTime, tvDesc;
    String idKuliner, NamaKuliner, AddressKuliner, PhoneKuliner, OpenTime, Desc;
    ModelKuliner modelKuliner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kuliner);
        Log.e("TAG IS ANYTHING", "setting the longitude latitude11" + modelKuliner);
        tbDetailKuliner = findViewById(R.id.tbDetailKuliner);
        tbDetailKuliner.setTitle("Detail Kuliner");
        setSupportActionBar(tbDetailKuliner);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.e("TAG IS ANYTHING", "setting the longitude latitude22" + modelKuliner);
        //show maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.e("TAG IS ANYTHING", "setting the longitude latitude33" + modelKuliner);
        modelKuliner = (ModelKuliner) getIntent().getSerializableExtra("detailKuliner");
        if (modelKuliner != null) {
            idKuliner = modelKuliner.getIdKuliner();
            NamaKuliner = modelKuliner.getTxtNamaKuliner();
            Log.e("TAG IS ANYTHING", "setting the longitude latitude44" + modelKuliner);
            //set Id
            tvNamaKuliner = findViewById(R.id.tvNamaKuliner);
            getDetailKuliner();
        }
    }

    private void getDetailKuliner() {
        AndroidNetworking.get(Api.DetailKuliner)
                .addPathParameter("id", idKuliner)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Log.e("TAG IS ANYTHING", "setting the longitude latitude44" + response);
                                //get String Api
                                JSONObject data = response.getJSONObject("data");
                                NamaKuliner = data.getString("name");

                                //set Text
                                tvNamaKuliner.setText(NamaKuliner);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(DetailKulinerActivity.this,
                                        "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(DetailKulinerActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //get LatLong
        String[] latlong = modelKuliner.getKoordinat().split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);

        googleMaps = googleMap;
        LatLng latLng = new LatLng(latitude, longitude);
        googleMaps.addMarker(new MarkerOptions().position(latLng).title(NamaKuliner));
        googleMaps.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMaps.getUiSettings().setAllGesturesEnabled(true);
        googleMaps.getUiSettings().setZoomGesturesEnabled(true);
        googleMaps.setTrafficEnabled(true);
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
