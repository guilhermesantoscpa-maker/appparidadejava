package com.example.appparidadejava.model;

import java.io.Serializable;

public class Capital implements Serializable {
    // Campos do JSON
    private String nome;
    private String estado; // Esta é a sigla
    private double latitude;
    private double longitude;

    // Novos campos que virão da API
    private String estadoNome;
    private String regiaoNome;
    private int estadoId;

    // Getters para todos os campos
    public String getNome() { return nome; }
    public String getEstado() { return estado; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getEstadoNome() { return estadoNome; }
    public String getRegiaoNome() { return regiaoNome; }
    public int getEstadoId() { return estadoId; }

    // Setters para os novos campos (usaremos na MainActivity)
    public void setEstadoNome(String estadoNome) { this.estadoNome = estadoNome; }
    public void setRegiaoNome(String regiaoNome) { this.regiaoNome = regiaoNome; }
    public void setEstadoId(int estadoId) { this.estadoId = estadoId; }
}