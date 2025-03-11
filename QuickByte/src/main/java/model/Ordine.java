package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Ordine {
    private int idOrdine;
    private StatoOrdine stato;
    private double costo;
    private Timestamp dataOraOrdine; // Usa Timestamp per gestire data e ora
    private String indirizzo;
    private String emailCliente;
    private String emailCorriere;
    private int idRistorante;

    public Ordine(int idOrdine, StatoOrdine stato, double costo, Timestamp dataOraOrdine, String indirizzo,
                  String emailCliente, String emailCorriere, int idRistorante) {
        this.idOrdine = idOrdine;
        this.stato = stato;
        this.costo = costo;
        this.dataOraOrdine = dataOraOrdine;
        this.indirizzo = indirizzo;
        this.emailCliente = emailCliente;
        this.emailCorriere = emailCorriere;
        this.idRistorante = idRistorante;
    }

    public int getIdOrdine() { return idOrdine; }
    public StatoOrdine getStato() { return stato; }
    public double getCosto() { return costo; }

    public Timestamp getDataOraOrdine() { return dataOraOrdine; }

    // Metodo per ottenere la data e l'ora formattata
    public String getFormattedDataOraOrdine() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dataOraOrdine);
    }

    public String getIndirizzo() { return indirizzo; }
    public String getEmailCliente() { return emailCliente; }
    public String getEmailCorriere() { return emailCorriere; }
    public void setEmailCorriere(String emailCorriere) { this.emailCorriere = emailCorriere; }
    public int getIdRistorante() { return idRistorante; }

    @Override
    public String toString() {
        return "Ordine ID: " + idOrdine + ", Stato: " + stato + ", Costo: " + costo +
               ", Data e Ora: " + getFormattedDataOraOrdine() + ", Indirizzo: " + indirizzo;
    }
}
