package dao;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.MetodoDiPagamento;

public class MetodoDiPagamentoDAO {

    private static MetodoDiPagamentoDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    public MetodoDiPagamentoDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return L'istanza singola di MetodoDiPagamentoDAO.
     */
    public static MetodoDiPagamentoDAO getInstance() {
        if (instance == null) {
            instance = new MetodoDiPagamentoDAO();
        }
        return instance;
    }

    /**
     * Crea la tabella MetodoDiPagamento nel database.
     *
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS MetodoDiPagamento (" +
                "nominativo TEXT NOT NULL, " +
                "numeroCarta TEXT PRIMARY KEY, " +
                "scadenza TEXT NOT NULL, " +
                "emailCliente TEXT, " +
                "FOREIGN KEY(emailCliente) REFERENCES Utente(email)" +
                ");";
        executeUpdate(createTableSQL);
    }

    /**
     * Aggiunge un metodo di pagamento al database.
     *
     * @param metodo Il metodo di pagamento da aggiungere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiungiMetodo(MetodoDiPagamento metodo) throws SQLException {
        String insertQuery = "INSERT INTO MetodoDiPagamento (nominativo, numeroCarta, scadenza, emailCliente) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            setMetodoParameters(ps, metodo);
            ps.executeUpdate();
        }
    }

    /**
     * Imposta i parametri per il PreparedStatement.
     *
     * @param ps Il PreparedStatement da configurare.
     * @param metodo Il metodo di pagamento con i dati da impostare.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private void setMetodoParameters(PreparedStatement ps, MetodoDiPagamento metodo) throws SQLException {
        ps.setString(1, metodo.getNominativo());
        ps.setString(2, metodo.getNumeroCarta());
        ps.setString(3, metodo.getScadenza());
        ps.setString(4, metodo.getEmailCliente());
    }

    /**
     * Ottiene un metodo di pagamento in base al numero della carta.
     *
     * @param numeroCarta Il numero della carta del metodo di pagamento da ottenere.
     * @return Il metodo di pagamento trovato, o null se non trovato.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public MetodoDiPagamento getMetodoByNumeroCarta(String numeroCarta) throws SQLException {
        String selectQuery = "SELECT * FROM MetodoDiPagamento WHERE numeroCarta = ?";
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, numeroCarta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMetodo(rs);
                }
            }
        }
        return null;
    }

    /**
     * Mappa un ResultSet a un oggetto MetodoDiPagamento.
     *
     * @param rs Il ResultSet da mappare.
     * @return Un oggetto MetodoDiPagamento.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private MetodoDiPagamento mapResultSetToMetodo(ResultSet rs) throws SQLException {
        String nominativo = rs.getString("nominativo");
        String numeroCarta = rs.getString("numeroCarta");
        String scadenza = rs.getString("scadenza");
        String emailCliente = rs.getString("emailCliente");
        return new MetodoDiPagamento(nominativo, numeroCarta, scadenza, emailCliente);
    }

    /**
     * Aggiorna i dati di un metodo di pagamento.
     *
     * @param metodo Il metodo di pagamento con i nuovi dati.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiornaMetodo(MetodoDiPagamento metodo) throws SQLException {
        String updateQuery = "UPDATE MetodoDiPagamento SET nominativo = ?, scadenza = ?, emailCliente = ? " +
                "WHERE numeroCarta = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, metodo.getNominativo());
            ps.setString(2, metodo.getScadenza());
            ps.setString(3, metodo.getEmailCliente());
            ps.setString(4, metodo.getNumeroCarta());
            ps.executeUpdate();
        }
    }

    /**
     * Rimuove un metodo di pagamento in base al numero della carta.
     *
     * @param numeroCarta Il numero della carta del metodo di pagamento da rimuovere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void rimuoviMetodo(String numeroCarta) throws SQLException {
        String deleteQuery = "DELETE FROM MetodoDiPagamento WHERE numeroCarta = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setString(1, numeroCarta);
            ps.executeUpdate();
        }
    }

    /**
     * Ottiene i metodi di pagamento associati a un utente.
     *
     * @param emailUtente L'email dell'utente di cui vogliamo ottenere i metodi di pagamento.
     * @return Una lista di metodi di pagamento.
     */
    public List<String> getMetodiPagamento(String emailUtente) {
        List<String> metodi = new ArrayList<>();
        String sql = "SELECT numeroCarta, nominativo FROM MetodoDiPagamento WHERE emailCliente = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailUtente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    metodi.add(rs.getString("numeroCarta") + " - " + rs.getString("nominativo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return metodi;
    }

    /**
     * Esegue un aggiornamento SQL.
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
