package com.example.ourshop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
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
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan data");

        rvPrayPlace = findViewById(R.id.rvPrayPlace);
        rvPrayPlace.setHasFixedSize(true);
        rvPrayPlace.setLayoutManager(new LinearLayoutManager(this));

        modelHotel = (ModelHotel) getIntent().getSerializableExtra("detailHotel");
        if (modelHotel != null) {
            tbPlace.setTitle(modelHotel.getTxtNamaHotel());
            //get String
            id = modelHotel.get_id();
        }

        getPrayPlace();
    }

    private void getPrayPlace() {
        progressDialog.show();
        String API = Api.TempatIbadah + "?language=" + Locale.getDefault().getLanguage();
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
}
