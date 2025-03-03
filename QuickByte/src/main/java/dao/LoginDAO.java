package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

	private static LoginDAO instance;
	private Connection connection;

	private LoginDAO() {
		try {
			this.connection = DatabaseConnection.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static LoginDAO getInstance() {
		if(instance == null) {
			instance = new LoginDAO();
		}
		return instance;
	}
	// Metodo per verificare le credenziali dell'utente
	public boolean verifyUser(String email, String password) {
		String query = "SELECT * FROM Utente WHERE email = ? AND password = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, email);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();

			return resultSet.next();
		} catch (SQLException e) {
			System.out.println("Errore durante la verifica dell'utente: " + e.getMessage());
			return false;
		}
	}

	// Metodo per ottenere il tipo di utente
	public String getUserType(String email) {
		String query = "SELECT tipoUtente FROM Utente WHERE email = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, email);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getString("tipoUtente");
			}
		} catch (SQLException e) {
			System.out.println("Errore durante il recupero del tipo di utente: " + e.getMessage());
		}
		return null;
	}
}