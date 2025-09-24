package com.example.appparidadejava;

import android.app.Application;

public class MeuApp extends Application {

    // Variável estática para ser acessível de qualquer lugar do app
    public static long startTime;

    @Override
    public void onCreate() {
        super.onCreate();
        // Marca o tempo exato em que o processo do app foi iniciado
        startTime = System.currentTimeMillis();
    }
}