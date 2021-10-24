package com.example.ourshop.api;

public class Api {
    // Base API
    public static String BaseUrl = "https://fpi-pariwisata-palembang-api.herokuapp.com";

    // API for Hotel
    public static String Hotel = BaseUrl + "/penginapan";
    public static String DetailHotel = BaseUrl + "/kuliner/{id}";

    // API for Kuliner
    public static String Kuliner = BaseUrl + "/kuliner";
    public static String DetailKuliner = BaseUrl + "/kuliner/{id}";

    // API for Place of Worships
    public static String JenisTempatIbadah = BaseUrl + "/tempatibadah";
    public static String TempatIbadah = BaseUrl + "/tempatibadah/{id}";

    // API for Wisata
    public static String Wisata = BaseUrl + "/wisata";
    public static String DetailWisata = BaseUrl + "/wisata/{id}";


}
