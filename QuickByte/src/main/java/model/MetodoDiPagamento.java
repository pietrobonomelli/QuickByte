package model;

public class MetodoDiPagamento {
    private String nominativo;
    private String numeroCarta;
    private String scadenza;
    private String emailCliente;

    public MetodoDiPagamento(String nominativo, String numeroCarta, String scadenza, String emailCliente) {
        this.nominativo = nominativo;
        this.numeroCarta = numeroCarta;
        this.scadenza = scadenza;
        this.emailCliente = emailCliente;
    }

    public String getNominativo() {
        return nominativo;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public String getScadenza() {
        return scadenza;
    }

    public String getEmailCliente() {
        return emailCliente;
    }
}
