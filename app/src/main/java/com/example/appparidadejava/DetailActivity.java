package com.example.appparidadejava;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.appparidadejava.utils.DistanceUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class DetailActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtDistancia;

    private double capitalLat;
    private double capitalLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView txtNome = findViewById(R.id.txtNome);
        TextView txtEstado = findViewById(R.id.txtEstado);
        txtDistancia = findViewById(R.id.txtDistancia);

        // Recupera dados da capital vindos do Intent
        String nome = getIntent().getStringExtra("nome");
        String estado = getIntent().getStringExtra("estado");
        capitalLat = getIntent().getDoubleExtra("latitude", 0.0);
        capitalLon = getIntent().getDoubleExtra("longitude", 0.0);

        if (nome != null) txtNome.setText(nome);
        if (estado != null) txtEstado.setText("Estado: " + estado);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Verifica permissões inicialmente e, se OK, tenta obter a localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
        } else {
            // Se já tiver permissão, chama o método que fará nova checagem segura
            getUserLocation();
        }
    }

    private void getUserLocation() {
        // CHECAGEM EXPLÍCITA ANTES DE CHAMAR getLastLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // Se não tem permissão, solicita e retorna sem chamar getLastLocation
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        // Mesmo com a checagem, encapsulamos em try/catch para evitar SecurityException em runtime
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            showDistance(location);
                        } else {
                            txtDistancia.setText("Não foi possível obter sua localização.");
                        }
                    });
        } catch (SecurityException se) {
            // Caso raro — tratamos e mostramos mensagem amigável
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

    // Resposta do usuário à permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Agora que a permissão foi concedida, tentar obter a localização
                getUserLocation();
            } else {
                txtDistancia.setText("Permissão de localização negada.");
            }
        }
    }
}
