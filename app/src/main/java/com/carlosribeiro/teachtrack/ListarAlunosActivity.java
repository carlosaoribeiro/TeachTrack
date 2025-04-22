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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListarAlunosActivity extends AppCompatActivity {

    private static final int REQUEST_CADASTRO = 1;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView recyclerView;
    private EditText editBuscar;
    private AlunoAdapter adapter;
    private List<Aluno> listaAlunos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alunos);

        inicializarComponentes();
        configurarListeners();
        escutarAlteracoesAlunos(); // 🔁 Escuta alterações em tempo real
    }

    private void inicializarComponentes() {
        recyclerView = findViewById(R.id.recyclerAlunos);
        editBuscar = findViewById(R.id.editBuscar);

        adapter = new AlunoAdapter(listaAlunos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void configurarListeners() {
        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabCadastrar);
        fab.setOnClickListener(view -> abrirCadastro(null));

        adapter.setOnAlunoLongClickListener(this::abrirCadastro);
    }

    // 🔁 Firestore Listener em tempo real
    private void escutarAlteracoesAlunos() {
        db.collection("alunos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null || snapshots == null) return;

                        listaAlunos.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Aluno aluno = doc.toObject(Aluno.class);
                            if (aluno != null) {
                                aluno.setId(doc.getId());
                                listaAlunos.add(aluno);
                            }
                        }

                        adapter = new AlunoAdapter(listaAlunos);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnAlunoLongClickListener(ListarAlunosActivity.this::abrirCadastro);
                    }
                });
    }

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

    // 🔄 Pode remover se quiser, já não é necessário com listener
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CADASTRO && resultCode == RESULT_OK) {
            // carregarAlunos(); ⛔️ não precisa mais com snapshotListener
        }
    }
}
