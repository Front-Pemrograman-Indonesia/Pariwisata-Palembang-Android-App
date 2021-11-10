package com.example.ourshop.model;

import java.io.Serializable;

public class ModelPrayPlace implements Serializable {

    private String txtTempatIbadah;
    private double latitude, longitude;

    // Data Tempat Ibadah
    public void setTxtTempatIbadah(String txtTempatIbadah) {
        this.txtTempatIbadah = txtTempatIbadah;
    }
    public String getTxtTempatIbadah() {
        return txtTempatIbadah;
    }

    // Data latitude
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLatitude() {
        return latitude;
    }

    // Data Longitude
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLongitude() {
        return longitude;
    }
}
