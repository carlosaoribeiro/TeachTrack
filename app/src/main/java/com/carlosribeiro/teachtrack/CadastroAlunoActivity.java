package com.carlosribeiro.teachtrack;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.Spinner;

import com.carlosribeiro.teachtrack.util.MaskUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroAlunoActivity extends Activity {

    private EditText editNome, editSobrenome, editTelefone, editEmail, editDataNascimento, editDataCadastro;
    private Spinner spinnerTipoAluno;
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
        spinnerTipoAluno = findViewById(R.id.spinnerTipoAluno);
        btnSalvar = findViewById(R.id.btnSalvar);

        db = FirebaseFirestore.getInstance();

        // ✅ Máscaras
        MaskUtils.applyPhoneMask(editTelefone);
        MaskUtils.applyDateMask(editDataNascimento);
        MaskUtils.applyDateMask(editDataCadastro);

        // ✅ Spinner de tipo
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Mensal", "Diário"}
        );
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAluno.setAdapter(adapterTipo);

        btnSalvar.setOnClickListener(v -> salvarAluno());

        alunoId = getIntent().getStringExtra("alunoId");
        if (alunoId != null) preencherCamposEdicao();
    }

    private void preencherCamposEdicao() {
        editNome.setText(getIntent().getStringExtra("nome"));
        editSobrenome.setText(getIntent().getStringExtra("sobrenome"));
        editTelefone.setText(getIntent().getStringExtra("telefone"));
        editEmail.setText(getIntent().getStringExtra("email"));
        editDataNascimento.setText(getIntent().getStringExtra("dataNascimento"));
        editDataCadastro.setText(getIntent().getStringExtra("dataCadastro"));

        String tipo = getIntent().getStringExtra("tipoAluno");
        if (tipo != null) {
            int pos = tipo.equals("Mensal") ? 0 : 1;
            spinnerTipoAluno.setSelection(pos);
        }
    }

    private void salvarAluno() {
        String nome = editNome.getText().toString().trim();
        String sobrenome = editSobrenome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();
        String nascimento = editDataNascimento.getText().toString().trim();
        String cadastro = editDataCadastro.getText().toString().trim();
        String tipoAluno = spinnerTipoAluno.getSelectedItem().toString();

        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(sobrenome)) {
            mostrarDialogo("Erro", "Nome e sobrenome são obrigatórios.");
            return;
        }

        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarDialogo("Erro", "Informe um email válido.");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> aluno = new HashMap<>();
        aluno.put("nome", nome);
        aluno.put("sobrenome", sobrenome);
        aluno.put("email", email);
        aluno.put("telefone", telefone);
        aluno.put("dataNascimento", nascimento);
        aluno.put("dataCadastro", cadastro);
        aluno.put("tipoAluno", tipoAluno);
        aluno.put("userId", userId); // 🔥 dono do dado

        if (alunoId != null) {
            db.collection("alunos").document(alunoId)
                    .set(aluno)
                    .addOnSuccessListener(aVoid -> mostrarDialogoSucesso("Aluno atualizado com sucesso!"))
                    .addOnFailureListener(e -> mostrarDialogo("Erro", "Falha ao atualizar aluno."));
        } else {
            db.collection("alunos")
                    .add(aluno)
                    .addOnSuccessListener(docRef -> mostrarDialogoSucesso("Aluno cadastrado com sucesso!"))
                    .addOnFailureListener(e -> mostrarDialogo("Erro", "Erro ao salvar aluno."));
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
