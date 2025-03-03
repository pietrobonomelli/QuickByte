package dao;

import database.DatabaseConnection;
import java.sql.*;
import model.Ristorante; // Assicurati di avere la classe Ristorante nel package model
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
		if(instance == null) {
			instance = new RistoranteDAO();
		}
		return instance;
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
    
 // Metodo per ottenere tutti i ristoranti
    public List<Ristorante> getRistoranti() throws SQLException {
        String query = "SELECT * FROM Ristorante";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        
            ResultSet rs = stmt.executeQuery();
            
            List<Ristorante> ristoranti = new ArrayList<>();
            while (rs.next()) {
                Ristorante ristorante = new Ristorante();
                ristorante.setIdRistorante(rs.getInt("idRistorante"));
                ristorante.setNome(rs.getString("nome"));
                ristorante.setIndirizzo(rs.getString("indirizzo"));
                ristorante.setTelefono(rs.getString("telefono"));
                ristorante.setEmailTitolare(rs.getString("emailTitolare"));
                ristoranti.add(ristorante);
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
    
    public boolean aggiornaRistorante(Ristorante ristorante, String nomeRistorante) throws SQLException {
        String updateQuery = "UPDATE Ristorante SET nome = ?, telefono = ?, indirizzo = ? WHERE nome = ? AND emailTitolare = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, ristorante.getNome());
            ps.setString(2, ristorante.getTelefono());
            ps.setString(3, ristorante.getIndirizzo());
            ps.setString(4, nomeRistorante);
            ps.setString(5, ristorante.getEmailTitolare());

            return ps.executeUpdate() > 0; // Ritorna true se almeno una riga è stata aggiornata
        }
    }


}
