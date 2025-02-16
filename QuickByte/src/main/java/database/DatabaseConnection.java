package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // Modifica il percorso con il corretto percorso relativo o assoluto del database
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database_embedded.db"; // Usa il percorso corretto

    // Metodo per ottenere la connessione
    public static Connection connect() throws SQLException {
        System.out.println("Tentando di connettersi al database...");

        // Tenta di stabilire una connessione al database
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        if (connection != null) {
            System.out.println("Connessione al database riuscita!");

            // Attiva il supporto per le foreign keys
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
                System.out.println("Le foreign keys sono ATTIVE");
            } catch (SQLException e) {
                System.err.println("Errore durante l'attivazione delle foreign keys: " + e.getMessage());
            }
        } else {
            System.out.println("Connessione al database fallita!");
        }

        return connection;
    }

    // Metodo per chiudere la connessione
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connessione chiusa con successo.");
            } catch (SQLException e) {
                System.out.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }
}
