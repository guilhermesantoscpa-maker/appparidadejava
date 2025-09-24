package com.example.appparidadejava;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // Importar o Log
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appparidadejava.adapter.CapitalAdapter;
import com.example.appparidadejava.model.Capital;
import com.example.appparidadejava.model.Estado;
import com.example.appparidadejava.network.IBGEService;
import com.example.appparidadejava.network.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // ... (suas outras variáveis de classe)
    private RecyclerView recyclerView;
    private CapitalAdapter capitalAdapter;
    private ProgressBar progressBar;
    private EditText edtBusca;
    private Button btnObterLocalizacao;
    private RelativeLayout rootLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_MAIN = 1002;
    private boolean isListVisible = false;
    private boolean coldStartMeasured = false; // Flag para medir o cold start apenas uma vez

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ... (seu código onCreate)
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        edtBusca = findViewById(R.id.edtBusca);
        btnObterLocalizacao = findViewById(R.id.btnObterLocalizacao);
        rootLayout = findViewById(R.id.rootLayout);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setupRecyclerView();
        setupSearch();
        setupLocationButton();
        setupRootLayoutListener();
        fetchEstadosFromAPI();
    }

    private void fetchEstadosFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        IBGEService service = RetrofitClient.getClient().create(IBGEService.class);
        Call<List<Estado>> call = service.getEstados();

        // ALTERAÇÃO: Marcar tempo de início da chamada da API
        long apiStartTime = System.currentTimeMillis();

        call.enqueue(new Callback<List<Estado>>() {
            @Override
            public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {
                // ALTERAÇÃO: Marcar tempo de fim da chamada da API e calcular duração
                long apiEndTime = System.currentTimeMillis();
                long apiDuration = apiEndTime - apiStartTime;
                Log.d("PerformanceMetrics", "Tempo de Acesso à API: " + apiDuration + "ms");

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Estado> estadosDaAPI = response.body();
                    List<Capital> capitaisDoJson = loadCapitalsFromJSON();
                    List<Capital> listaFinal = mergeApiAndLocalData(estadosDaAPI, capitaisDoJson);

                    // ALTERAÇÃO: Marcar tempo de início da renderização
                    long renderStartTime = System.currentTimeMillis();

                    capitalAdapter.updateData(listaFinal);

                    // Este post() agora mede tanto o Cold Start quanto a Renderização
                    recyclerView.post(() -> {
                        long endTime = System.currentTimeMillis();

                        // Medir tempo de renderização
                        long renderDuration = endTime - renderStartTime;
                        Log.d("PerformanceMetrics", "Tempo de Renderização da Lista: " + renderDuration + "ms");

                        // Medir o Cold Start total (apenas na primeira vez)
                        if (!coldStartMeasured) {
                            long coldStartDuration = endTime - MeuApp.startTime;
                            Log.d("ColdStart", "Tempo de Início do Aplicativo (Total): " + coldStartDuration + "ms");
                            coldStartMeasured = true;
                        }
                    });
                } else {
                    showError("Falha ao buscar dados dos estados.");
                }
            }

            @Override
            public void onFailure(Call<List<Estado>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Erro de rede: " + t.getMessage());
            }
        });
    }


    // ... (O restante da sua classe MainActivity continua igual, não precisa mudar mais nada)
    private void setupRootLayoutListener() {
        rootLayout.setOnClickListener(v -> {
            edtBusca.clearFocus();
            hideKeyboard(v);
            recyclerView.setVisibility(View.GONE);
            isListVisible = false;
        });
    }
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void setupSearch() {
        edtBusca.setOnClickListener(v -> {
            if (!isListVisible) {
                recyclerView.setVisibility(View.VISIBLE);
                isListVisible = true;
            }
        });
        edtBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                capitalAdapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_stopwatch) {
            Intent intent = new Intent(this, StopwatchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupLocationButton() {
        btnObterLocalizacao.setOnClickListener(v -> checkAndRequestLocationPermission());
    }
    private void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_MAIN);
        } else {
            getLocationAndShowSuccess();
        }
    }
    private void getLocationAndShowSuccess() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(this, "Não foi possível obter a localização. Verifique se o GPS está ativado.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
            showError("Erro de segurança ao obter localização.");
        }
    }
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sucesso")
                .setMessage("Sua localização foi salva! Agora clique em uma capital para ver a distância.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_MAIN) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndShowSuccess();
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        capitalAdapter = new CapitalAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(capitalAdapter);
    }
    private List<Capital> mergeApiAndLocalData(List<Estado> estados, List<Capital> capitais) {
        List<Capital> listaFinal = new ArrayList<>();
        for (Estado estado : estados) {
            for (Capital capital : capitais) {
                if (capital.getEstado().equalsIgnoreCase(estado.getSigla())) {
                    capital.setEstadoNome(estado.getNome());
                    capital.setRegiaoNome(estado.getRegiao().getNome());
                    capital.setEstadoId(estado.getId());
                    listaFinal.add(capital);
                    break;
                }
            }
        }
        return listaFinal;
    }
    private List<Capital> loadCapitalsFromJSON() {
        try {
            InputStream is = getResources().openRawResource(R.raw.capitais);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type capitalListType = new TypeToken<ArrayList<Capital>>(){}.getType();
            return gson.fromJson(json, capitalListType);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}