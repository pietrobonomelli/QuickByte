package model;

public class Menu {
    private String nome;
    private int idRistorante;

    // Costruttore
    public Menu(String nome, int idRistorante) {
        this.nome = nome;
        this.idRistorante = idRistorante;
    }

    // Getter e Setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdRistorante() {
        return idRistorante;
    }

    public void setIdRistorante(int idRistorante) {
        this.idRistorante = idRistorante;
    }
}
