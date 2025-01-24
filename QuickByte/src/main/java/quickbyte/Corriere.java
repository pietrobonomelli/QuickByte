package quickbyte;

public class Corriere extends Utente {
    private boolean staLavorando;
    
    public boolean isStaLavorando() {
        return staLavorando;
    }

    public void setStaLavorando(boolean staLavorando) {
        this.staLavorando = staLavorando;
    }

    public void iniziaLavoro() {}
    public void finisceLavoro() {}
    public void visualizzaOrdiniDisponibili() {}
    public void accettaConsegna() {}
}