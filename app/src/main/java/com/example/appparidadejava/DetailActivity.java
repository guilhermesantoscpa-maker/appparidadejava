package com.example.appparidadejava;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log; // ALTERAÇÃO: Importar o Log
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.appparidadejava.model.Capital;
import com.example.appparidadejava.utils.DistanceUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class DetailActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView txtDistancia;
    private double capitalLat;
    private double capitalLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ALTERAÇÃO: Bloco para medir o tempo de transição
        long startTime = getIntent().getLongExtra("startTime", 0);
        if (startTime > 0) {
            getWindow().getDecorView().post(() -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                Log.d("ScreenTransition", "Tempo para abrir a DetailActivity: " + duration + "ms");
            });
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        TextView txtEstadoNome = findViewById(R.id.txtEstadoNome);
        TextView txtRegiao = findViewById(R.id.txtRegiao);
        TextView txtSigla = findViewById(R.id.txtSigla);
        TextView txtIdIbge = findViewById(R.id.txtIdIbge);
        txtDistancia = findViewById(R.id.txtDistancia);

        Capital capital = (Capital) getIntent().getSerializableExtra("CAPITAL_SELECIONADA");

        if (capital != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbarTitle.setText(capital.getNome());
            txtEstadoNome.setText(capital.getEstadoNome());
            txtRegiao.setText("Região: " + capital.getRegiaoNome());
            txtSigla.setText("Sigla: " + capital.getEstado());
            txtIdIbge.setText("ID (IBGE): " + String.valueOf(capital.getEstadoId()));

            capitalLat = capital.getLatitude();
            capitalLon = capital.getLongitude();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            checkAndRequestLocationPermission();

        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbarTitle.setText("Erro");
            Toast.makeText(this, "Não foi possível carregar os dados da capital.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            showDistance(location);
                        } else {
                            txtDistancia.setText("Não foi possível obter sua localização.");
                        }
                    });
        } catch (SecurityException se) {
            se.printStackTrace();
            txtDistancia.setText("Permissão de localização não disponível.");
        }
    }

    private void showDistance(Location userLocation) {
        double userLat = userLocation.getLatitude();
        double userLon = userLocation.getLongitude();
        double distanciaKm = DistanceUtils.haversine(userLat, userLon, capitalLat, capitalLon);
        txtDistancia.setText("Distância até sua localização: " + String.format("%.2f km", distanciaKm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                txtDistancia.setText("Permissão de localização negada.");
            }
        }
    }
}