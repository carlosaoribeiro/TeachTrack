package com.carlosribeiro.teachtrack;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.app.AlertDialog;

import com.carlosribeiro.teachtrack.util.MaskUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroAlunoActivity extends Activity {

    private EditText editNome, editSobrenome, editTelefone, editEmail, editDataNascimento, editDataCadastro;
    private CheckBox checkMensal, checkDiaria;
    private Button btnSalvar;

    private FirebaseFirestore db;
    private String alunoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_aluno);

        editNome = findViewById(R.id.editNome);
        editSobrenome = findViewById(R.id.editSobrenome);
        editTelefone = findViewById(R.id.editTelefone);
        editEmail = findViewById(R.id.editEmail);
        editDataNascimento = findViewById(R.id.editDataNascimento);
        editDataCadastro = findViewById(R.id.editDataCadastro);
        checkMensal = findViewById(R.id.checkMensal);
        checkDiaria = findViewById(R.id.checkDiaria);
        btnSalvar = findViewById(R.id.btnSalvar);

        db = FirebaseFirestore.getInstance();

        // Máscara para datas
        MaskUtils.applyDateMask(editDataNascimento);
        MaskUtils.applyDateMask(editDataCadastro);

        // Checkbox: apenas uma opção pode ser marcada
        checkMensal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkDiaria.setChecked(false);
        });

        checkDiaria.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkMensal.setChecked(false);
        });

        btnSalvar.setOnClickListener(v -> salvarAluno());

        // Preenchimento automático se for edição
        alunoId = getIntent().getStringExtra("alunoId");
        if (alunoId != null) {
            editNome.setText(getIntent().getStringExtra("nome"));
            editSobrenome.setText(getIntent().getStringExtra("sobrenome"));
            editTelefone.setText(getIntent().getStringExtra("telefone"));
            editEmail.setText(getIntent().getStringExtra("email"));
            editDataNascimento.setText(getIntent().getStringExtra("dataNascimento"));
            editDataCadastro.setText(getIntent().getStringExtra("dataCadastro"));

            String tipo = getIntent().getStringExtra("tipoAluno");
            if ("Mensal".equals(tipo)) {
                checkMensal.setChecked(true);
            } else if ("Diária".equals(tipo)) {
                checkDiaria.setChecked(true);
            }
        }
    }

    private void salvarAluno() {
        String nome = editNome.getText().toString().trim();
        String sobrenome = editSobrenome.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String dataNascimento = editDataNascimento.getText().toString().trim();
        String dataCadastro = editDataCadastro.getText().toString().trim();
        String tipoAluno = "";

        // Validações
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(sobrenome) || TextUtils.isEmpty(email)) {
            mostrarDialogo("Erro", "Nome, sobrenome e email são obrigatórios.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarDialogo("Erro", "Digite um e-mail válido.");
            return;
        }

        if (checkMensal.isChecked() && checkDiaria.isChecked()) {
            mostrarDialogo("Erro", "Escolha apenas um tipo de aluno.");
            return;
        }

        if (!checkMensal.isChecked() && !checkDiaria.isChecked()) {
            mostrarDialogo("Erro", "Escolha o tipo de aluno.");
            return;
        }

        // Tipo selecionado
        if (checkMensal.isChecked()) {
            tipoAluno = "Mensal";
        } else if (checkDiaria.isChecked()) {
            tipoAluno = "Diária";
        }

        // Dados do aluno
        Map<String, Object> aluno = new HashMap<>();
        aluno.put("nome", nome);
        aluno.put("sobrenome", sobrenome);
        aluno.put("telefone", telefone);
        aluno.put("email", email);
        aluno.put("dataNascimento", dataNascimento);
        aluno.put("dataCadastro", dataCadastro);
        aluno.put("tipoAluno", tipoAluno);

        if (alunoId != null) {
            // Atualiza existente
            db.collection("alunos").document(alunoId).set(aluno)
                    .addOnSuccessListener(unused -> {
                        mostrarDialogoSucesso("Aluno atualizado com sucesso.");
                    })
                    .addOnFailureListener(e ->
                            mostrarDialogo("Erro", "Falha ao atualizar aluno: " + e.getMessage()));
        } else {
            // Novo aluno
            db.collection("alunos").add(aluno)
                    .addOnSuccessListener(documentReference -> {
                        mostrarDialogoSucesso("Aluno cadastrado com sucesso.");
                    })
                    .addOnFailureListener(e ->
                            mostrarDialogo("Erro", "Falha ao salvar aluno: " + e.getMessage()));
        }
    }

    private void mostrarDialogoSucesso(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Sucesso")
                .setMessage(mensagem)
                .setPositiveButton("OK", (dialog, which) -> {
                    setResult(RESULT_OK);
                    finish();
                })
                .show();
    }

    private void mostrarDialogo(String titulo, String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }
}
