package com.carlosribeiro.teachtrack;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.carlosribeiro.teachtrack.adapter.AulaAdapter;
import com.carlosribeiro.teachtrack.model.Aula;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ListarAulasActivity extends AppCompatActivity {

    private RecyclerView recyclerAulas;
    private EditText editBuscarAula;
    private AulaAdapter adapter;
    private List<Aula> listaAulas = new ArrayList<>();
    private List<Aula> listaFiltrada = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_aulas);

        recyclerAulas = findViewById(R.id.recyclerAulas);
        editBuscarAula = findViewById(R.id.editBuscarAula);

        adapter = new AulaAdapter(listaFiltrada);
        recyclerAulas.setLayoutManager(new LinearLayoutManager(this));
        recyclerAulas.setAdapter(adapter);

        carregarAulas();

        editBuscarAula.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarAulas(s.toString());
            }
        });
    }

    private void carregarAulas() {
        db.collection("aulas")
                .get()
                .addOnSuccessListener(result -> {
                    listaAulas.clear();
                    for (QueryDocumentSnapshot doc : result) {
                        Aula aula = doc.toObject(Aula.class);
                        listaAulas.add(aula);
                    }
                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaAulas);
                    adapter.notifyDataSetChanged();
                });
    }

    private void filtrarAulas(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaAulas);
        } else {
            for (Aula aula : listaAulas) {
                if (aula.getAluno().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(aula);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
