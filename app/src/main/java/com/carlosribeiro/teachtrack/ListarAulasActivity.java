package com.carlosribeiro.teachtrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosribeiro.teachtrack.adapter.AulaAdapter;
import com.carlosribeiro.teachtrack.model.Aula;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListarAulasActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private EditText editBuscarAula;
    private AulaAdapter adapter;
    private List<Aula> listaAulas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_aulas);

        recyclerView = findViewById(R.id.recyclerAulas);
        editBuscarAula = findViewById(R.id.editBuscarAula);

        adapter = new AulaAdapter(listaAulas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        editBuscarAula.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarAulas(s.toString());
            }
        });

        adapter.setOnAulaLongClickListener(this::abrirEdicao);
        carregarAulas();
    }

    private void carregarAulas() {
        db.collection("aulas")
                .get()
                .addOnSuccessListener(result -> {
                    listaAulas.clear();
                    for (QueryDocumentSnapshot doc : result) {
                        Aula aula = doc.toObject(Aula.class);
                        aula.setId(doc.getId());
                        listaAulas.add(aula);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void filtrarAulas(String texto) {
        List<Aula> filtrada = new ArrayList<>();
        for (Aula aula : listaAulas) {
            if (aula.getAluno() != null && aula.getAluno().toLowerCase().contains(texto.toLowerCase())) {
                filtrada.add(aula);
            }
        }
        adapter = new AulaAdapter(filtrada);
        recyclerView.setAdapter(adapter);
        adapter.setOnAulaLongClickListener(this::abrirEdicao);
    }

    private void abrirEdicao(Aula aula) {
        Intent intent = new Intent(this, AgendaActivity.class);
        intent.putExtra("aulaId", aula.getId());
        intent.putExtra("aluno", aula.getAluno());
        intent.putExtra("email", aula.getEmail());
        intent.putExtra("tipo", aula.getTipo());

        if ("Mensal".equals(aula.getTipo()) && aula.getHorariosSemana() != null) {
            for (Map.Entry<String, String> entry : aula.getHorariosSemana().entrySet()) {
                intent.putExtra("horario_" + entry.getKey(), entry.getValue());
            }
        } else {
            intent.putExtra("data", aula.getData());
            intent.putExtra("hora", aula.getHora());
        }

        startActivity(intent);
    }
}
