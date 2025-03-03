package dao;

import java.sql.*;
import model.Carrello;
import java.util.List;
import java.util.ArrayList;
import database.DatabaseConnection;

public class CarrelloDAO {
	private static CarrelloDAO instance;
	private Connection connection;

	private CarrelloDAO() {
		try {
			this.connection = DatabaseConnection.connect();
		} catch (SQLException e) {
			throw new RuntimeException("Errore nella connessione al database", e);
		}
	}

	public static CarrelloDAO getInstance() {
		if(instance == null) {
			instance = new CarrelloDAO();
		}
		return instance;
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
				String emailUtente1 = resultSet.getString("emailUtente");
				carrelli.add(new Carrello(idCarrello, quantitaPiatti, idPiatto, emailUtente1));
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

	// Metodo per ottenere il prezzo di un piatto in base all'idPiatto
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

}
