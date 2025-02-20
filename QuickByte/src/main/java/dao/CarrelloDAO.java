package dao;

import database.DatabaseConnection; // Importa il package database
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Carrello;  // Assicurati di avere la classe Carrello nel package model

public class CarrelloDAO {

    private Connection connection;

    // Modifica del costruttore per usare DatabaseConnection.connect()
    public CarrelloDAO() throws SQLException {
        this.connection = DatabaseConnection.connect(); // Ottieni la connessione dal DatabaseConnection
    }

    // Metodo per creare la tabella Carrello
    public void createTable() throws SQLException {
        String createCarrelloTable = "CREATE TABLE IF NOT EXISTS Carrello (" +
                "idCarrello INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quantitaPiatti INTEGER NOT NULL, " +
                "idPiatto INTEGER NOT NULL, " +
                "ordine INTEGER, " +
                "emailUtente TEXT, " +
                "FOREIGN KEY(idPiatto) REFERENCES Piatto(idPiatto), " +
                "FOREIGN KEY(ordine) REFERENCES Ordine(idOrdine), " +
                "FOREIGN KEY(emailUtente) REFERENCES Utente(email), " +
                "UNIQUE(emailUtente, idPiatto)" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createCarrelloTable);
        }
    }

    // Metodo per aggiungere un elemento al carrello
    public void aggiungiAlCarrello(Carrello carrello) throws SQLException {
        String insertQuery = "INSERT INTO Carrello (quantitaPiatti, idPiatto, ordine, emailUtente) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setInt(1, carrello.getQuantitaPiatti());
            ps.setInt(2, carrello.getIdPiatto());
            ps.setInt(3, carrello.getOrdine() != null ? carrello.getOrdine() : null);
            ps.setString(4, carrello.getEmailUtente());
            ps.executeUpdate();
        }
    }

    // Metodo per ottenere il carrello di un utente specifico
    public List<Carrello> getCarrelloByUtente(String emailUtente) throws SQLException {
        String selectQuery = "SELECT * FROM Carrello WHERE emailUtente = ?";
        List<Carrello> carrelli = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idCarrello = rs.getInt("idCarrello");
                    int quantitaPiatti = rs.getInt("quantitaPiatti");
                    int idPiatto = rs.getInt("idPiatto");
                    Integer ordine = rs.getInt("ordine");
                    String email = rs.getString("emailUtente");

                    Carrello carrello = new Carrello(idCarrello, quantitaPiatti, idPiatto, ordine, email);
                    carrelli.add(carrello);
                }
            }
        }
        return carrelli;
    }

    // Metodo per rimuovere un elemento dal carrello
    public void rimuoviDalCarrello(int idCarrello) throws SQLException {
        String deleteQuery = "DELETE FROM Carrello WHERE idCarrello = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, idCarrello);
            ps.executeUpdate();
        }
    }

    // Metodo per aggiornare la quantità di piatti nel carrello
    public void aggiornaQuantita(int idCarrello, int quantita) throws SQLException {
        String updateQuery = "UPDATE Carrello SET quantitaPiatti = ? WHERE idCarrello = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setInt(1, quantita);
            ps.setInt(2, idCarrello);
            ps.executeUpdate();
        }
    }

    // Metodo per svuotare il carrello di un utente
    public void svuotaCarrello(String emailUtente) throws SQLException {
        String deleteQuery = "DELETE FROM Carrello WHERE emailUtente = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setString(1, emailUtente);
            ps.executeUpdate();
        }
    }
}
