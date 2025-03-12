package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.DettaglioOrdine;
import database.DatabaseConnection;

public class DettaglioOrdineDAO {
    private static DettaglioOrdineDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern SINGLETON.
     */
    private DettaglioOrdineDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return L'istanza singola di DettaglioOrdineDAO.
     */
    public static DettaglioOrdineDAO getInstance() {
        if (instance == null) {
            instance = new DettaglioOrdineDAO();
        }
        return instance;
    }

    /**
     * Crea la tabella DettaglioOrdine nel database.
     *
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS DettaglioOrdine (" +
                "idOrdine INTEGER, " +
                "idPiatto INTEGER, " +
                "quantita INTEGER NOT NULL, " +
                "PRIMARY KEY (idOrdine, idPiatto), " +
                "FOREIGN KEY(idOrdine) REFERENCES Ordine(idOrdine), " +
                "FOREIGN KEY(idPiatto) REFERENCES Piatto(idPiatto)" +
                ");";
        executeUpdate(createTableSQL);
    }

    /**
     * Aggiunge un dettaglio ordine al database.
     *
     * @param dettaglio Il dettaglio ordine da aggiungere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiungiDettaglioOrdine(DettaglioOrdine dettaglio) throws SQLException {
        String insertSQL = "INSERT INTO DettaglioOrdine (idOrdine, idPiatto, quantita) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSQL)) {
            setPreparedStatementParameters(ps, dettaglio);
            ps.executeUpdate();
        }
    }

    /**
     * Imposta i parametri per il PreparedStatement.
     *
     * @param ps Il PreparedStatement da configurare.
     * @param dettaglio Il dettaglio ordine con i dati da impostare.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private void setPreparedStatementParameters(PreparedStatement ps, DettaglioOrdine dettaglio) throws SQLException {
        ps.setInt(1, dettaglio.getIdOrdine());
        ps.setInt(2, dettaglio.getIdPiatto());
        ps.setInt(3, dettaglio.getQuantita());
    }

    /**
     * Ottiene i dettagli di un ordine specifico.
     *
     * @param idOrdine L'ID dell'ordine di cui ottenere i dettagli.
     * @return Una lista di dettagli ordine.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public List<DettaglioOrdine> getDettagliByOrdine(int idOrdine) throws SQLException {
        String selectSQL = "SELECT * FROM DettaglioOrdine WHERE idOrdine = ?";
        List<DettaglioOrdine> dettagli = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectSQL)) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dettagli.add(mapResultSetToDettaglioOrdine(rs));
                }
            }
        }
        return dettagli;
    }

    /**
     * Mappa un ResultSet a un oggetto DettaglioOrdine.
     *
     * @param rs Il ResultSet da mappare.
     * @return Un oggetto DettaglioOrdine.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private DettaglioOrdine mapResultSetToDettaglioOrdine(ResultSet rs) throws SQLException {
        int idOrdine = rs.getInt("idOrdine");
        int idPiatto = rs.getInt("idPiatto");
        int quantita = rs.getInt("quantita");
        return new DettaglioOrdine(idOrdine, idPiatto, quantita);
    }

    /**
     * Aggiorna la quantità di un piatto in un ordine.
     *
     * @param idOrdine L'ID dell'ordine.
     * @param idPiatto L'ID del piatto.
     * @param nuovaQuantita La nuova quantità.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiornaQuantita(int idOrdine, int idPiatto, int nuovaQuantita) throws SQLException {
        String updateSQL = "UPDATE DettaglioOrdine SET quantita = ? WHERE idOrdine = ? AND idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateSQL)) {
            ps.setInt(1, nuovaQuantita);
            ps.setInt(2, idOrdine);
            ps.setInt(3, idPiatto);
            ps.executeUpdate();
        }
    }

    /**
     * Rimuove un dettaglio ordine dal database.
     *
     * @param idOrdine L'ID dell'ordine.
     * @param idPiatto L'ID del piatto.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void rimuoviDettaglio(int idOrdine, int idPiatto) throws SQLException {
        String deleteSQL = "DELETE FROM DettaglioOrdine WHERE idOrdine = ? AND idPiatto = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteSQL)) {
            ps.setInt(1, idOrdine);
            ps.setInt(2, idPiatto);
            ps.executeUpdate();
        }
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
