package com.carlosribeiro.teachtrack.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Map;

public class Aula implements Serializable {

    @Exclude
    private String id;

    private String aluno;
    private String email;
    private String tipo;
    private String data;
    private String hora;

    private String tema;

    private Map<String, String> horariosSemana;

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public Aula() {
        // Firestore requires empty constructor
    }

    // 🔑 Getters e Setters
    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Map<String, String> getHorariosSemana() {
        return horariosSemana;
    }

    public void setHorariosSemana(Map<String, String> horariosSemana) {
        this.horariosSemana = horariosSemana;
    }
}
