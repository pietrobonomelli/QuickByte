package model;

public class Ristorante {
    private int idRistorante;
    private String nome;
    private String telefono;
    private String indirizzo;
    private String emailTitolare;

    // Costruttore
    public Ristorante(int idRistorante, String nome, String telefono, String indirizzo, String emailTitolare) {
        this.idRistorante = idRistorante;
        this.nome = nome;
        this.telefono = telefono;
        this.indirizzo = indirizzo;
        this.emailTitolare = emailTitolare;
    }
    
    public Ristorante() {}

    // Costruttore senza id (per quando si crea un nuovo ristorante)
    public Ristorante(String nome, String telefono, String indirizzo, String emailTitolare) {
        this(0, nome, telefono, indirizzo, emailTitolare);  // id sarà gestito dal database
    }

    // Getters e Setters
    public int getIdRistorante() {
        return idRistorante;
    }

    public void setIdRistorante(int idRistorante) {
        this.idRistorante = idRistorante;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getEmailTitolare() {
        return emailTitolare;
    }

    public void setEmailTitolare(String emailTitolare) {
        this.emailTitolare = emailTitolare;
    }

    // Metodo toString() per debug
    @Override
    public String toString() {
        return "Ristorante{" +
                "idRistorante=" + idRistorante +
                ", nome='" + nome + '\'' +
                ", telefono='" + telefono + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", emailTitolare='" + emailTitolare + '\'' +
                '}';
    }
}
