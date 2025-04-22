package com.carlosribeiro.teachtrack;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private TextView textVoltarLogin;

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
        textVoltarLogin = findViewById(R.id.txtVoltarLogin);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());

        textVoltarLogin.setOnClickListener(v -> {
            startActivity(new Intent(CadastroProfessorActivity.this, LoginActivity.class));
            finish(); // Fecha a tela atual
        });
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
                                    Map<String, Object> dadosUsuario = new HashMap<>();
                                    dadosUsuario.put("nome", nome);
                                    dadosUsuario.put("email", email);

                                    firestore.collection("usuarios")
                                            .document(usuario.getUid())
                                            .set(dadosUsuario);

                                    mostrarDialog("Cadastro realizado com sucesso!\n\nVerifique seu e-mail para ativar sua conta.");

                                    editNome.setText("");
                                    editEmail.setText("");
                                    editSenha.setText("");

                                    auth.signOut();

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
