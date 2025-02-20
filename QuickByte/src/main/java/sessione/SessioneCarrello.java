package sessione;

public class SessioneCarrello {
	private static int idRistorante;
	private static boolean pieno;

    public static int getIdRistorante() {
        return idRistorante;
    }

    public static void setIdRistorante(int idRistorante) {
        SessioneCarrello.idRistorante = idRistorante;
    }
    
    public static boolean getPieno() {
    	return pieno;
    }
    
    public static void setPieno(boolean pieno) {
    	SessioneCarrello.pieno = pieno;
    }
}