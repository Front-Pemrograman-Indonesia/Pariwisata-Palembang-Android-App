package com.example.ourshop.model;

import java.io.Serializable;

public class ModelWisata implements Serializable {

    private String idWisata, txtJarakWisata, txtNamaWisata, GambarWisata, KategoriWisata;

    // Data Wisata
    public String getIdWisata() {
        return idWisata;
    }
    public void setIdWisata(String idWisata) {
        this.idWisata = idWisata;
    }

    // Data Jarak Wisata
    public String getJarakWisata() {
        return txtJarakWisata;
    }
    public void setJarakWisata(String txtJarakWisata) {
        this.txtJarakWisata = txtJarakWisata;
    }

    // Data Nama Wisata
    public String getTxtNamaWisata() {
        return txtNamaWisata;
    }
    public void setTxtNamaWisata(String txtNamaWisata) {
        this.txtNamaWisata = txtNamaWisata;
    }

    // Data Gambar Thumbnail Wisata
    public String getGambarWisata() {
        return GambarWisata;
    }
    public void setGambarWisata(String gambarWisata) {
        GambarWisata = gambarWisata;
    }
}
