package dao;

import java.sql.*;
import database.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import model.Indirizzo;

public class IndirizzoDAO {
    
    private static IndirizzoDAO instance;
    private Connection connection;

    private IndirizzoDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static IndirizzoDAO getInstance() {
        if (instance == null) {
            instance = new IndirizzoDAO();
        }
        return instance;
    }

    // Metodo per creare la tabella Indirizzo
    public void createTable() throws SQLException {
        String createIndirizzoTable = "CREATE TABLE IF NOT EXISTS Indirizzo (" +
                "idIndirizzo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "indirizzo TEXT NOT NULL, " +
                "citta TEXT NOT NULL, " +
                "cap TEXT NOT NULL, " +
                "provincia TEXT NOT NULL, " +
                "emailUtente TEXT, " +
                "FOREIGN KEY(emailUtente) REFERENCES Utente(email) ON DELETE CASCADE" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createIndirizzoTable);
        }
    }

    // Metodo per aggiungere un indirizzo
    public void aggiungiIndirizzo(Indirizzo indirizzo) throws SQLException {
        String insertQuery = "INSERT INTO Indirizzo (indirizzo, citta, cap, provincia, emailUtente) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, indirizzo.getIndirizzo());
            ps.setString(2, indirizzo.getCitta());
            ps.setString(3, indirizzo.getCap());
            ps.setString(4, indirizzo.getProvincia());
            ps.setString(5, indirizzo.getEmailUtente());
            ps.executeUpdate();
        }
    }

    // Metodo per ottenere tutti gli indirizzi di un utente
    public List<Indirizzo> getIndirizziByUtente(String emailUtente) throws SQLException {
        String selectQuery = "SELECT * FROM Indirizzo WHERE emailUtente = ?";
        List<Indirizzo> indirizzi = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idIndirizzo = rs.getInt("idIndirizzo");
                    String indirizzo = rs.getString("indirizzo");
                    String citta = rs.getString("citta");
                    String cap = rs.getString("cap");
                    String provincia = rs.getString("provincia");
                    indirizzi.add(new Indirizzo(idIndirizzo, indirizzo, citta, cap, provincia, emailUtente));
                }
            }
        }
        return indirizzi;
    }

    // Metodo per aggiornare un indirizzo
    public void aggiornaIndirizzo(Indirizzo indirizzo) throws SQLException {
        String updateQuery = "UPDATE Indirizzo SET indirizzo = ?, citta = ?, cap = ?, provincia = ? WHERE idIndirizzo = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, indirizzo.getIndirizzo());
            ps.setString(2, indirizzo.getCitta());
            ps.setString(3, indirizzo.getCap());
            ps.setString(4, indirizzo.getProvincia());
            ps.setInt(5, indirizzo.getIdIndirizzo());
            ps.executeUpdate();
        }
    }

    // Metodo per eliminare un indirizzo
    public void rimuoviIndirizzo(int idIndirizzo) throws SQLException {
        String deleteQuery = "DELETE FROM Indirizzo WHERE idIndirizzo = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, idIndirizzo);
            ps.executeUpdate();
        }
    }

    // Metodo per ottenere gli indirizzi di un utente
    public List<String> getIndirizzi(String emailUtente) {
        List<String> indirizzi = new ArrayList<>();
        String sql = "SELECT indirizzo FROM Indirizzo WHERE emailUtente = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailUtente);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                indirizzi.add(rs.getString("indirizzo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return indirizzi;
    }

    // Metodo per chiudere la connessione, che può essere chiamato quando l'oggetto IndirizzoDAO non è più necessario
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
