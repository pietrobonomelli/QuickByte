package dao;

import java.sql.*;
import database.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import sessione.SessioneUtente;

public class TitolareDAO {

    private static TitolareDAO instance;
    private Connection connection;

    private TitolareDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            System.err.println("Errore nella connessione al database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static TitolareDAO getInstance() {
        if (instance == null) {
            instance = new TitolareDAO();
        }
        return instance;
    }

    public List<Integer> getRistorantiByEmail() throws SQLException {
        List<Integer> ristoranti = new ArrayList<>();
        String titolare = SessioneUtente.getEmail();

        String sql = "SELECT idRistorante FROM Ristorante WHERE emailTitolare = ?"; 

        if (connection == null || connection.isClosed()) {
            try {
                connection = DatabaseConnection.connect();
            } catch (SQLException e) {
                System.err.println("Errore durante la riconnessione al database: " + e.getMessage());
                e.printStackTrace();
                return ristoranti;
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, titolare);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int ristoranteId = rs.getInt("idRistorante");
                    ristoranti.add(ristoranteId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'esecuzione della query: " + e.getMessage());
            e.printStackTrace();
        }

        return ristoranti;
    }
}
