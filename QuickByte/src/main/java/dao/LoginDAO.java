package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class LoginDAO {

    private static LoginDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern SINGLETON.
     */
    private LoginDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return L'istanza singola di LoginDAO.
     */
    public static LoginDAO getInstance() {
        if (instance == null) {
            instance = new LoginDAO();
        }
        return instance;
    }

    /**
     * Verifica le credenziali dell'utente.
     *
     * @param email L'email dell'utente.
     * @param password La password dell'utente.
     * @return true se le credenziali sono corrette, false altrimenti.
     */
    public boolean verifyUser(String email, String password) {
        String query = "SELECT password FROM Utente WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHashedPassword = resultSet.getString("password");
                    //confronta la password con l'hash salvato nel db
                    return BCrypt.checkpw(password, storedHashedPassword);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la verifica dell'utente: " + e.getMessage());
        }
        return false;
    }


    /**
     * Ottiene il tipo di utente.
     *
     * @param email L'email dell'utente di cui ottenere il tipo.
     * @return Il tipo di utente come stringa, o null se non trovato.
     */
    public String getUserType(String email) {
        String query = "SELECT tipoUtente FROM Utente WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("tipoUtente");
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero del tipo di utente: " + e.getMessage());
        }
        return null;
    }
}
