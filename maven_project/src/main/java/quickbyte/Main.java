package quickbyte;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Avvio applicazione...");

        // Inizializzazione del database
        try {
            System.out.println("Connessione al database...");
            Connection conn = DatabaseManager.connect();
            if (conn != null) {
                System.out.println("Connessione stabilita con successo.");
            }

            // Creazione delle tabelle
            System.out.println("Creazione delle tabelle...");
            DatabaseManager.createTables();

            // Chiusura connessione
            conn.close();
            System.out.println("Database inizializzato correttamente.");
        } catch (SQLException e) {
            System.err.println("Errore durante l'inizializzazione del database: " + e.getMessage());
        }

        // Qui puoi aggiungere altre logiche di avvio dell'applicazione
        System.out.println("Applicazione avviata correttamente!");
    }
}
