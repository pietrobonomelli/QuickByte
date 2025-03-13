package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Indirizzo;
import database.DatabaseConnection;

public class IndirizzoDAO {

    private static IndirizzoDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern SINGLETON.
     */
    private IndirizzoDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return L'istanza singola di IndirizzoDAO.
     */
    public static IndirizzoDAO getInstance() {
        if (instance == null) {
            instance = new IndirizzoDAO();
        }
        return instance;
    }

    /**
     * Crea la tabella Indirizzo nel database.
     *
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Indirizzo (" +
                "idIndirizzo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "indirizzo TEXT NOT NULL, " +
                "citta TEXT NOT NULL, " +
                "cap TEXT NOT NULL, " +
                "provincia TEXT NOT NULL, " +
                "emailUtente TEXT, " +
                "FOREIGN KEY(emailUtente) REFERENCES Utente(email) ON DELETE CASCADE" +
                ");";
        executeUpdate(createTableSQL);
    }

    /**
     *
     * @param indirizzo L'indirizzo da aggiungere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiungiIndirizzo(Indirizzo indirizzo) throws SQLException {
        String insertSQL = "INSERT INTO Indirizzo (indirizzo, citta, cap, provincia, emailUtente) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSQL)) {
            setIndirizzoParameters(ps, indirizzo);
            ps.executeUpdate();
        }
    }

    /**
     * Imposta i parametri per il PreparedStatement.
     *
     * @param ps Il PreparedStatement da configurare.
     * @param indirizzo L'indirizzo con i dati da impostare.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private void setIndirizzoParameters(PreparedStatement ps, Indirizzo indirizzo) throws SQLException {
        ps.setString(1, indirizzo.getIndirizzo());
        ps.setString(2, indirizzo.getCitta());
        ps.setString(3, indirizzo.getCap());
        ps.setString(4, indirizzo.getProvincia());
        ps.setString(5, indirizzo.getEmailUtente());
    }

    /**
     * Ottiene tutti gli indirizzi di un utente.
     *
     * @param emailUtente L'email dell'utente di cui ottenere gli indirizzi.
     * @return Una lista di indirizzi.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public List<Indirizzo> getIndirizziByUtente(String emailUtente) throws SQLException {
        String selectSQL = "SELECT * FROM Indirizzo WHERE emailUtente = ?";
        List<Indirizzo> indirizzi = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectSQL)) {
            ps.setString(1, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    indirizzi.add(mapResultSetToIndirizzo(rs));
                }
            }
        }
        return indirizzi;
    }

    /**
     * Mappa un ResultSet a un oggetto Indirizzo.
     *
     * @param rs Il ResultSet da mappare.
     * @return Un oggetto Indirizzo.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private Indirizzo mapResultSetToIndirizzo(ResultSet rs) throws SQLException {
        int idIndirizzo = rs.getInt("idIndirizzo");
        String indirizzo = rs.getString("indirizzo");
        String citta = rs.getString("citta");
        String cap = rs.getString("cap");
        String provincia = rs.getString("provincia");
        String emailUtente = rs.getString("emailUtente");
        return new Indirizzo(idIndirizzo, indirizzo, citta, cap, provincia, emailUtente);
    }

    /**
     * @param indirizzo L'indirizzo da aggiornare.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiornaIndirizzo(Indirizzo indirizzo) throws SQLException {
        String updateSQL = "UPDATE Indirizzo SET indirizzo = ?, citta = ?, cap = ?, provincia = ? WHERE idIndirizzo = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateSQL)) {
            ps.setString(1, indirizzo.getIndirizzo());
            ps.setString(2, indirizzo.getCitta());
            ps.setString(3, indirizzo.getCap());
            ps.setString(4, indirizzo.getProvincia());
            ps.setInt(5, indirizzo.getIdIndirizzo());
            ps.executeUpdate();
        }
    }

    /**
     * @param idIndirizzo L'ID dell'indirizzo da rimuovere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void rimuoviIndirizzo(int idIndirizzo) throws SQLException {
        String deleteSQL = "DELETE FROM Indirizzo WHERE idIndirizzo = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteSQL)) {
            ps.setInt(1, idIndirizzo);
            ps.executeUpdate();
        }
    }

    /**
     * Ottiene gli indirizzi di un utente.
     *
     * @param emailUtente L'email dell'utente di cui ottenere gli indirizzi.
     * @return Una lista di indirizzi.
     */
    public List<String> getIndirizzi(String emailUtente) {
        List<String> indirizzi = new ArrayList<>();
        String sql = "SELECT indirizzo FROM Indirizzo WHERE emailUtente = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailUtente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    indirizzi.add(rs.getString("indirizzo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return indirizzi;
    }

    /**
     * Chiude la connessione al database.
     */
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Esegue aggiornamento SQL.
     *
     * @param sql La query SQL da eseguire.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private void executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}
