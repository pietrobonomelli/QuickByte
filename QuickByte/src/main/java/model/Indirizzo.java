package model;

public class Indirizzo {
    private int idIndirizzo;
    private String indirizzo;
    private String citta;
    private String cap;
    private String provincia;
    private String emailUtente;

    // Costruttore
    public Indirizzo(int idIndirizzo, String indirizzo, String citta, String cap, String provincia, String emailUtente) {
        this.idIndirizzo = idIndirizzo;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.cap = cap;
        this.provincia = provincia;
        this.emailUtente = emailUtente;
    }

    // Costruttore senza id (per inserimenti)
    public Indirizzo(String indirizzo, String citta, String cap, String provincia, String emailUtente) {
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.cap = cap;
        this.provincia = provincia;
        this.emailUtente = emailUtente;
    }

    // Getter e Setter
    public int getIdIndirizzo() {
        return idIndirizzo;
    }

    public void setIdIndirizzo(int idIndirizzo) {
        this.idIndirizzo = idIndirizzo;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getEmailUtente() {
        return emailUtente;
    }

    public void setEmailUtente(String emailUtente) {
        this.emailUtente = emailUtente;
    }
}
