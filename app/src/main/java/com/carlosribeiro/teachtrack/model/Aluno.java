package com.carlosribeiro.teachtrack.model;

import com.google.firebase.firestore.Exclude;

public class Aluno {

    @Exclude
    private String id;

    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private String dataNascimento;
    private String dataCadastro;
    private String tipoAluno;

    public Aluno() {
        // Construtor vazio obrigatório para o Firestore
    }

    // 🔑 Getter e Setter para ID (não salvo no Firestore)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters e Setters padrão
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getTipoAluno() {
        return tipoAluno;
    }

    public void setTipoAluno(String tipoAluno) {
        this.tipoAluno = tipoAluno;
    }
}
