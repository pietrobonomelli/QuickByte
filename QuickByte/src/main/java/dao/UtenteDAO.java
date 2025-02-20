package dao;

import database.DatabaseConnection;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {
    
    // 📌 Ottenere un utente tramite email
    public Utente getUtenteByEmail(String email) {
        String query = "SELECT * FROM Utente WHERE email = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creaUtenteDaResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 📌 Ottenere tutti gli utenti
    public List<Utente> getAllUtenti() {
        List<Utente> utenti = new ArrayList<>();
        String query = "SELECT * FROM Utente";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                utenti.add(creaUtenteDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utenti;
    }

    // 📌 Inserire un nuovo utente
    public boolean insertUtente(Utente utente, String tipoUtente) {
        String query = "INSERT INTO Utente (email, password, nome, telefono, tipoUtente) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, utente.getEmail());
            stmt.setString(2, utente.getPassword());
            stmt.setString(3, utente.getNome());
            stmt.setString(4, utente.getTelefono());
            stmt.setString(5, tipoUtente);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 📌 Eliminare un utente tramite email
    public boolean deleteUtente(String email) {
        String query = "DELETE FROM Utente WHERE email = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 📌 Aggiornare i dati di un utente
    public boolean updateUtente(Utente utente) {
        String query = "UPDATE Utente SET password = ?, nome = ?, telefono = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, utente.getPassword());
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getTelefono());
            stmt.setString(4, utente.getEmail());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 📌 Metodo per creare l'oggetto corretto dal database
    private Utente creaUtenteDaResultSet(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String telefono = rs.getString("telefono");
        String tipoUtente = rs.getString("tipoUtente");

        if ("CLIENTE".equals(tipoUtente)) {
            return new Cliente(email, password, nome, telefono);
        } else if ("TITOLARE".equals(tipoUtente)) {
            return new Titolare(email, password, nome, telefono);
        } else if ("CORRIERE".equals(tipoUtente)) {
            return new Corriere(email, password, nome, telefono);
        } else {
            return null;
        }
    }

}
