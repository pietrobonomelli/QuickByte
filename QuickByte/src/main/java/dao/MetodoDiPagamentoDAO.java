package dao;

import database.DatabaseManager; // Importa il DatabaseManager
import java.sql.*;
import model.MetodoDiPagamento; // Assicurati di avere la classe MetodoDiPagamento nel package model

public class MetodoDiPagamentoDAO {

    private Connection connection;

    // Costruttore che utilizza DatabaseManager.connect()
    public MetodoDiPagamentoDAO() throws SQLException {
        this.connection = DatabaseManager.connect(); // Usa DatabaseManager per la connessione
    }

    // Metodo per creare la tabella MetodoDiPagamento
    public void createTable() throws SQLException {
        String createMetodoDiPagamentoTable = "CREATE TABLE IF NOT EXISTS MetodoDiPagamento (" +
                "nominativo TEXT NOT NULL, " +
                "numeroCarta TEXT PRIMARY KEY, " +
                "scadenza TEXT NOT NULL, " +
                "emailCliente TEXT, " +
                "FOREIGN KEY(emailCliente) REFERENCES Utente(email)" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createMetodoDiPagamentoTable);
        }
    }

    // Metodo per aggiungere un metodo di pagamento
    public void aggiungiMetodo(MetodoDiPagamento metodo) throws SQLException {
        String insertQuery = "INSERT INTO MetodoDiPagamento (nominativo, numeroCarta, scadenza, emailCliente) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, metodo.getNominativo());
            ps.setString(2, metodo.getNumeroCarta());
            ps.setString(3, metodo.getScadenza());
            ps.setString(4, metodo.getEmailCliente());
            ps.executeUpdate();
        }
    }

    // Metodo per ottenere un metodo di pagamento in base al numero della carta
    public MetodoDiPagamento getMetodoByNumeroCarta(String numeroCarta) throws SQLException {
        String selectQuery = "SELECT * FROM MetodoDiPagamento WHERE numeroCarta = ?";
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, numeroCarta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nominativo = rs.getString("nominativo");
                    String scadenza = rs.getString("scadenza");
                    String emailCliente = rs.getString("emailCliente");
                    return new MetodoDiPagamento(nominativo, numeroCarta, scadenza, emailCliente);
                }
            }
        }
        return null;  // Ritorna null se non trovato
    }

    // Metodo per aggiornare i dati di un metodo di pagamento
    public void aggiornaMetodo(MetodoDiPagamento metodo) throws SQLException {
        String updateQuery = "UPDATE MetodoDiPagamento SET nominativo = ?, scadenza = ?, emailCliente = ? " +
                "WHERE numeroCarta = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, metodo.getNominativo());
            ps.setString(2, metodo.getScadenza());
            ps.setString(3, metodo.getEmailCliente());
            ps.setString(4, metodo.getNumeroCarta());
            ps.executeUpdate();
        }
    }

    // Metodo per rimuovere un metodo di pagamento in base al numero della carta
    public void rimuoviMetodo(String numeroCarta) throws SQLException {
        String deleteQuery = "DELETE FROM MetodoDiPagamento WHERE numeroCarta = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setString(1, numeroCarta);
            ps.executeUpdate();
        }
    }
}
