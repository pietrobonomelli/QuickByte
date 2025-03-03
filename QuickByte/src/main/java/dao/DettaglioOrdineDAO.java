package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.DettaglioOrdine;
import database.DatabaseConnection;

public class DettaglioOrdineDAO {
    private static DettaglioOrdineDAO instance;
    private Connection connection;

    private DettaglioOrdineDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per ottenere l'istanza singola (Singleton)
    public static DettaglioOrdineDAO getInstance() {
        if (instance == null) {
            instance = new DettaglioOrdineDAO();
        }
        return instance;
    }

    // Metodo per creare la tabella DettaglioOrdine
    public void createTable() throws SQLException {
        String createDettaglioOrdineTable = "CREATE TABLE IF NOT EXISTS DettaglioOrdine (" +
                "idOrdine INTEGER, " +
                "idPiatto INTEGER, " +
                "quantita INTEGER NOT NULL, " +
                "PRIMARY KEY (idOrdine, idPiatto), " +
                "FOREIGN KEY(idOrdine) REFERENCES Ordine(idOrdine), " +
                "FOREIGN KEY(idPiatto) REFERENCES Piatto(idPiatto)" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createDettaglioOrdineTable);
        }
    }

    // Metodo per aggiungere un dettaglio ordine
    public void aggiungiDettaglioOrdine(DettaglioOrdine dettaglio) throws SQLException {
        String insertQuery = "INSERT INTO DettaglioOrdine (idOrdine, idPiatto, quantita) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setInt(1, dettaglio.getIdOrdine());
            ps.setInt(2, dettaglio.getIdPiatto());
            ps.setInt(3, dettaglio.getQuantita());
            ps.executeUpdate();
        }
    }

    // Metodo per ottenere i dettagli di un ordine
    public List<DettaglioOrdine> getDettagliByOrdine(int idOrdine) throws SQLException {
        String selectQuery = "SELECT * FROM DettaglioOrdine WHERE idOrdine = ?";
        List<DettaglioOrdine> dettagli = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPiatto = rs.getInt("idPiatto");
                    int quantita = rs.getInt("quantita");
                    dettagli.add(new DettaglioOrdine(idOrdine, idPiatto, quantita));
                }
            }
        }
        return dettagli;
    }

    // Metodo per aggiornare la quantità di un piatto in un ordine
    public void aggiornaQuantita(int idOrdine, int idPiatto, int nuovaQuantita) throws SQLException {
        String updateQuery = "UPDATE DettaglioOrdine SET quantita = ? WHERE idOrdine = ? AND idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setInt(1, nuovaQuantita);
            ps.setInt(2, idOrdine);
            ps.setInt(3, idPiatto);
            ps.executeUpdate();
        }
    }

    // Metodo per eliminare un piatto da un ordine
    public void rimuoviDettaglio(int idOrdine, int idPiatto) throws SQLException {
        String deleteQuery = "DELETE FROM DettaglioOrdine WHERE idOrdine = ? AND idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, idOrdine);
            ps.setInt(2, idPiatto);
            ps.executeUpdate();
        }
    }
}