package dao;

import database.DatabaseManager;
import java.sql.*;
import model.Ristorante; // Assicurati di avere la classe Ristorante nel package model
import java.util.List;
import java.util.ArrayList;

public class RistoranteDAO {

    private Connection connection;

    public RistoranteDAO() throws SQLException {
        this.connection = DatabaseManager.connect();
    }

    // Metodo per ottenere ristoranti per email
    public List<Ristorante> getRistorantiByEmail(String email) throws SQLException {
        String query = "SELECT * FROM Ristorante WHERE emailTitolare = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            
            System.out.println("query: " + query); // Debug
            
            ResultSet rs = stmt.executeQuery();

        	String nomeRistorante = rs.getString("nome"); // Assicurati che "nome" sia una colonna esistente
            System.out.println("Nome ristorante: " + nomeRistorante);
            
            List<Ristorante> ristoranti = new ArrayList<>();
            while (rs.next()) {
                Ristorante ristorante = new Ristorante();
                ristorante.setIdRistorante(rs.getInt("idRistorante"));
                ristorante.setNome(rs.getString("nome"));
                ristoranti.add(ristorante);
            }
            
            // Debug: Stampa i dati ottenuti
            System.out.println("Numero di ristoranti trovati: " + ristoranti.size());
            for (Ristorante ristorante : ristoranti) {
                System.out.println("Ristorante: " + ristorante.getNome());
            }
            
            return ristoranti;
        }
    }


    // Metodo per ottenere un ristorante per nome e email
    public Ristorante getRistoranteByNome(String nome, String emailTitolare) throws SQLException {
        String selectQuery = "SELECT * FROM Ristorante WHERE nome = ? AND emailTitolare = ?";
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
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

    // Metodo per rimuovere un ristorante
    public void rimuoviRistorante(int idRistorante) throws SQLException {
        String deleteQuery = "DELETE FROM Ristorante WHERE idRistorante = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, idRistorante);
            ps.executeUpdate();
        }
    }

    // Metodo per inserire un nuovo ristorante
    public void inserisciRistorante(String nome, String telefono, String indirizzo, String emailTitolare) throws SQLException {
        String insertQuery = "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, nome);
            ps.setString(2, telefono);
            ps.setString(3, indirizzo);
            ps.setString(4, emailTitolare);
            ps.executeUpdate();
        }
    }
}
