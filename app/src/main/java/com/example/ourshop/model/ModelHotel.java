package com.example.ourshop.model;

import java.io.Serializable;

public class ModelHotel implements Serializable {

    private String _id, txtNamaHotel, txtAlamatHotel, txtNoTelp, Koordinat, GambarHotel, HotelDistance;

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public String getTxtNamaHotel() {
        return txtNamaHotel;
    }

    public void setTxtNamaHotel(String txtNamaHotel) {
        this.txtNamaHotel = txtNamaHotel;
    }

    public String getTxtAlamatHotel() {
        return txtAlamatHotel;
    }

    public void setTxtAlamatHotel(String txtAlamatHotel) {
        this.txtAlamatHotel = txtAlamatHotel;
    }

    public String getTxtNoTelp() {
        return txtNoTelp;
    }

    public void setTxtNoTelp(String txtNoTelp) {
        this.txtNoTelp = txtNoTelp;
    }

    public String getKoordinat() {
        return Koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.Koordinat = koordinat;
    }

    public String getGambarHotel() {
        return GambarHotel;
    }

    public void setGambarHotel(String gambarHotel) {
        this.GambarHotel = gambarHotel;
    }

    // Hotel Distance
    public void setHotelDistance(String hotelDistance) {
        this.HotelDistance = hotelDistance;
    }
    public String getHotelDistance() {
        return HotelDistance;
    }
}
