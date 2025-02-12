package logica.utenti;

import java.sql.*;
import database.DatabaseConnection;


public class Utente {
    private String email;
    private String password;
    private String nome;
    private String telefono;

    public Utente(String email, String password, String nome, String telefono) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    public void registrazione() {
    	String query = "INSERT INTO utenti (email, password, nome, telefono) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, nome);
            statement.setString(4, telefono);
            statement.executeUpdate();
            System.out.println("RegisterScreen completata con successo.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean login() {
        String query = "SELECT * FROM utenti WHERE email = ? AND password = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                System.out.println("Login effettuato con successo.");
                return true;
            } else {
                System.out.println("Credenziali non valide.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void modificaProfilo(String nuovoNome, String nuovoTelefono, String nuovaPassword) {
        String query = "UPDATE utenti SET nome = ?, telefono = ?, password = ? WHERE email = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statementmt = connection.prepareStatement(query)) {
            statementmt.setString(1, nuovoNome);
            statementmt.setString(2, nuovoTelefono);
            statementmt.setString(3, nuovaPassword);
            statementmt.setString(4, email); 
            
        
            int rowsUpdated = statementmt.executeUpdate();
            if (rowsUpdated > 0) {
                this.nome = nuovoNome;
                this.telefono = nuovoTelefono;
                this.password = nuovaPassword;
                
                System.out.println("Profilo aggiornato con successo.");
            } else {
                System.out.println("Errore: Nessun profilo trovato con l'email specificata.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void logout() {
        System.out.println("Logout effettuato con successo.");
    }
    
}