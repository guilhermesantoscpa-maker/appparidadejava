package com.example.appparidadejava.model;

public class Capital {
    private String nome;
    private String estado;
    private double latitude;
    private double longitude;

    public Capital(String nome, String estado, double latitude, double longitude) {
        this.nome = nome;
        this.estado = estado;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public String getEstado() {
        return estado;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
