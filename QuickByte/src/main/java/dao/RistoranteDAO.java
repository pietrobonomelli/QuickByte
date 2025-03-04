package dao;

import database.DatabaseConnection;
import model.Ristorante;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class RistoranteDAO {

    private static RistoranteDAO instance;
    private Connection connection;

    private RistoranteDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static RistoranteDAO getInstance() {
        if (instance == null) {
            instance = new RistoranteDAO();
        }
        return instance;
    }

    public List<Ristorante> getRistorantiByEmail(String email) throws SQLException {
        String query = "SELECT * FROM Ristorante WHERE emailTitolare = ?";
        List<Ristorante> ristoranti = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ristoranti.add(new Ristorante(
                        rs.getInt("idRistorante"),
                        rs.getString("nome"),
                        rs.getString("telefono"),
                        rs.getString("indirizzo"),
                        rs.getString("emailTitolare")
                    ));
                }
            }
        }
        return ristoranti;
    }

    public List<Ristorante> getRistoranti() throws SQLException {
        String query = "SELECT * FROM Ristorante";
        List<Ristorante> ristoranti = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ristoranti.add(new Ristorante(
                    rs.getInt("idRistorante"),
                    rs.getString("nome"),
                    rs.getString("telefono"),
                    rs.getString("indirizzo"),
                    rs.getString("emailTitolare")
                ));
            }
        }
        return ristoranti;
    }

    public Ristorante getRistoranteByNome(String nome, String emailTitolare) throws SQLException {
        String query = "SELECT * FROM Ristorante WHERE nome = ? AND emailTitolare = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nome);
            ps.setString(2, emailTitolare);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ristorante(
                        rs.getInt("idRistorante"),
                        rs.getString("nome"),
                        rs.getString("telefono"),
                        rs.getString("indirizzo"),
                        rs.getString("emailTitolare")
                    );
                }
            }
        }
        return null;
    }

    public void rimuoviRistorante(int idRistorante) throws SQLException {
        String query = "DELETE FROM Ristorante WHERE idRistorante = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idRistorante);
            ps.executeUpdate();
        }
    }

    public void inserisciRistorante(String nome, String telefono, String indirizzo, String emailTitolare) throws SQLException {
    	String query = "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nome);
            ps.setString(2, telefono);
            ps.setString(3, indirizzo);
            ps.setString(4, emailTitolare);
            ps.executeUpdate();
        }
    }

    public boolean aggiornaRistorante(Ristorante ristorante, String nomeRistorante) throws SQLException {
        String query = "UPDATE Ristorante SET nome = ?, telefono = ?, indirizzo = ? WHERE nome = ? AND emailTitolare = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, ristorante.getNome());
            ps.setString(2, ristorante.getTelefono());
            ps.setString(3, ristorante.getIndirizzo());
            ps.setString(4, nomeRistorante);
            ps.setString(5, ristorante.getEmailTitolare());
            return ps.executeUpdate() > 0;
        }
    }
}

