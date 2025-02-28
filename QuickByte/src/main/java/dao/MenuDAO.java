package dao;

import java.sql.*;
import model.Menu;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;

public class MenuDAO {

    private Connection connection;

    public MenuDAO() throws SQLException {
        this.connection = DatabaseConnection.connect();
    }

    // Metodo per creare la tabella Menu
    public void createTable() throws SQLException {
        String createMenuTable = "CREATE TABLE IF NOT EXISTS Menu (" +
                "nome TEXT NOT NULL, " +
                "idRistorante INTEGER NOT NULL, " +
                "PRIMARY KEY (nome, idRistorante), " +
                "FOREIGN KEY (idRistorante) REFERENCES Ristorante(idRistorante) ON DELETE CASCADE" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createMenuTable);
        }
    }

    // Metodo per aggiungere un menu
    public void aggiungiMenu(Menu menu) throws SQLException {
        String insertQuery = "INSERT INTO Menu (nome, idRistorante) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, menu.getNome());
            ps.setInt(2, menu.getIdRistorante());
            ps.executeUpdate();
        }
    }

    // Metodo per ottenere il nome del ristorante
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

    // Metodo per ottenere tutti i menu di un ristorante
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

    // Metodo per eliminare un menu
    public void rimuoviMenu(String nome, int idRistorante) throws SQLException {
        String deleteQuery = "DELETE FROM Menu WHERE nome = ? AND idRistorante = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setString(1, nome);
            ps.setInt(2, idRistorante);
            ps.executeUpdate();
        }
    }
}
