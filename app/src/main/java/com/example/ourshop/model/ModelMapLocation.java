package com.example.ourshop.model;

import com.google.android.gms.maps.model.LatLng;

public class ModelMapLocation {

    public String name;
    public LatLng center;

    // Model lokasi
    public ModelMapLocation() {}
    public ModelMapLocation(String name, double lat, double lng) {
        this.name = name;
        this.center = new LatLng(lat, lng);
    }
}
