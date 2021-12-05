package com.example.ourshop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.ourshop.R;
import com.example.ourshop.adapter.ViewPagerAdapter;
import com.example.ourshop.api.Api;
import com.example.ourshop.model.ModelWisata;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class DetailWisataActivity extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar tbDetailWisata;
    TextView tvNamaWisata, tvDescWisata, tvOpenHours;
    String idWisata, NamaWisata, Desc;
    ModelWisata modelWisata;
    GoogleMap googleMaps;
    private String koordinat;
    private ArrayList<String> images = new ArrayList<>();

    //creating object of ViewPager
    ViewPager mViewPager;

    //Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wisata);

        tbDetailWisata = findViewById(R.id.tbDetailWisata);
        tbDetailWisata.setTitle(R.string.tourist_destination_details_header);
        setSupportActionBar(tbDetailWisata);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.e("TAG IS ANYTHING", "setting the longitude latitude44" + modelWisata);


        modelWisata = (ModelWisata) getIntent().getSerializableExtra("detailWisata");

        //show maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.e("TAG IS ANYTHING", "setting the longitude latitude44" + modelWisata.getKoordinatWisata());
        if (modelWisata != null) {
            idWisata = modelWisata.getIdWisata();
            NamaWisata = modelWisata.getTxtNamaWisata();

            //set Id
            tvNamaWisata = findViewById(R.id.tvNamaWisata);
            tvDescWisata = findViewById(R.id.tvDescWisata);
            tvOpenHours = findViewById(R.id.tvOpenHours);

            getDetailWisata();
        }
    }

    private void getDetailWisata() {
        String API = Api.DetailWisata + "?language=" + Locale.getDefault().getLanguage();
        AndroidNetworking.get(API)
                .addPathParameter("id", idWisata)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject respon1 = response.getJSONObject("data");

                            //set Text
                            tvNamaWisata.setText(respon1.getString("name"));
                            tvDescWisata.setText(respon1.getString("description"));
                            tvOpenHours.setText(respon1.getString("openHours"));
                            koordinat = respon1.getString("latitude") + respon1.getString("longitude");

                            // set the images slider with pager
                            JSONArray arrJson = respon1.getJSONArray("gallery");

                            for(int i = 0; i < arrJson.length(); i++)
                                images.add(Api.BaseUrl + arrJson.getString(i));

                            //Initializing the ViewPager Object
                            mViewPager = findViewById(R.id.viewPagerMain);

                            //Initializing the ViewPagerAdapter
                            mViewPagerAdapter = new ViewPagerAdapter(DetailWisataActivity.this, images);

                            //Adding the Adapter to the ViewPager
                            mViewPager.setAdapter(mViewPagerAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailWisataActivity.this,
                                    "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(DetailWisataActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Log.e("TAG IS ANYTHING", "setting the longitude latitude44" + modelWisata.getKoordinatWisata().split(","));
        //get LatLong
        String[] latlong = modelWisata.getKoordinatWisata().split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);

        googleMaps = googleMap;
        LatLng latLng = new LatLng(latitude, longitude);
        googleMaps.addMarker(new MarkerOptions().position(latLng).title(modelWisata.getTxtNamaWisata()));
        googleMaps.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMaps.getUiSettings().setAllGesturesEnabled(true);
        googleMaps.getUiSettings().setZoomGesturesEnabled(true);
        googleMaps.setTrafficEnabled(true);
    }
}
