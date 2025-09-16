package com.example.appparidadejava;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appparidadejava.adapter.CapitalAdapter;
import com.example.appparidadejava.model.Capital;
import com.example.appparidadejava.model.Estado;
import com.example.appparidadejava.network.IBGEService;
import com.example.appparidadejava.network.RetrofitClient;
import com.example.appparidadejava.utils.JsonHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private List<Capital> listaCapitais = new ArrayList<>();
    private List<Capital> listaFiltrada = new ArrayList<>();
    private CapitalAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler = findViewById(R.id.recyclerCapitais);
        EditText edtBusca = findViewById(R.id.edtBusca);
        Button btnLocalizacao = findViewById(R.id.btnLocalizacao);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Carregar JSON local (nome, estado, latitude, longitude)
        try {
            JSONArray json = new JSONArray(JsonHelper.loadJSON(this, R.raw.capitais));
            for (int i = 0; i < json.length(); i++) {
                String nome = json.getJSONObject(i).getString("nome");
                String estado = json.getJSONObject(i).getString("estado");
                double latitude = json.getJSONObject(i).optDouble("latitude", 0.0);
                double longitude = json.getJSONObject(i).optDouble("longitude", 0.0);

                listaCapitais.add(new Capital(nome, estado, latitude, longitude));
            }
            listaFiltrada.addAll(listaCapitais);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new CapitalAdapter(this, listaFiltrada);
        recycler.setAdapter(adapter);

        // Chamada API IBGE
        IBGEService service = RetrofitClient.getClient().create(IBGEService.class);
        service.getEstados().enqueue(new Callback<List<Estado>>() {
            @Override
            public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Estado e : response.body()) {
                        // API não retorna lat/lng → usamos 0.0 como placeholder
                        listaCapitais.add(new Capital(e.getNome(), e.getSigla(), 0.0, 0.0));
                    }
                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaCapitais);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Estado>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // Filtro em tempo real
        edtBusca.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                listaFiltrada.clear();
                for (Capital c : listaCapitais) {
                    if (c.getNome().toLowerCase().contains(s.toString().toLowerCase())
                            || c.getEstado().toLowerCase().contains(s.toString().toLowerCase())) {
                        listaFiltrada.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnLocalizacao.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Toast.makeText(this, "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
