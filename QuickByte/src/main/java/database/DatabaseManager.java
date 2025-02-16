package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTables() {
        String createUtenteTable = "CREATE TABLE IF NOT EXISTS Utente (" +
                "email TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "nome TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "tipoUtente TEXT NOT NULL" +
                ");";

        String createOrdineTable = "CREATE TABLE IF NOT EXISTS Ordine (" +
                "idOrdine INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "stato TEXT NOT NULL, " +
                "costo REAL NOT NULL, " +
                "dataOraOrdine TEXT NOT NULL, " +
                "pagato INTEGER NOT NULL, " +
                "indirizzo TEXT NOT NULL, " +
                "emailCliente TEXT, " +
                "emailCorriere TEXT, " +
                "idRistorante INTEGER, " +
                "FOREIGN KEY(emailCliente) REFERENCES Utente(email), " +
                "FOREIGN KEY(emailCorriere) REFERENCES Utente(email), " +
                "FOREIGN KEY(idRistorante) REFERENCES Ristorante(idRistorante)" +
                ");";

        String createCarrelloTable = "CREATE TABLE IF NOT EXISTS Carrello (" +
                "idCarrello INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quantitaPiatti INTEGER NOT NULL, " +
                "idPiatto INTEGER NOT NULL, " +  // Modificato da 'piatto' a 'idPiatto' di tipo INTEGER
                "ordine INTEGER NOT NULL, " +
                "emailUtente TEXT, " +  // Nuovo campo per legare il carrello a un utente
                "FOREIGN KEY(idPiatto) REFERENCES Piatto(idPiatto), " + // Modifica della foreign key
                "FOREIGN KEY(ordine) REFERENCES Ordine(idOrdine), " +
                "FOREIGN KEY(emailUtente) REFERENCES Utente(email)" + // Associazione con l'utente
                ");";


        String createMetodoDiPagamentoTable = "CREATE TABLE IF NOT EXISTS MetodoDiPagamento (" +
                "nominativo TEXT NOT NULL, " +
                "numeroCarta TEXT PRIMARY KEY, " +
                "scadenza TEXT NOT NULL, " +
                "emailCliente TEXT, " +
                "FOREIGN KEY(emailCliente) REFERENCES Utente(email)" +
                ");";

        String createRistoranteTable = "CREATE TABLE IF NOT EXISTS Ristorante (" +
                "idRistorante INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "indirizzo TEXT NOT NULL, " +
                "emailTitolare TEXT, " +
                "FOREIGN KEY(emailTitolare) REFERENCES Utente(email) ON DELETE CASCADE" +  // ← Questa riga assicura che eliminando l'utente, si elimini il ristorante
                ");";

        String createMenuTable = "CREATE TABLE IF NOT EXISTS Menu (" +
                "nome TEXT NOT NULL, " +
                "idRistorante INTEGER NOT NULL, " +
                "PRIMARY KEY (nome, idRistorante), " +
                "FOREIGN KEY (idRistorante) REFERENCES Ristorante(idRistorante) ON DELETE CASCADE" +  // ← Questa riga fa eliminare i menu con il ristorante
                ");";


        String createPiattoTable = "CREATE TABLE IF NOT EXISTS Piatto (" +
        	    "idPiatto INTEGER PRIMARY KEY AUTOINCREMENT, " +
        	    "nome TEXT NOT NULL, " +
        	    "disponibile INTEGER NOT NULL, " +
        	    "prezzo TEXT NOT NULL, " +
        	    "allergeni TEXT, " +
        	    "foto TEXT, " +
        	    "nomeMenu TEXT NOT NULL, " +
        	    "idRistorante INTEGER NOT NULL, " +
        	    "FOREIGN KEY(nomeMenu, idRistorante) REFERENCES Menu(nome, idRistorante) ON DELETE CASCADE" +
        	");";





        try (Connection conn = connect();
        	    Statement stmt = conn.createStatement()) {
        	    // Abilita le foreign keys PRIMA di creare le tabelle
        	    stmt.execute("PRAGMA foreign_keys = ON;");
        	    System.out.println("PRAGMA foreign_keys = ON");

        	    // Controlla se le foreign keys sono attive
        	    ResultSet rs = stmt.executeQuery("PRAGMA foreign_keys;");
        	    if (rs.next() && rs.getInt(1) == 1) {
        	        System.out.println("Le foreign keys sono ATTIVE");
        	    } else {
        	        System.out.println("Le foreign keys NON sono attive!");
        	    }
        	    rs.close();

        	    // Creazione delle tabelle
        	    stmt.execute(createUtenteTable);
        	    stmt.execute(createOrdineTable);
        	    stmt.execute(createCarrelloTable);
        	    stmt.execute(createMetodoDiPagamentoTable);
        	    stmt.execute(createRistoranteTable);
        	    stmt.execute(createMenuTable);
        	    stmt.execute(createPiattoTable);

        	    System.out.println("Tabelle create con successo!");
        	} catch (SQLException e) {
        	    System.err.println("Errore durante la creazione delle tabelle: " + e.getMessage());
        	}
        
        
        try (Connection conn = connect();
        	    Statement stmt = conn.createStatement()) {

        	    // Controlla le foreign keys per la tabella 'Utente'
        	    try (ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(Utente);")) {
        	        System.out.println("Foreign keys per la tabella Utente:");
        	        while (rs.next()) {
        	            System.out.println(
        	                "id: " + rs.getInt("id") +
        	                ", tabella riferimento: " + rs.getString("table") +
        	                ", colonna riferimento: " + rs.getString("to")
        	            );
        	        }
        	    }

        	    // Controlla le foreign keys per la tabella 'Ristorante'
        	    try (ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(Ristorante);")) {
        	        System.out.println("Foreign keys per la tabella Ristorante:");
        	        while (rs.next()) {
        	            System.out.println(
        	                "id: " + rs.getInt("id") +
        	                ", tabella riferimento: " + rs.getString("table") +
        	                ", colonna riferimento: " + rs.getString("to")
        	            );
        	        }
        	    }

        	    // Controlla le foreign keys per la tabella 'Menu'
        	    try (ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(Menu);")) {
        	        System.out.println("Foreign keys per la tabella Menu:");
        	        while (rs.next()) {
        	            System.out.println(
        	                "id: " + rs.getInt("id") +
        	                ", tabella riferimento: " + rs.getString("table") +
        	                ", colonna riferimento: " + rs.getString("to")
        	            );
        	        }
        	    }

        	    // Controlla le foreign keys per la tabella 'Piatto'
        	    try (ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(Piatto);")) {
        	        System.out.println("Foreign keys per la tabella Piatto:");
        	        while (rs.next()) {
        	            System.out.println(
        	                "id: " + rs.getInt("id") +
        	                ", tabella riferimento: " + rs.getString("table") +
        	                ", colonna riferimento: " + rs.getString("to")
        	            );
        	        }
        	    }

        	} catch (SQLException e) {
        	    System.err.println("Errore durante la verifica delle foreign keys: " + e.getMessage());
        	}




    }
}
