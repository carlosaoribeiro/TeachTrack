package com.carlosribeiro.teachtrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAgendarAula = findViewById(R.id.btnAgendarAula);
        btnAgendarAula.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgendaActivity.class);
            startActivity(intent);
        });

        // Botão: Listar Alunos
        Button btnListarAlunos = findViewById(R.id.btnListarAlunos);
        btnListarAlunos.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListarAlunosActivity.class);
            startActivity(intent);
        });

        // ✅ Botão: Ver Aulas Agendadas
        Button btnVerAulas = findViewById(R.id.btnVerAulas);
        btnVerAulas.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, com.carlosribeiro.teachtrack.ListarAulasActivity.class);
            startActivity(intent);
        });
    }
}
