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

import com.carlosribeiro.teachtrack.adapter.AlunoAdapter;
import com.carlosribeiro.teachtrack.model.Aluno;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListarAlunosActivity extends AppCompatActivity {

    // 🚩 Constantes
    private static final int REQUEST_CADASTRO = 1;

    // 🔗 Firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 📦 Componentes e dados
    private RecyclerView recyclerView;
    private EditText editBuscar;
    private AlunoAdapter adapter;
    private List<Aluno> listaAlunos = new ArrayList<>();

    // ▶️ Ciclo de vida
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alunos);

        inicializarComponentes();
        configurarListeners();
        carregarAlunos();
    }

    // 🧱 Inicialização da UI
    private void inicializarComponentes() {
        recyclerView = findViewById(R.id.recyclerAlunos);
        editBuscar = findViewById(R.id.editBuscar);

        adapter = new AlunoAdapter(listaAlunos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // 🎯 Listeners (busca, FAB, clique longo)
    private void configurarListeners() {
        // Busca
        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }
        });

        // FAB
        FloatingActionButton fab = findViewById(R.id.fabCadastrar);
        fab.setOnClickListener(view -> abrirCadastro(null));

        // Clique longo
        adapter.setOnAlunoLongClickListener(this::abrirCadastro);
    }

    // 🔁 Carrega dados do Firestore
    private void carregarAlunos() {
        db.collection("alunos")
                .get()
                .addOnSuccessListener(result -> {
                    listaAlunos.clear();
                    for (QueryDocumentSnapshot doc : result) {
                        Aluno aluno = doc.toObject(Aluno.class);
                        aluno.setId(doc.getId()); // 🔑 Necessário para edição
                        listaAlunos.add(aluno);
                    }
                    adapter = new AlunoAdapter(listaAlunos);
                    recyclerView.setAdapter(adapter);

                    // Reanexa listener de clique longo após recriar o adapter
                    adapter.setOnAlunoLongClickListener(this::abrirCadastro);
                });
    }

    // ➕ Abre tela de cadastro/edição
    private void abrirCadastro(@Nullable Aluno aluno) {
        Intent intent = new Intent(this, CadastroAlunoActivity.class);

        if (aluno != null) {
            intent.putExtra("alunoId", aluno.getId());
            intent.putExtra("nome", aluno.getNome());
            intent.putExtra("sobrenome", aluno.getSobrenome());
            intent.putExtra("email", aluno.getEmail());
            intent.putExtra("telefone", aluno.getTelefone());
            intent.putExtra("dataNascimento", aluno.getDataNascimento());
            intent.putExtra("dataCadastro", aluno.getDataCadastro());
            intent.putExtra("tipoAluno", aluno.getTipoAluno());
        }

        startActivityForResult(intent, REQUEST_CADASTRO);
    }

    // 🔄 Atualiza lista ao voltar do cadastro
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CADASTRO && resultCode == RESULT_OK) {
            carregarAlunos();
        }
    }
}
