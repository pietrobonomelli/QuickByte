package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Piatto;

public class PiattoDAO {

    private Connection connection;

    public PiattoDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo per creare la tabella Piatto
    public void createTable() throws SQLException {
        String createPiattoTable = "CREATE TABLE IF NOT EXISTS Piatto (" +
                "idPiatto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "disponibile INTEGER NOT NULL, " +
                "prezzo TEXT NOT NULL, " +
                "allergeni TEXT, " +
                "foto TEXT, " +
                "nomeMenu TEXT NOT NULL, " +
                "idRistorante INTEGER NOT NULL, " +
                "FOREIGN KEY(nomeMenu, idRistorante) REFERENCES Menu(nome, idRistorante) ON DELETE CASCADE" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createPiattoTable);
        }
    }

    // Metodo per aggiungere un piatto
    public void aggiungiPiatto(Piatto piatto) throws SQLException {
        String insertQuery = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, piatto.getNome());
            ps.setInt(2, piatto.isDisponibile() ? 1 : 0);
            ps.setString(3, piatto.getPrezzo());
            ps.setString(4, piatto.getAllergeni());
            ps.setString(5, piatto.getFoto());
            ps.setString(6, piatto.getNomeMenu());
            ps.setInt(7, piatto.getIdRistorante());
            ps.executeUpdate();
        }
    }

    // Metodo per ottenere tutti i piatti di un menu
    public List<Piatto> getPiattiByMenu(String nomeMenu, int idRistorante) throws SQLException {
        String selectQuery = "SELECT * FROM Piatto WHERE nomeMenu = ? AND idRistorante = ?";
        List<Piatto> piatti = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, nomeMenu);
            ps.setInt(2, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPiatto = rs.getInt("idPiatto");
                    String nome = rs.getString("nome");
                    boolean disponibile = rs.getInt("disponibile") == 1;
                    String prezzo = rs.getString("prezzo");
                    String allergeni = rs.getString("allergeni");
                    String foto = rs.getString("foto");
                    piatti.add(new Piatto(idPiatto, nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante));
                }
            }
        }
        return piatti;
    }

    // Metodo per aggiornare un piatto
    public void aggiornaPiatto(Piatto piatto) throws SQLException {
        String updateQuery = "UPDATE Piatto SET nome = ?, disponibile = ?, prezzo = ?, allergeni = ?, foto = ?, nomeMenu = ?, idRistorante = ? " +
                "WHERE idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, piatto.getNome());
            ps.setInt(2, piatto.isDisponibile() ? 1 : 0);
            ps.setString(3, piatto.getPrezzo());
            ps.setString(4, piatto.getAllergeni());
            ps.setString(5, piatto.getFoto());
            ps.setString(6, piatto.getNomeMenu());
            ps.setInt(7, piatto.getIdRistorante());
            ps.setInt(8, piatto.getIdPiatto());
            ps.executeUpdate();
        }
    }

    // Metodo per eliminare un piatto
    public void rimuoviPiatto(int idPiatto) throws SQLException {
        String deleteQuery = "DELETE FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, idPiatto);
            ps.executeUpdate();
        }
    }
}
