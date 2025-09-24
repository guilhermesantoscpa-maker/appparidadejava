package com.example.appparidadejava;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log; // ALTERAÇÃO: Importar o Log
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {

    private TextView txtTimer;
    private Button btnStart, btnStop, btnReset;

    private Handler handler;
    private boolean isRunning = false;

    private long startTime, timeInMillis, timeSwapBuff, updatedTime = 0L;

    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMillis = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMillis;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);

            txtTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%03d", mins, secs, milliseconds));
            handler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        // ALTERAÇÃO: Bloco para medir o tempo de transição
        long startTime = getIntent().getLongExtra("startTime", 0);
        if (startTime > 0) {
            getWindow().getDecorView().post(() -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                Log.d("ScreenTransition", "Tempo para abrir a StopwatchActivity: " + duration + "ms");
            });
        }

        txtTimer = findViewById(R.id.txtTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);

        handler = new Handler();

        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                this.startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
                isRunning = true;
            }
        });

        btnStop.setOnClickListener(v -> {
            if (isRunning) {
                timeSwapBuff += timeInMillis;
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
            }
        });

        btnReset.setOnClickListener(v -> {
            handler.removeCallbacks(updateTimerThread);
            isRunning = false;
            this.startTime = 0L;
            timeInMillis = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            txtTimer.setText("00:00:000");
        });
    }
}