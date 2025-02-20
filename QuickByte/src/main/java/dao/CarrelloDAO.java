package dao;

import java.sql.*;
import model.Carrello;
import java.util.List;

import database.DatabaseConnection;

import java.util.ArrayList;

public class CarrelloDAO {

    private Connection connection;

    public CarrelloDAO() throws SQLException {
        this.connection = DatabaseConnection.connect();
    }

    // Metodo per ottenere il nome del piatto in base all'idPiatto
    public String getNomePiattoById(int idPiatto) throws SQLException {
        String query = "SELECT nome FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPiatto);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nome");
            }
        }
        return null;  // Restituisce null se non trovato
    }

    // Metodo per ottenere i carrelli di un utente
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
                int ordine = resultSet.getInt("ordine");
                String emailUtente1 = resultSet.getString("emailUtente");
                carrelli.add(new Carrello(idCarrello, quantitaPiatti, idPiatto, ordine, emailUtente1));
            }
        }
        return carrelli;
    }

    // Rimuovere dal carrello (esempio)
    public void rimuoviDalCarrello(int idCarrello) throws SQLException {
        String query = "DELETE FROM Carrello WHERE idCarrello = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idCarrello);
            statement.executeUpdate();
        }
    }
}
