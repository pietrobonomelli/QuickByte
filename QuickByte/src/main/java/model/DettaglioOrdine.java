package model;

public class DettaglioOrdine {
    private int idOrdine;
    private int idPiatto;
    private int quantita;

    // Costruttore
    public DettaglioOrdine(int idOrdine, int idPiatto, int quantita) {
        this.idOrdine = idOrdine;
        this.idPiatto = idPiatto;
        this.quantita = quantita;
    }

    // Getter e Setter
    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public int getIdPiatto() {
        return idPiatto;
    }

    public void setIdPiatto(int idPiatto) {
        this.idPiatto = idPiatto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
}
