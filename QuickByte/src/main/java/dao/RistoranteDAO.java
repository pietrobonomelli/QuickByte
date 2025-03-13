package dao;

import database.DatabaseConnection;
import model.Ristorante;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe DAO per la gestione dei ristoranti nel database.
 */
public class RistoranteDAO {

    private static RistoranteDAO instance;
    private Connection connection;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private RistoranteDAO() {
        try {
            this.connection = DatabaseConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return L'istanza singleton di RistoranteDAO.
     */
    public static RistoranteDAO getInstance() {
        if (instance == null) {
            instance = new RistoranteDAO();
        }
        return instance;
    }

    /**
     * Recupera i ristoranti associati a un'email del titolare.
     *
     * @param email L'email del titolare.
     * @return Una lista di ristoranti associati all'email.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public List<Ristorante> getRistorantiByEmail(String email) throws SQLException {
        String query = "SELECT * FROM Ristorante WHERE emailTitolare = ?";
        return executeQuery(query, email);
    }

    /**
     * Recupera tutti i ristoranti nel database.
     *
     * @return Una lista di tutti i ristoranti.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public List<Ristorante> getRistoranti() throws SQLException {
        String query = "SELECT * FROM Ristorante";
        return executeQuery(query);
    }

    /**
     * Esegue una query e restituisce una lista di ristoranti.
     *
     * @param query La query SQL da eseguire.
     * @param params I parametri per la query.
     * @return Una lista di ristoranti risultanti dalla query.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private List<Ristorante> executeQuery(String query, String... params) throws SQLException {
        List<Ristorante> ristoranti = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ristoranti.add(mapResultSetToRistorante(rs));
                }
            }
        }
        return ristoranti;
    }

    /**
     * Mappa un ResultSet a un oggetto Ristorante.
     *
     * @param rs Il ResultSet da mappare.
     * @return Un oggetto Ristorante.
     * @throws SQLException Se si verifica un errore SQL.
     */
    private Ristorante mapResultSetToRistorante(ResultSet rs) throws SQLException {
        return new Ristorante(
            rs.getInt("idRistorante"),
            rs.getString("nome"),
            rs.getString("telefono"),
            rs.getString("indirizzo"),
            rs.getString("emailTitolare")
        );
    }

    /**
     * Recupera un ristorante per nome e email del titolare.
     *
     * @param nome L'email del titolare.
     * @param emailTitolare L'email del titolare.
     * @return Il ristorante corrispondente o null se non trovato.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public Ristorante getRistoranteByNome(String nome, String emailTitolare) throws SQLException {
        String query = "SELECT * FROM Ristorante WHERE nome = ? AND emailTitolare = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nome);
            ps.setString(2, emailTitolare);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRistorante(rs);
                }
            }
        }
        return null;
    }

    /**
     * Rimuove un ristorante dal database.
     *
     * @param idRistorante L'ID del ristorante da rimuovere.
     * @throws SQLException Se si verifica un errore SQL.
     */
    public void rimuoviRistorante(int idRistorante) throws SQLException {
        String query = "DELETE FROM Ristorante WHERE idRistorante = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idRistorante);
            ps.executeUpdate();
        }
    }

    /**
     * Inserisce un nuovo ristorante nel database.
     *
     * @param nome Il nome del ristorante.
     * @param telefono Il telefono del ristorante.
     * @param indirizzo L'indirizzo del ristorante.
     * @param emailTitolare L'email del titolare del ristorante.
     * @throws SQLException Se si verifica un errore SQL.
     */
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

    /**
     * Aggiorna le informazioni di un ristorante nel database.
     *
     * @param ristorante Il ristorante con le nuove informazioni.
     * @param nomeRistorante Il nome originale del ristorante.
     * @return true se l'aggiornamento è avvenuto con successo, false altrimenti.
     * @throws SQLException Se si verifica un errore SQL.
     */
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
