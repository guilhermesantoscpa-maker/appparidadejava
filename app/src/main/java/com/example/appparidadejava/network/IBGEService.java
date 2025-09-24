package com.example.appparidadejava.network;

import com.example.appparidadejava.model.Estado;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IBGEService {
    // Endpoint para buscar todos os estados do Brasil
    @GET("localidades/estados?orderBy=nome") // Adicionei ?orderBy=nome para vir em ordem alfabética
    Call<List<Estado>> getEstados();
}