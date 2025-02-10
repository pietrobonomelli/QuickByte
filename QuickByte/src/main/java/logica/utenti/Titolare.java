package logica.utenti;

public class Titolare extends Utente {
	public Titolare(String email, String password, String nome, String telefono) {
        super(email, password, nome, telefono);
    }
	
    public void aggiungiRistorante() {}
    public void aggiungiMenu() {}
    public void aggiungiPiatto() {}
    public void rimuoviMenu() {}
    public void rimuoviPiatto() {}
    public void modificaPiatto() {}
    public void visualizzaOrdini() {}
    public void gestisciOrdine() {}
    public void produciScontrino() {}
}
