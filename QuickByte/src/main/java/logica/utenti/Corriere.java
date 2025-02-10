package logica.utenti;

public class Corriere extends Utente {
	public Corriere(String email, String password, String nome, String telefono) {
        super(email, password, nome, telefono);
    }
	
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