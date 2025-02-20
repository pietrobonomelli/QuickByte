package dao;

import database.DatabaseConnection;
import model.Ordine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAO {

    // 📌 Creare un nuovo ordine
    public boolean insertOrdine(Ordine ordine) {
        String query = "INSERT INTO Ordine (stato, costo, dataOraOrdine, pagato, indirizzo, emailCliente, emailCorriere, idRistorante) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ordine.getStato());
            stmt.setDouble(2, ordine.getCosto());
            stmt.setString(3, ordine.getDataOraOrdine());
            stmt.setInt(4, ordine.getPagato());
            stmt.setString(5, ordine.getIndirizzo());
            stmt.setString(6, ordine.getEmailCliente());
            stmt.setString(7, ordine.getEmailCorriere());
            stmt.setInt(8, ordine.getIdRistorante());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 📌 Recuperare un ordine tramite ID
    public Ordine getOrdineById(int idOrdine) {
        String query = "SELECT * FROM Ordine WHERE idOrdine = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idOrdine);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creaOrdineDaResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 📌 Recuperare tutti gli ordini
    public List<Ordine> getAllOrdini() {
        List<Ordine> ordini = new ArrayList<>();
        String query = "SELECT * FROM Ordine";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ordini.add(creaOrdineDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    // 📌 Aggiornare lo stato di un ordine
    public boolean updateStatoOrdine(int idOrdine, String nuovoStato) {
        String query = "UPDATE Ordine SET stato = ? WHERE idOrdine = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nuovoStato);
            stmt.setInt(2, idOrdine);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 📌 Eliminare un ordine tramite ID
    public boolean deleteOrdine(int idOrdine) {
        String query = "DELETE FROM Ordine WHERE idOrdine = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idOrdine);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 📌 Metodo per creare l'oggetto Ordine dal ResultSet
    private Ordine creaOrdineDaResultSet(ResultSet rs) throws SQLException {
        int idOrdine = rs.getInt("idOrdine");
        String stato = rs.getString("stato");
        double costo = rs.getDouble("costo");
        String dataOraOrdine = rs.getString("dataOraOrdine");
        int pagato = rs.getInt("pagato");
        String indirizzo = rs.getString("indirizzo");
        String emailCliente = rs.getString("emailCliente");
        String emailCorriere = rs.getString("emailCorriere");
        int idRistorante = rs.getInt("idRistorante");

        return new Ordine(idOrdine, stato, costo, dataOraOrdine, pagato, indirizzo, emailCliente, emailCorriere, idRistorante);
    }
}
