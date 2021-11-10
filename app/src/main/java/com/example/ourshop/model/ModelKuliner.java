package com.example.ourshop.model;

import java.io.Serializable;

public class ModelKuliner implements Serializable {

    private String idKuliner, txtNamaKuliner, txtAlamatKuliner, txtOpenTime, Koordinat, GambarKuliner, KategoriKuliner;

    // Data Id Kuliner
    public void setIdKuliner(String idKuliner) {
        this.idKuliner = idKuliner;
    }
    public String getIdKuliner() {
        return idKuliner;
    }

    // Data Nama Kuliner
    public void setTxtNamaKuliner(String txtNamaKuliner) {
        this.txtNamaKuliner = txtNamaKuliner;
    }
    public String getTxtNamaKuliner() {
        return txtNamaKuliner;
    }

    // Data Alamat Kuliner
    public void setTxtAlamatKuliner(String txtAlamatKuliner) {
        this.txtAlamatKuliner = txtAlamatKuliner;
    }
    public String getTxtAlamatKuliner() {
        return txtAlamatKuliner;
    }

    // Data Waktu Buka
    public void setTxtOpenTime(String txtOpenTime) {
        this.txtOpenTime = txtOpenTime;
    }
    public String getTxtOpenTime() {
        return txtOpenTime;
    }

    // Data Koordinat
    public void setKoordinat(String koordinat) {
        this.Koordinat = koordinat;
    }
    public String getKoordinat() {
        return Koordinat;
    }

    // Data Thumbnail
    public void setGambarKuliner(String gambarKuliner) {
        this.GambarKuliner = gambarKuliner;
    }
    public String getGambarKuliner() {
        return GambarKuliner;
    }
}
