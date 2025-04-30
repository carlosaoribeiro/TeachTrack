package com.carlosribeiro.teachtrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosribeiro.teachtrack.adapter.AgendaAdapter;
import com.carlosribeiro.teachtrack.model.Aula;
import com.carlosribeiro.teachtrack.model.ItemAgenda;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class ListarAulasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AgendaAdapter adapter;
    private final List<ItemAgenda> listaItens = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editBuscar;
    private Button btnTodos, btnDiario, btnMensal;
    private List<Aula> todasAulas = new ArrayList<>();
    private String userIdAtual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_aulas);

        userIdAtual = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                db.collection("aulas").document(aula.getId()).delete()
                        .addOnSuccessListener(unused -> carregarAulas());
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
                .whereEqualTo("userId", userIdAtual)
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
        String textoBusca = editBuscar.getText() != null ? editBuscar.getText().toString().toLowerCase(Locale.ROOT) : "";
        Map<String, List<Aula>> agrupadasPorData = new LinkedHashMap<>();
        listaItens.clear();

        String dataHoje = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        for (Aula aula : todasAulas) {
            boolean atendeFiltro = filtro.equals("Todos") || filtro.equals("Buscar") ||
                    (filtro.equals("Mensal") && "Mensal".equalsIgnoreCase(aula.getTipo())) ||
                    (filtro.equals("Diário") && aula.getData() != null && aula.getData().equals(dataHoje));

            String nomeAluno = aula.getAluno() != null ? aula.getAluno() : "";
            boolean atendeBusca = nomeAluno.toLowerCase(Locale.ROOT).contains(textoBusca);

            if (atendeFiltro && atendeBusca) {
                agrupadasPorData
                        .computeIfAbsent(aula.getData() != null ? aula.getData() : "Data não definida", k -> new ArrayList<>())
                        .add(aula);
            }
        }

        for (Map.Entry<String, List<Aula>> entry : agrupadasPorData.entrySet()) {
            String data = entry.getKey();
            List<Aula> aulasDoDia = entry.getValue();

            listaItens.add(new ItemAgenda(data));

            aulasDoDia.sort(Comparator.comparing(
                    aula -> aula.getHora() != null ? aula.getHora() : ""
            ));

            for (Aula aula : aulasDoDia) {
                listaItens.add(new ItemAgenda(aula));
            }
        }

        adapter.notifyDataSetChanged();
    }
}
