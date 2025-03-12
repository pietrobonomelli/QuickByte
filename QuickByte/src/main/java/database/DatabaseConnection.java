package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static String URL_DATABASE = "jdbc:sqlite:src/main/resources/database_embedded.db"; // Percorso predefinito del database

    /**
     * Imposta un nuovo URL per il database, utile per i test.
     *
     * @param url Il nuovo URL del database.
     */
    public static void setDatabaseUrl(String url) {
        URL_DATABASE = url;
    }

    /**
     * Stabilisce una connessione al database.
     *
     * @return La connessione al database.
     * @throws SQLException Se si verifica un errore durante la connessione.
     */
    public static Connection connect() throws SQLException {
        System.out.println("Tentativo di connessione al database...");

        // Tenta di stabilire una connessione al database
        Connection connessione = DriverManager.getConnection(URL_DATABASE);

        if (connessione != null) {
            System.out.println("Connessione al database riuscita!");
            attivaForeignKeys(connessione);
        } else {
            System.out.println("Connessione al database fallita!");
        }

        return connessione;
    }

    /**
     * Attiva il supporto per le foreign keys nel database.
     *
     * @param connessione La connessione al database.
     */
    private static void attivaForeignKeys(Connection connessione) {
        try (Statement stmt = connessione.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Le foreign keys sono ATTIVE");
        } catch (SQLException e) {
            System.err.println("Errore durante l'attivazione delle foreign keys: " + e.getMessage());
        }
    }

    /**
     * Chiude la connessione al database.
     *
     * @param connessione La connessione da chiudere.
     */
    public static void chiudiConnessione(Connection connessione) {
        if (connessione != null) {
            try {
                connessione.close();
                System.out.println("Connessione chiusa con successo.");
            } catch (SQLException e) {
                System.out.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }
}
