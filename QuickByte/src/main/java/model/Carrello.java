package model;

public class Carrello {
    private int idCarrello;
    private int quantitaPiatti;
    private int idPiatto;
    private String emailUtente;

    public Carrello(int idCarrello, int quantitaPiatti, int idPiatto, String emailUtente) {
        this.idCarrello = idCarrello;
        this.quantitaPiatti = quantitaPiatti;
        this.idPiatto = idPiatto;
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


    public String getEmailUtente() {
        return emailUtente;
    }
}
