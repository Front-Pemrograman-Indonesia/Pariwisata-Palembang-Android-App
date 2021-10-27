package com.example.ourshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class PrayPlaceActivity extends AppCompatActivity implements HotelAdapter.onSelectData {

    RecyclerView rvHotel;
    HotelAdapter hotelAdapter;
    ProgressDialog progressDialog;
    List<ModelHotel> modelHotel = new ArrayList<>();
    Toolbar tbHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        tbHotel = findViewById(R.id.toolbar_hotel);
        tbHotel.setTitle("Daftar Tempat Ibadah");
        setSupportActionBar(tbHotel);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan data...");

        rvHotel = findViewById(R.id.rvHotel);
        rvHotel.setHasFixedSize(true);
        rvHotel.setLayoutManager(new LinearLayoutManager(this));

        getHotel();
    }

    private void getHotel() {
        progressDialog.show();
        AndroidNetworking.get(Api.JenisTempatIbadah)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray playerArray = response.getJSONArray("data");
                            Log.e("TAG IS ANYTHING", "setting the longitude latitude" + playerArray);
                            for (int i = 0; i < playerArray.length(); i++) {
                                JSONObject temp = playerArray.getJSONObject(i);
                                ModelHotel dataApi = new ModelHotel();

                                String coordinate = "0.0000" + ", " + "0.0000";
                                String thumbnailEndpoint = Api.BaseUrl + temp.getString("thumbnail");

                                dataApi.set_id(temp.getString("id"));
                                dataApi.setTxtNamaHotel(temp.getString("name"));
                                dataApi.setKoordinat(coordinate);
                                dataApi.setGambarHotel(thumbnailEndpoint);

                                modelHotel.add(dataApi);
                                showHotel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PrayPlaceActivity.this,
                                    "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(PrayPlaceActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showHotel() {
        hotelAdapter = new HotelAdapter(PrayPlaceActivity.this, modelHotel, this);
        rvHotel.setAdapter(hotelAdapter);
    }

    @Override
    public void onSelected(ModelHotel modelHotel) {
        Intent intent = new Intent(PrayPlaceActivity.this, DetailPrayPlaceActivity.class);
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
}
