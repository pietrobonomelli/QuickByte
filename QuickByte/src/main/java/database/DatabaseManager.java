package database;

import java.sql.Connection;
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
                "piatto TEXT NOT NULL, " +
                "ordine INTEGER NOT NULL, " +
                "FOREIGN KEY(piatto) REFERENCES Piatto(nome), " +
                "FOREIGN KEY(ordine) REFERENCES Ordine(idOrdine)" +
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
                "FOREIGN KEY(emailTitolare) REFERENCES Utente(email)" +
                ");";

        String createMenuTable = "CREATE TABLE IF NOT EXISTS Menu (" +
                "nome TEXT NOT NULL, " +
                "idRistorante INTEGER NOT NULL, " +
                "PRIMARY KEY (nome, idRistorante), " +  // Chiave primaria composta
                "FOREIGN KEY (idRistorante) REFERENCES Ristorante(idRistorante)" +
                ");";


        String createPiattoTable = "CREATE TABLE IF NOT EXISTS Piatto (" +
                "nome TEXT PRIMARY KEY, " +
                "disponibile INTEGER NOT NULL, " +
                "prezzo TEXT NOT NULL, " +
                "allergeni TEXT, " +
                "foto TEXT, " +
                "nomeMenu TEXT, " +
                "FOREIGN KEY(nomeMenu) REFERENCES Menu(nome)" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
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
    }
}
