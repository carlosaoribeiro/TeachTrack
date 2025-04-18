package com.carlosribeiro.teachtrack.model;

import java.util.Map;

public class Aula {

    private String aluno;
    private String email;
    private String tipo; // "Diário" ou "Mensal"

    // Para tipo Diário
    private String data;
    private String hora;

    // Para tipo Mensal
    private Map<String, String> horariosSemana; // ex: "segunda": "10:00"

    public Aula() {
        // Construtor vazio exigido pelo Firestore
    }

    // Getters e Setters

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

    public char[] getDiaSemana() {
        return new char[0];
    }
}
