package model;

public class Ordine {
    private int idOrdine;
    private String stato;
    private double costo;
    private String dataOraOrdine;
    private int pagato;
    private String indirizzo;
    private String emailCliente;
    private String emailCorriere;
    private int idRistorante;

    public Ordine(int idOrdine, String stato, double costo, String dataOraOrdine, int pagato, String indirizzo, 
                  String emailCliente, String emailCorriere, int idRistorante) {
        this.idOrdine = idOrdine;
        this.stato = stato;
        this.costo = costo;
        this.dataOraOrdine = dataOraOrdine;
        this.pagato = pagato;
        this.indirizzo = indirizzo;
        this.emailCliente = emailCliente;
        this.emailCorriere = emailCorriere;
        this.idRistorante = idRistorante;
    }

    public int getIdOrdine() { return idOrdine; }
    public String getStato() { return stato; }
    public double getCosto() { return costo; }
    public String getDataOraOrdine() { return dataOraOrdine; }
    public int getPagato() { return pagato; }
    public String getIndirizzo() { return indirizzo; }
    public String getEmailCliente() { return emailCliente; }
    public String getEmailCorriere() { return emailCorriere; }
    public int getIdRistorante() { return idRistorante; }

    @Override
    public String toString() {
        return "Ordine ID: " + idOrdine + ", Stato: " + stato + ", Costo: " + costo + ", Indirizzo: " + indirizzo;
    }
}
