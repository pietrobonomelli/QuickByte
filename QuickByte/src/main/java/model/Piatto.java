package model;

public class Piatto {
    private int idPiatto;
    private String nome;
    private boolean disponibile;
    private String prezzo;
    private String allergeni;
    private String foto;
    private String nomeMenu;
    private int idRistorante;

    // Costruttore
    public Piatto(int idPiatto, String nome, boolean disponibile, String prezzo, String allergeni, String foto, String nomeMenu, int idRistorante) {
        this.idPiatto = idPiatto;
        this.nome = nome;
        this.disponibile = disponibile;
        this.prezzo = prezzo;
        this.allergeni = allergeni;
        this.foto = foto;
        this.nomeMenu = nomeMenu;
        this.idRistorante = idRistorante;
    }

    // Getter e Setter
    public int getIdPiatto() {
        return idPiatto;
    }

    public void setIdPiatto(int idPiatto) {
        this.idPiatto = idPiatto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public String getAllergeni() {
        return allergeni;
    }

    public void setAllergeni(String allergeni) {
        this.allergeni = allergeni;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNomeMenu() {
        return nomeMenu;
    }

    public void setNomeMenu(String nomeMenu) {
        this.nomeMenu = nomeMenu;
    }

    public int getIdRistorante() {
        return idRistorante;
    }

    public void setIdRistorante(int idRistorante) {
        this.idRistorante = idRistorante;
    }
}
