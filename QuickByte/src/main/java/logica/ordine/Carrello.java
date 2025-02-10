package logica.ordine;

public class Carrello {
    private int idCarrello;
    private int quantitaPiatti;
    
    public Carrello(int idCarrello, int quantitaPiatti) {
        this.idCarrello = idCarrello;
        this.quantitaPiatti = quantitaPiatti;
    }

    public int getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(int idCarrello) {
        this.idCarrello = idCarrello;
    }

    public int getQuantitaPiatti() {
        return quantitaPiatti;
    }

    public void setQuantitaPiatti(int quantitaPiatti) {
        this.quantitaPiatti = quantitaPiatti;
    }
    
    public void aggiungiPiatto() {}
    public void rimuoviPiatto() {}
    public void checkout() {}
    
    
}
