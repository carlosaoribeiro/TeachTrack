package com.carlosribeiro.teachtrack.model;

public class ItemAgenda {
    public static final int TIPO_CABECALHO = 0;
    public static final int TIPO_AULA = 1;

    private int tipo;
    private String data; // usado quando for cabeçalho
    private Aula aula;   // usado quando for uma aula

    // Cabeçalho
    public ItemAgenda(String data) {
        this.tipo = TIPO_CABECALHO;
        this.data = data;
    }

    // Aula
    public ItemAgenda(Aula aula) {
        this.tipo = TIPO_AULA;
        this.aula = aula;
    }

    public int getTipo() {
        return tipo;
    }

    public String getData() {
        return data;
    }

    public Aula getAula() {
        return aula;
    }
}
