package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";	//percorso del db

    // Metodo per connettersi al database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Metodo per creare le tabelle
    public static void createTables() {
        String createUtenteTable = "CREATE TABLE IF NOT EXISTS Utente (" +
                "email TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "nome TEXT NOT NULL, " +
                "telefono TEXT NOT NULL" +
                ");";

        String createClienteTable = "CREATE TABLE IF NOT EXISTS Cliente (" +
                "email TEXT PRIMARY KEY, " +
                "FOREIGN KEY (email) REFERENCES Utente(email)" +
                ");";

        String createTitolareTable = "CREATE TABLE IF NOT EXISTS Titolare (" +
                "email TEXT PRIMARY KEY, " +
                "FOREIGN KEY (email) REFERENCES Utente(email)" +
                ");";

        String createCorriereTable = "CREATE TABLE IF NOT EXISTS Corriere (" +
                "email TEXT PRIMARY KEY, " +
                "staLavorando INTEGER NOT NULL, " +
                "FOREIGN KEY (email) REFERENCES Utente(email)" +
                ");";

        String createOrdineTable = "CREATE TABLE IF NOT EXISTS Ordine (" +
                "idOrdine INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "stato TEXT NOT NULL, " +
                "costo REAL NOT NULL, " +
                "dataOraOrdine TEXT NOT NULL, " +
                "pagato INTEGER NOT NULL, " +
                "indirizzo TEXT NOT NULL" +
                ");";

        String createCarrelloTable = "CREATE TABLE IF NOT EXISTS Carrello (" +
                "idCarrello INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quantitaPiatti INTEGER NOT NULL" +
                ");";

        String createMetodoDiPagamentoTable = "CREATE TABLE IF NOT EXISTS MetodoDiPagamento (" +
                "nominativo TEXT NOT NULL, " +
                "numeroCarta TEXT PRIMARY KEY, " +
                "scadenza TEXT NOT NULL" +
                ");";

        String createRistoranteTable = "CREATE TABLE IF NOT EXISTS Ristorante (" +
                "idRistorante INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "indirizzo TEXT NOT NULL" +
                ");";

        String createMenuTable = "CREATE TABLE IF NOT EXISTS Menu (" +
                "nome TEXT PRIMARY KEY" +
                ");";

        String createPiattoTable = "CREATE TABLE IF NOT EXISTS Piatto (" +
                "nome TEXT PRIMARY KEY, " +
                "disponibile INTEGER NOT NULL, " +
                "prezzo TEXT NOT NULL, " +
                "allergeni TEXT, " +
                "foto TEXT" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUtenteTable);
            stmt.execute(createClienteTable);
            stmt.execute(createTitolareTable);
            stmt.execute(createCorriereTable);
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
    }

    // Metodo main per testare la creazione del database
    public static void main(String[] args) {
        createTables();
    }
}

