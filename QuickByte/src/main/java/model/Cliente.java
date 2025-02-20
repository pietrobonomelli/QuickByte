package model;

public class Cliente extends Utente {
	 public Cliente(String email, String password, String nome, String telefono) {
	        super(email, password, nome, telefono);
	    }
	 
    public void visualizzaRistoranti() {}
    public void visualizzaMenu() {}
    public void effettuaOrdine() {}
    public void annullaOrdine() {}
    public void modificaOrdine() {}
    public void controllaStatoOrdine() {}
}
