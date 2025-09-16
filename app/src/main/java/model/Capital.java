package model;

public class Capital {
    private String nome;
    private String estado;

    public Capital(String nome, String estado) {
        this.nome = nome;
        this.estado = estado;
    }

    public String getNome() {
        return nome;
    }

    public String getEstado() {
        return estado;
    }
}
