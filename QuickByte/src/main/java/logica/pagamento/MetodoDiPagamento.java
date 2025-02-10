package logica.pagamento;

public class MetodoDiPagamento {
    private String nominativo;
    private String numeroCarta;
    private String scadenza;
    
    public MetodoDiPagamento(String nominativo, String numeroCarta, String scadenza) {
        this.nominativo = nominativo;
        this.numeroCarta = numeroCarta;
        this.scadenza = scadenza;
    }
    
    public String getNominativo() {
        return nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }
}
