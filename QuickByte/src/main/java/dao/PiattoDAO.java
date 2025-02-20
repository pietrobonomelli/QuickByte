package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import model.Piatto;

public class PiattoDAO {

    private Connection connection;

    public PiattoDAO() throws SQLException {
        this.connection = DatabaseConnection.connect();
    }

    // Metodo per ottenere un piatto tramite il suo ID
    public Piatto getPiattoById(int idPiatto) throws SQLException {
        String query = "SELECT * FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idPiatto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Piatto(
                        rs.getInt("idPiatto"),
                        rs.getString("nome"),
                        rs.getInt("disponibile") == 1,
                        rs.getString("prezzo"),
                        rs.getString("allergeni"),
                        rs.getString("foto"),
                        rs.getString("nomeMenu"),
                        rs.getInt("idRistorante")
                    );
                }
            }
        }
        return null;
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

    // Metodo per ottenere tutti i piatti di un menu per un determinato ristorante
    public List<Piatto> getPiattiByMenuAndIdRistorante(String nomeMenu, int idRistorante) throws SQLException {
        String selectQuery = "SELECT * FROM Piatto WHERE nomeMenu = ? AND idRistorante = ?";
        List<Piatto> piatti = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, nomeMenu);
            ps.setInt(2, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPiatto = rs.getInt("idPiatto");
                    String nome = rs.getString("nome");
                    int disponibile = rs.getInt("disponibile");
                    boolean isDisponibile = disponibile == 1;
                    String prezzo = rs.getString("prezzo");
                    String allergeni = rs.getString("allergeni");
                    String foto = rs.getString("foto");
                    piatti.add(new Piatto(idPiatto, nome, isDisponibile, prezzo, allergeni, foto, nomeMenu, idRistorante));
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

 // Metodo per aggiungere un piatto al carrello
    public void aggiungiPiattoAlCarrello(int idPiatto, String emailCliente) throws SQLException {
        String checkCarrelloQuery = "SELECT quantitaPiatti FROM Carrello WHERE idPiatto = ? AND emailUtente = ?";
        String updateCarrelloQuery = "UPDATE Carrello SET quantitaPiatti = quantitaPiatti + 1 WHERE idPiatto = ? AND emailUtente = ?";
        String insertCarrelloQuery = "INSERT INTO Carrello (idPiatto, emailUtente, quantitaPiatti) VALUES (?, ?, ?)";

        try (PreparedStatement checkPs = connection.prepareStatement(checkCarrelloQuery)) {
            checkPs.setInt(1, idPiatto);
            checkPs.setString(2, emailCliente);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // Il piatto è già presente, quindi aggiorniamo la quantità
                try (PreparedStatement updatePs = connection.prepareStatement(updateCarrelloQuery)) {
                    updatePs.setInt(1, idPiatto);
                    updatePs.setString(2, emailCliente);
                    updatePs.executeUpdate();
                }
            } else {
                // Il piatto non è presente, quindi lo inseriamo nel carrello
                try (PreparedStatement insertPs = connection.prepareStatement(insertCarrelloQuery)) {
                    insertPs.setInt(1, idPiatto);
                    insertPs.setString(2, emailCliente);
                    insertPs.setInt(3, 1); // Quantità iniziale di 1
                    insertPs.executeUpdate();
                }
            }
        }
    }



    // Metodo per svuotare il carrello
    public void svuotaCarrello(String emailCliente) throws SQLException {
        String deleteCarrelloQuery = "DELETE FROM Carrello WHERE emailCliente = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteCarrelloQuery)) {
            ps.setString(1, emailCliente);
            ps.executeUpdate();
        }
    }
}
