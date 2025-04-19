package com.carlosribeiro.teachtrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 6000;
    private static final int REQUEST_PERMISSIONS_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logoTeachTrack);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.fade_pulse);
        logo.startAnimation(pulse);

        solicitarPermissoes();
    }

    private void solicitarPermissoes() {
        String[] permissoes = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        List<String> permissoesParaPedir = new ArrayList<>();

        for (String permissao : permissoes) {
            if (ContextCompat.checkSelfPermission(this, permissao) != PackageManager.PERMISSION_GRANTED) {
                permissoesParaPedir.add(permissao);
            }
        }

        if (!permissoesParaPedir.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissoesParaPedir.toArray(new String[0]),
                    REQUEST_PERMISSIONS_CODE);
        } else {
            iniciarApp();
        }
    }

    private void iniciarApp() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, SPLASH_DURATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean todasPermissoesConcedidas = true;

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                todasPermissoesConcedidas = false;
                break;
            }
        }

        if (todasPermissoesConcedidas) {
            iniciarApp();
        } else {
            Toast.makeText(this, "Permissões obrigatórias não concedidas. Encerrando o app.", Toast.LENGTH_LONG).show();
            finishAffinity(); // Encerra o app completamente
        }
    }
}
