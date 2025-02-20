package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {
    
    // Metodo per verificare le credenziali dell'utente
    public static boolean verifyUser(String email, String password) {
        String query = "SELECT * FROM Utente WHERE email = ? AND password = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
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
    public static String getUserType(String email) {
        String query = "SELECT tipoUtente FROM Utente WHERE email = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
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