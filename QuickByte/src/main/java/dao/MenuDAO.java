package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Menu;
import database.DatabaseConnection;

public class MenuDAO {

    private static MenuDAO instance;
    private Connection connection;

    private MenuDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return L'istanza singola di MenuDAO.
     */
    public static MenuDAO getInstance() {
        if (instance == null) {
            instance = new MenuDAO();
        }
        return instance;
    }

    /**
     * Crea la tabella Menu nel database.
     * 
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void createTable() throws SQLException {
        String createMenuTable = "CREATE TABLE IF NOT EXISTS Menu (" +
                "nome TEXT NOT NULL, " +
                "idRistorante INTEGER NOT NULL, " +
                "PRIMARY KEY (nome, idRistorante), " +
                "FOREIGN KEY (idRistorante) REFERENCES Ristorante(idRistorante) ON DELETE CASCADE" +
                ");";
        executeUpdate(createMenuTable);
    }

    /**
     * Aggiunge un menu al database.
     *
     * @param menu Il menu da aggiungere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void aggiungiMenu(Menu menu) throws SQLException {
        String insertQuery = "INSERT INTO Menu (nome, idRistorante) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, menu.getNome());
            ps.setInt(2, menu.getIdRistorante());
            ps.executeUpdate();
        }
    }

    /**
     * Ottiene il nome del ristorante dato il suo ID.
     *
     * @param idRistorante L'ID del ristorante.
     * @return Il nome del ristorante.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public String getNomeRistorante(int idRistorante) throws SQLException {
        String nomeRistorante = "";
        String query = "SELECT nome FROM Ristorante WHERE idRistorante = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nomeRistorante = rs.getString("nome");
                }
            }
        }
        return nomeRistorante;
    }

    /**
     * Ottiene tutti i menu di un ristorante.
     *
     * @param idRistorante L'ID del ristorante di cui ottenere i menu.
     * @return Una lista di menu.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public List<Menu> getMenuByRistorante(int idRistorante) throws SQLException {
        String selectQuery = "SELECT * FROM Menu WHERE idRistorante = ?";
        List<Menu> menuList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    menuList.add(new Menu(nome, idRistorante));
                }
            }
        }
        return menuList;
    }

    /**
     * Rimuove un menu dal database.
     *
     * @param nome Il nome del menu da rimuovere.
     * @param idRistorante L'ID del ristorante associato al menu.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void rimuoviMenu(String nome, int idRistorante) throws SQLException {
        String deleteQuery = "DELETE FROM Menu WHERE nome = ? AND idRistorante = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setString(1, nome);
            ps.setInt(2, idRistorante);
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
