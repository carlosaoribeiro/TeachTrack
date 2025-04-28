package com.carlosribeiro.teachtrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosribeiro.teachtrack.adapter.AgendaAdapter;
import com.carlosribeiro.teachtrack.model.Aula;
import com.carlosribeiro.teachtrack.model.ItemAgenda;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListarAulasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AgendaAdapter adapter;
    private final List<ItemAgenda> listaItens = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editBuscar;
    private Button btnTodos, btnDiario, btnMensal;

    private List<Aula> todasAulas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_aulas);

        recyclerView = findViewById(R.id.recyclerAulas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AgendaAdapter(listaItens);
        recyclerView.setAdapter(adapter);

        adapter.setOnAulaClickListener(new AgendaAdapter.OnAulaClickListener() {
            @Override
            public void onEditar(Aula aula) {
                Intent intent = new Intent(ListarAulasActivity.this, AgendaActivity.class);
                intent.putExtra("aulaId", aula.getId());
                intent.putExtra("aluno", aula.getAluno());
                intent.putExtra("email", aula.getEmail());
                intent.putExtra("tipo", aula.getTipo());
                intent.putExtra("data", aula.getData());
                intent.putExtra("hora", aula.getHora());
                startActivity(intent);
            }

            @Override
            public void onExcluir(Aula aula) {
                db.collection("aulas").document(aula.getId()).delete();
            }
        });

        editBuscar = findViewById(R.id.editBuscar);
        btnTodos = findViewById(R.id.btnTodos);
        btnDiario = findViewById(R.id.btnDiario);
        btnMensal = findViewById(R.id.btnMensal);

        configurarFiltros();
        carregarAulas();
    }

    private void configurarFiltros() {
        btnTodos.setOnClickListener(v -> filtrarLista("Todos"));
        btnDiario.setOnClickListener(v -> filtrarLista("Diário"));
        btnMensal.setOnClickListener(v -> filtrarLista("Mensal"));

        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarLista("Buscar");
            }
        });
    }

    private void carregarAulas() {
        db.collection("aulas")
                .orderBy("data")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    todasAulas.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Aula aula = doc.toObject(Aula.class);
                        aula.setId(doc.getId());
                        todasAulas.add(aula);
                    }

                    filtrarLista("Todos");
                });
    }

    private void filtrarLista(String filtro) {
        String textoBusca = editBuscar.getText().toString().toLowerCase(Locale.ROOT);
        Map<String, List<Aula>> agrupadasPorData = new LinkedHashMap<>();
        listaItens.clear();

        // Pega a data de hoje formatada
        String dataHoje = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new java.util.Date());

        for (Aula aula : todasAulas) {
            boolean atendeFiltro = filtro.equals("Todos") ||
                    (filtro.equals("Mensal") && "Mensal".equalsIgnoreCase(aula.getTipo())) ||
                    (filtro.equals("Diário") && aula.getData() != null && aula.getData().equals(dataHoje));

            boolean atendeBusca = aula.getAluno().toLowerCase(Locale.ROOT).contains(textoBusca);

            if (atendeFiltro && atendeBusca) {
                if (!agrupadasPorData.containsKey(aula.getData())) {
                    agrupadasPorData.put(aula.getData(), new ArrayList<>());
                }
                agrupadasPorData.get(aula.getData()).add(aula);
            }
        }

        for (Map.Entry<String, List<Aula>> entry : agrupadasPorData.entrySet()) {
            String data = entry.getKey();
            List<Aula> aulasDoDia = entry.getValue();

            listaItens.add(new ItemAgenda(data));

            Collections.sort(aulasDoDia, Comparator.comparing(Aula::getHora));
            for (Aula aula : aulasDoDia) {
                listaItens.add(new ItemAgenda(aula));
            }
        }

        adapter.notifyDataSetChanged();
    }
}
