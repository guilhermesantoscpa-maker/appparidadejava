package com.example.appparidadejava.network;

import com.example.appparidadejava.model.Estado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IBGEService {
    @GET("localidades/estados")
    Call<List<Estado>> getEstados();
}
