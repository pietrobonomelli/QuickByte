package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import javafx.scene.control.Alert;
import model.Piatto;
import sessione.SessioneCarrello;

public class PiattoDAO {

    private static PiattoDAO instance;
    private Connection connection;
    private static boolean alertEnabled = true;

    private PiattoDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PiattoDAO getInstance() {
        if (instance == null) {
            instance = new PiattoDAO();
        }
        return instance;
    }

    /**
     * Recupera un piatto tramite il suo ID.
     *
     * @param idPiatto l'ID del piatto da recuperare.
     * @return il piatto corrispondente all'ID specificato.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public Piatto getPiattoById(int idPiatto) throws SQLException {
        String query = "SELECT * FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setInt(1, idPiatto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return creaPiattoDaResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Aggiunge un nuovo piatto al database.
     *
     * @param piatto il piatto da aggiungere.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public void aggiungiPiatto(Piatto piatto) throws SQLException {
        connection.setAutoCommit(false);
        try {
            verificaEsistenzaRistorante(piatto.getIdRistorante());
            inserisciPiatto(piatto);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Errore durante l'aggiunta del piatto: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void verificaEsistenzaRistorante(int idRistorante) throws SQLException {
        String query = "SELECT 1 FROM Ristorante WHERE idRistorante = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Ristorante con ID " + idRistorante + " non esistente.");
                }
            }
        }
    }

    private void inserisciPiatto(Piatto piatto) throws SQLException {
        String query = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
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

    /**
     * Recupera tutti i piatti di un menu per un determinato ristorante.
     *
     * @param nomeMenu il nome del menu.
     * @param idRistorante l'ID del ristorante.
     * @return una lista di piatti corrispondenti al menu e al ristorante specificati.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public List<Piatto> getPiattiByMenuAndIdRistorante(String nomeMenu, int idRistorante) throws SQLException {
        String query = "SELECT * FROM Piatto WHERE nomeMenu = ? AND idRistorante = ?";
        return eseguiQueryPiatti(query, nomeMenu, idRistorante);
    }

    /**
     * Recupera tutti i piatti disponibili di un menu per un determinato ristorante.
     *
     * @param nomeMenu il nome del menu.
     * @param idRistorante l'ID del ristorante.
     * @return una lista di piatti disponibili corrispondenti al menu e al ristorante specificati.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public List<Piatto> getPiattiDisponibiliByMenuAndIdRistorante(String nomeMenu, int idRistorante) throws SQLException {
        String query = "SELECT * FROM Piatto WHERE nomeMenu = ? AND idRistorante = ? AND disponibile = 1";
        return eseguiQueryPiatti(query, nomeMenu, idRistorante);
    }

    private List<Piatto> eseguiQueryPiatti(String query, String nomeMenu, int idRistorante) throws SQLException {
        List<Piatto> piatti = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nomeMenu);
            ps.setInt(2, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    piatti.add(creaPiattoDaResultSet(rs));
                }
            }
        }
        return piatti;
    }

    private Piatto creaPiattoDaResultSet(ResultSet rs) throws SQLException {
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

    /**
     * Aggiorna le informazioni di un piatto esistente.
     *
     * @param piatto il piatto con le informazioni aggiornate.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public void aggiornaPiatto(Piatto piatto) throws SQLException {
        String query = "UPDATE Piatto SET disponibile = ?, prezzo = ?, allergeni = ? WHERE idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, piatto.isDisponibile() ? 1 : 0);
            ps.setString(2, piatto.getPrezzo());
            ps.setString(3, piatto.getAllergeni());
            ps.setInt(4, piatto.getIdPiatto());
            ps.executeUpdate();
        }
    }

    /**
     * Rimuove un piatto dal database.
     *
     * @param idPiatto l'ID del piatto da rimuovere.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public void rimuoviPiatto(int idPiatto) throws SQLException {
        String deletePiatto = "DELETE FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement psPiatto = connection.prepareStatement(deletePiatto)) {
            psPiatto.setInt(1, idPiatto);
            int rowsAffected = psPiatto.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nessun piatto trovato con l'ID: " + idPiatto);
            }
        }
    }


    /**
     * Aggiunge un piatto al carrello di un cliente.
     *
     * @param idPiatto l'ID del piatto da aggiungere.
     * @param emailCliente l'email del cliente.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public void aggiungiPiattoAlCarrello(int idPiatto, String emailCliente) throws SQLException {
        connection.setAutoCommit(false);
        try {
            verificaEsistenzaCliente(emailCliente);
            aggiornaCarrello(idPiatto, emailCliente);
            connection.commit();
            showAlert("Successo", "Piatto aggiunto al carrello!");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void verificaEsistenzaCliente(String emailCliente) throws SQLException {
        String query = "SELECT 1 FROM Utente WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, emailCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Cliente con email " + emailCliente + " non esistente.");
                }
            }
        }
    }

    private void aggiornaCarrello(int idPiatto, String emailCliente) throws SQLException {
        String checkQuery = "SELECT quantitaPiatti FROM Carrello WHERE idPiatto = ? AND emailUtente = ?";
        String updateQuery = "UPDATE Carrello SET quantitaPiatti = quantitaPiatti + 1 WHERE idPiatto = ? AND emailUtente = ?";
        String insertQuery = "INSERT INTO Carrello (idPiatto, emailUtente, quantitaPiatti) VALUES (?, ?, ?)";

        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, idPiatto);
            checkPs.setString(2, emailCliente);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    aggiornaQuantitaPiatto(updateQuery, idPiatto, emailCliente);
                } else {
                    inserisciNuovoPiattoInCarrello(insertQuery, idPiatto, emailCliente);
                }
            }
        }
    }

    private void aggiornaQuantitaPiatto(String query, int idPiatto, String emailCliente) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idPiatto);
            ps.setString(2, emailCliente);
            ps.executeUpdate();
        }
    }

    private void inserisciNuovoPiattoInCarrello(String query, int idPiatto, String emailCliente) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idPiatto);
            ps.setString(2, emailCliente);
            ps.setInt(3, 1);
            ps.executeUpdate();
        }
    }

    public static void setAlertEnabled(boolean enabled) {
        alertEnabled = enabled;
    }

    private void showAlert(String title, String message) {
        if (!alertEnabled) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Svuota il carrello di un utente.
     *
     * @param emailUtente l'email dell'utente.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public void svuotaCarrello(String emailUtente) throws SQLException {
        String query = "DELETE FROM Carrello WHERE emailUtente = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, emailUtente);
            ps.executeUpdate();
            SessioneCarrello.setPieno(false);
            SessioneCarrello.setIdRistorante(0);
        }
    }

    /**
     * Aggiorna la foto di un piatto.
     *
     * @param idPiatto l'ID del piatto da aggiornare.
     * @param nomeFoto il nome della nuova foto.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public void aggiornaFotoPiatto(int idPiatto, String nomeFoto) throws SQLException {
        String query = "UPDATE Piatto SET foto = ? WHERE idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nomeFoto);
            ps.setInt(2, idPiatto);
            ps.executeUpdate();
        }
    }

    /**
     * Recupera il nome della foto di un piatto.
     *
     * @param idPiatto l'ID del piatto.
     * @return il nome della foto del piatto.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    public String mostraFoto(int idPiatto) throws SQLException {
        String query = "SELECT foto FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idPiatto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("foto");
                }
            }
        }
        return null;
    }
}
