package dao;

import java.sql.*;
import model.Carrello;
import java.util.List;
import java.util.ArrayList;
import database.DatabaseConnection;

/**
 * Classe DAO (Data Access Object) per gestire le operazioni relative al carrello nel database.
 */
public class CarrelloDAO {
    private static CarrelloDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Stabilisce una connessione al database.
     */
    private CarrelloDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    /**
     * Restituisce l'istanza singola di CarrelloDAO.
     *
     * @return L'istanza di CarrelloDAO.
     */
    public static CarrelloDAO getInstance() {
        if (instance == null) {
            instance = new CarrelloDAO();
        }
        return instance;
    }

    /**
     * Ottiene il nome di un piatto in base al suo ID.
     *
     * @param idPiatto L'ID del piatto.
     * @return Il nome del piatto o null se non trovato.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public String getNomePiattoById(int idPiatto) throws SQLException {
        String query = "SELECT nome FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPiatto);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nome");
            }
        }
        return null;  // Restituisce null se il piatto non è trovato
    }

    /**
     * Ottiene la lista dei carrelli associati a un utente.
     *
     * @param emailUtente L'email dell'utente.
     * @return La lista dei carrelli dell'utente.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public List<Carrello> getCarrelloByUtente(String emailUtente) throws SQLException {
        List<Carrello> carrelli = new ArrayList<>();
        String query = "SELECT * FROM Carrello WHERE emailUtente = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, emailUtente);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int idCarrello = resultSet.getInt("idCarrello");
                int quantitaPiatti = resultSet.getInt("quantitaPiatti");
                int idPiatto = resultSet.getInt("idPiatto");
                String emailUtente1 = resultSet.getString("emailUtente");
                carrelli.add(new Carrello(idCarrello, quantitaPiatti, idPiatto, emailUtente1));
            }
        }
        return carrelli;
    }

    /**
     * Rimuove un elemento dal carrello in base al suo ID.
     *
     * @param idCarrello L'ID del carrello da rimuovere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void rimuoviDalCarrello(int idCarrello) throws SQLException {
        String query = "DELETE FROM Carrello WHERE idCarrello = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idCarrello);
            statement.executeUpdate();
        }
    }

    /**
     * Svuota il carrello di un utente.
     *
     * @param emailUtente L'email dell'utente.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void svuotaCarrello(String emailUtente) throws SQLException {
        String query = "DELETE FROM Carrello WHERE emailUtente = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, emailUtente);
            statement.executeUpdate();
        }
    }

    /**
     * Ottiene il prezzo di un piatto in base al suo ID.
     *
     * @param idPiatto L'ID del piatto.
     * @return Il prezzo del piatto o 0 se non trovato.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public double getPrezzoPiattoById(int idPiatto) throws SQLException {
        String query = "SELECT prezzo FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPiatto);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("prezzo");
            }
        }
        return 0;  // Restituisce 0 se il piatto non è trovato
    }

    /**
     * Calcola il costo totale del carrello di un utente.
     *
     * @param emailUtente L'email dell'utente.
     * @return Il costo totale del carrello.
     */
    public double calcolaCostoTotale(String emailUtente) {
        double costoTotale = 0.0;
        String sql = "SELECT p.prezzo, c.quantitaPiatti FROM Carrello c JOIN Piatto p ON c.idPiatto = p.idPiatto WHERE c.emailUtente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emailUtente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double prezzo = rs.getDouble("prezzo");
                int quantita = rs.getInt("quantitaPiatti");
                costoTotale += prezzo * quantita;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return costoTotale;
    }

    /**
     * Aggiorna la quantità di un piatto nel carrello.
     *
     * @param idCarrello L'ID del carrello.
     * @param nuovaQuantita La nuova quantità del piatto.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiornaQuantita(int idCarrello, int nuovaQuantita) throws SQLException {
        String query = "UPDATE Carrello SET quantitaPiatti = ? WHERE idCarrello = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nuovaQuantita);
            statement.setInt(2, idCarrello);
            statement.executeUpdate();
        }
    }
}
