package logica.ordine;

public class Ordine {
    private int idOrdine;
    private StatoOrdine stato;
    private double costo;
    private String dataOraOrdine;
    private boolean pagato;
    private String indirizzo;
    
    public Ordine(int idOrdine, StatoOrdine stato, double costo, String dataOraOrdine, boolean pagato, String indirizzo) {
        this.idOrdine = idOrdine;
        this.stato = stato;
        this.costo = costo;
        this.dataOraOrdine = dataOraOrdine;
        this.pagato = pagato;
        this.indirizzo = indirizzo;
    }
    
    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public StatoOrdine getStato() {
        return stato;
    }

    public void setStato(StatoOrdine stato) {
        this.stato = stato;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getDataOraOrdine() {
        return dataOraOrdine;
    }

    public void setDataOraOrdine(String dataOraOrdine) {
        this.dataOraOrdine = dataOraOrdine;
    }

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(boolean pagato) {
        this.pagato = pagato;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void aggiornaStato() {}
}
