package logica.prodotti;

public class Piatto {
    private String nome;
    private boolean disponibile;
    private String prezzo;
    private String allergeni;
    private String foto;
    
    public Piatto(String nome, boolean disponibile, String prezzo, String allergeni, String foto) {
        this.nome = nome;
        this.disponibile = disponibile;
        this.prezzo = prezzo;
        this.allergeni = allergeni;
        this.foto = foto;
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
}