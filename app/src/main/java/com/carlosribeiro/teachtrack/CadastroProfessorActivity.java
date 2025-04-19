package com.carlosribeiro.teachtrack;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroProfessorActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha;
    private Button btnCadastrar;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_professor);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());
    }

    private void cadastrarUsuario() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            mostrarDialog("Preencha todos os campos!");
            return;
        }

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser usuario = auth.getCurrentUser();
                    if (usuario != null) {
                        usuario.sendEmailVerification()
                                .addOnSuccessListener(unused -> {
                                    // Salvar nome + email no Firestore
                                    Map<String, Object> dadosUsuario = new HashMap<>();
                                    dadosUsuario.put("nome", nome);
                                    dadosUsuario.put("email", email);

                                    firestore.collection("usuarios")
                                            .document(usuario.getUid())
                                            .set(dadosUsuario);

                                    mostrarDialog("Cadastro realizado com sucesso!\n\nVerifique seu e-mail para ativar sua conta.");

                                    // ✅ Limpa os campos
                                    editNome.setText("");
                                    editEmail.setText("");
                                    editSenha.setText("");

                                    auth.signOut();

                                    // ✅ Redireciona para login e finaliza tela atual
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> mostrarDialog("Erro ao enviar e-mail: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> mostrarDialog("Erro ao cadastrar: " + e.getMessage()));
    }

    private void mostrarDialog(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage(mensagem)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
