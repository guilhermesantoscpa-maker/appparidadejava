package com.example.appparidadejava.model;

import java.io.Serializable;

public class Regiao implements Serializable {
    private int id;
    private String sigla;
    private String nome;

    // Getters
    public int getId() { return id; }
    public String getSigla() { return sigla; }
    public String getNome() { return nome; }
}


