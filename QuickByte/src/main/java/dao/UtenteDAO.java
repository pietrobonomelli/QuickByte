package dao;

import database.DatabaseConnection;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import utilities.Utilities;

/**
 * Classe DAO per la gestione degli utenti nel database.
 */
public class UtenteDAO {

    private static UtenteDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private UtenteDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    /**
     * Restituisce l'istanza singleton di UtenteDAO.
     *
     * @return L'istanza singleton di UtenteDAO.
     */
    public static UtenteDAO getInstance() {
        if (instance == null) {
            instance = new UtenteDAO();
        }
        return instance;
    }

    /**
     * Recupera un utente dal database tramite l'email.
     *
     * @param email L'email dell'utente da recuperare.
     * @return L'oggetto Utente se trovato, altrimenti null.
     */
    public Utente getUtenteByEmail(String email) {
        String query = "SELECT * FROM Utente WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Ritorna utente esistente");
                    return creaUtenteDaResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Ritorna nessun utente esistente");
        return null;
    }

    /**
     * Recupera tutti gli utenti dal database.
     *
     * @return Una lista di tutti gli utenti.
     */
    public List<Utente> getAllUtenti() {
        List<Utente> utenti = new ArrayList<>();
        String query = "SELECT * FROM Utente";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                utenti.add(creaUtenteDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utenti;
    }

    /**
     * Inserisce un nuovo utente nel database.
     *
     * @param utente L'oggetto Utente da inserire.
     * @param tipoUtente Il tipo di utente (Cliente, Titolare, Corriere).
     * @return true se l'inserimento è avvenuto con successo, false altrimenti.
     * @throws SQLException Se si verifica un errore SQL o se il tipo utente non è valido.
     */
    public boolean insertUtente(Utente utente, String tipoUtente) throws SQLException {
        if (!isValidUserType(tipoUtente)) {
            throw new SQLException("Tipo utente non valido: " + tipoUtente);
        }
        if (getUtenteByEmail(utente.getEmail()) != null) {
            System.out.println("L'utente con questa email esiste già");
            return false;
        }

        String query = "INSERT INTO Utente (email, password, nome, telefono, tipoUtente) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setUtenteStatementParameters(stmt, utente, tipoUtente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Imposta i parametri per l'inserimento di un utente nel PreparedStatement.
     *
     * @param stmt Il PreparedStatement da configurare.
     * @param utente L'oggetto Utente da inserire.
     * @param tipoUtente Il tipo di utente.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private void setUtenteStatementParameters(PreparedStatement stmt, Utente utente, String tipoUtente) throws SQLException {
        stmt.setString(1, utente.getEmail());
        stmt.setString(2, utente.getPassword());
        stmt.setString(3, utente.getNome());
        stmt.setString(4, utente.getTelefono());
        stmt.setString(5, tipoUtente);
    }

    /**
     * Verifica se il tipo di utente è valido.
     *
     * @param tipoUtente Il tipo di utente da verificare.
     * @return true se il tipo è valido, false altrimenti.
     */
    private boolean isValidUserType(String tipoUtente) {
        return "Cliente".equals(tipoUtente) || "Titolare".equals(tipoUtente) || "Corriere".equals(tipoUtente);
    }

    /**
     * Elimina un utente dal database tramite l'email.
     *
     * @param email L'email dell'utente da eliminare.
     * @return true se l'eliminazione è avvenuta con successo, false altrimenti.
     */
    public boolean deleteUtente(String email) {
        String query = "DELETE FROM Utente WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Aggiorna le informazioni di un utente nel database.
     *
     * @param utente L'oggetto Utente con le nuove informazioni.
     * @return true se l'aggiornamento è avvenuto con successo, false altrimenti.
     */
    public boolean updateUtente(Utente utente) {
        String query = "UPDATE Utente SET password = ?, nome = ?, telefono = ? WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, utente.getPassword());
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getTelefono());
            stmt.setString(4, utente.getEmail());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Aggiorna le informazioni di un utente nel database.
     *
     * @param utente L'oggetto Utente con le nuove informazioni.
     * @return true se l'aggiornamento è avvenuto con successo, false altrimenti.
     */
    public boolean updateUtente(String emailUtente, String nuovaPassword, String nuovoNome, String nuovoTelefono) {
        String query = "UPDATE Utente SET password = ?, nome = ?, telefono = ? WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nuovaPassword);
            stmt.setString(2, nuovoNome);
            stmt.setString(3, nuovoTelefono);
            stmt.setString(4, emailUtente);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Crea un oggetto Utente da un ResultSet.
     *
     * @param rs Il ResultSet da cui creare l'oggetto Utente.
     * @return L'oggetto Utente creato.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public Utente creaUtenteDaResultSet(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String telefono = rs.getString("telefono");
        String tipoUtente = rs.getString("tipoUtente");
    
        switch (tipoUtente) {
	        case "Cliente":
	        	return new Cliente(email, password, nome, telefono);
	        case "Corriere":
	        	return new Corriere(email, password, nome, telefono);
	        case "Titolare":
	        	return new Titolare(email, password, nome, telefono);
	        default:
	            Utilities.showAlert("Errore", "Tipo utente non riconosciuto.");
	            return null;
        }
    }
    
    /**
     * Recupera il tipo di utente dal database tramite l'email.
     *
     * @param email L'email dell'utente da cui recuperare il tipo.
     * @return la stringa contentente tipoUtente se trovato, altrimenti null.
     */
    public String getTipoUtenteByEmail(String email) {
        String query = "SELECT tipoUtente FROM Utente WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Ritorna utente esistente");
                    return rs.getString("tipoUtente");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Ritorna nessun utente esistente");
        return null;
    }
}
