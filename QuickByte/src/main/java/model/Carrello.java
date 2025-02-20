package model;

public class Carrello {
    private int idCarrello;
    private int quantitaPiatti;
    private int idPiatto;
    private Integer ordine;  // Può essere null se non associato
    private String emailUtente;

    public Carrello(int idCarrello, int quantitaPiatti, int idPiatto, Integer ordine, String emailUtente) {
        this.idCarrello = idCarrello;
        this.quantitaPiatti = quantitaPiatti;
        this.idPiatto = idPiatto;
        this.ordine = ordine;
        this.emailUtente = emailUtente;
    }

    public int getIdCarrello() {
        return idCarrello;
    }

    public int getQuantitaPiatti() {
        return quantitaPiatti;
    }

    public int getIdPiatto() {
        return idPiatto;
    }

    public Integer getOrdine() {
        return ordine;
    }

    public String getEmailUtente() {
        return emailUtente;
    }
}
