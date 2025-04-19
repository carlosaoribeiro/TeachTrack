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

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button btnEntrar;
    private TextView txtEsqueciSenha, txtCadastrar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referência aos componentes
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);
        txtCadastrar = findViewById(R.id.txtCadastrar);

        auth = FirebaseAuth.getInstance();

        // Ações dos botões
        btnEntrar.setOnClickListener(v -> loginUsuario());
        txtCadastrar.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroProfessorActivity.class)));
        txtEsqueciSenha.setOnClickListener(v -> recuperarSenha());
    }

    private void loginUsuario() {
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            mostrarDialog("Preencha todos os campos.");
            return;
        }

        auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        mostrarDialog("Você precisa verificar seu e-mail para continuar.");
                        auth.signOut();
                    }
                })
                .addOnFailureListener(e ->
                        mostrarDialog("Erro ao entrar: " + e.getMessage()));
    }

    private void recuperarSenha() {
        String email = editEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            mostrarDialog("Informe o e-mail para recuperar a senha.");
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        mostrarDialog("Link de redefinição enviado para o e-mail."))
                .addOnFailureListener(e ->
                        mostrarDialog("Erro: " + e.getMessage()));
    }

    private void mostrarDialog(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage(mensagem)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
