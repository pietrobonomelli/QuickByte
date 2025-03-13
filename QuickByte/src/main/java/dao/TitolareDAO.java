package dao;

import java.sql.*;
import database.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import sessione.SessioneUtente;

/**
 * Classe DAO per la gestione dei titolari dei ristoranti nel database.
 */
public class TitolareDAO {

    private static TitolareDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private TitolareDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            System.err.println("Errore nella connessione al database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Restituisce l'istanza singleton di TitolareDAO.
     *
     * @return L'istanza singleton di TitolareDAO.
     */
    public static TitolareDAO getInstance() {
        if (instance == null) {
            instance = new TitolareDAO();
        }
        return instance;
    }

    /**
     * Recupera gli ID dei ristoranti associati all'email del titolare corrente.
     *
     * @return Una lista di ID dei ristoranti associati all'email del titolare.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public List<Integer> getRistorantiByEmail() throws SQLException {
        List<Integer> ristoranti = new ArrayList<>();
        String titolare = SessioneUtente.getEmail();
        String sql = "SELECT idRistorante FROM Ristorante WHERE emailTitolare = ?";

        // Verifica e riconnetti se necessario
        checkConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, titolare);
            return executeQueryAndRetrieveIds(stmt);
        } catch (SQLException e) {
            System.err.println("Errore durante l'esecuzione della query: " + e.getMessage());
            e.printStackTrace();
        }

        return ristoranti;
    }

    /**
     * Verifica la connessione al database e riconnette se necessario.
     *
     * @throws SQLException Se si verifica un errore SQL durante la riconnessione.
     */
    private void checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DatabaseConnection.connect();
            } catch (SQLException e) {
                System.err.println("Errore durante la riconnessione al database: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Esegue una query e restituisce una lista di ID dei ristoranti.
     *
     * @param stmt Il PreparedStatement da eseguire.
     * @return Una lista di ID dei ristoranti risultanti dalla query.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private List<Integer> executeQueryAndRetrieveIds(PreparedStatement stmt) throws SQLException {
        List<Integer> ristoranti = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int ristoranteId = rs.getInt("idRistorante");
                ristoranti.add(ristoranteId);
            }
        }
        return ristoranti;
    }
}
