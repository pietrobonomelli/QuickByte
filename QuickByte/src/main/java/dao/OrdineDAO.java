package dao;

import database.DatabaseConnection;
import model.*;
import sessione.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdineDAO {
    private static OrdineDAO instance;
    private Connection connection;

    private OrdineDAO() {
        try {
            this.connection = DatabaseConnection.connect();
            // Abilita i vincoli di chiave esterna per questa connessione
            try (Statement stmt = this.connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static OrdineDAO getInstance() {
        if (instance == null) {
            instance = new OrdineDAO();
        }
        return instance;
    }

    /**
     * Recupera un ordine tramite il suo ID.
     *
     * @param idOrdine l'ID dell'ordine da recuperare.
     * @return l'ordine corrispondente all'ID specificato.
     */
    public Ordine getOrdineById(int idOrdine) {
        String query = "SELECT * FROM Ordine WHERE idOrdine = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creaOrdineDaResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recupera tutti gli ordini.
     *
     * @return una lista di tutti gli ordini.
     */
    public List<Ordine> getAllOrdini() {
        List<Ordine> ordini = new ArrayList<>();
        String query = "SELECT * FROM Ordine";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ordini.add(creaOrdineDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    /**
     * Aggiorna lo stato di un ordine.
     *
     * @param idOrdine l'ID dell'ordine da aggiornare.
     * @param nuovoStato il nuovo stato dell'ordine.
     * @return true se l'aggiornamento è riuscito, false altrimenti.
     */
    public boolean updateStatoOrdine(int idOrdine, String nuovoStato) {
        String query = "UPDATE Ordine SET stato = ? WHERE idOrdine = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nuovoStato);
            stmt.setInt(2, idOrdine);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un ordine tramite il suo ID.
     *
     * @param idOrdine l'ID dell'ordine da eliminare.
     * @return true se l'eliminazione è riuscita, false altrimenti.
     */
    public boolean deleteOrdine(int idOrdine) {
        String query = "DELETE FROM Ordine WHERE idOrdine = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idOrdine);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Ordine creaOrdineDaResultSet(ResultSet rs) throws SQLException {
        int idOrdine = rs.getInt("idOrdine");
        String statoString = rs.getString("stato");
        StatoOrdine statoOrdine = StatoOrdine.valueOf(statoString);
        double costo = rs.getDouble("costo");
        long timestamp = rs.getLong("dataOraOrdine");
        Timestamp dataOraOrdine = new Timestamp(timestamp);
        String indirizzo = rs.getString("indirizzo");
        String emailCliente = rs.getString("emailCliente");
        String emailCorriere = rs.getString("emailCorriere");
        int idRistorante = rs.getInt("idRistorante");

        // Formatta la data e l'ora
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(dataOraOrdine);
        System.out.println("Data e ora formattate: " + formattedDate);

        return new Ordine(idOrdine, statoOrdine, costo, dataOraOrdine, indirizzo, emailCliente, emailCorriere, idRistorante);
    }

    /**
     * Recupera gli ordini in base al loro stato.
     *
     * @param stato lo stato degli ordini da recuperare.
     * @return una lista di ordini con lo stato specificato.
     */
    public List<Ordine> getOrdiniByStato(String stato) {
        List<Ordine> ordini = new ArrayList<>();
        String query = "SELECT * FROM Ordine WHERE stato = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, stato);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordini.add(creaOrdineDaResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    /**
     * Recupera gli ordini di un cliente tramite la sua email.
     *
     * @param emailCliente l'email del cliente.
     * @return una lista di ordini del cliente.
     */
    public List<Ordine> getOrdiniByEmailCliente(String emailCliente) {
        List<Ordine> ordini = new ArrayList<>();
        String query = "SELECT * FROM Ordine WHERE emailCliente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, emailCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordini.add(creaOrdineDaResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    /**
     * Recupera gli ordini presi in carico da un corriere.
     *
     * @param emailUtente l'email del corriere.
     * @param stati una lista di stati degli ordini da recuperare.
     * @return una lista di ordini presi in carico dal corriere.
     */
    public List<Ordine> getOrdiniPresiInCarico(String emailUtente, List<String> stati) {
        List<Ordine> ordini = new ArrayList<>();
        if (stati == null || stati.isEmpty()) {
            return ordini;
        }

        String placeholders = String.join(",", Collections.nCopies(stati.size(), "?"));
        String query = "SELECT * FROM Ordine WHERE stato IN (" + placeholders + ") AND emailCorriere = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < stati.size(); i++) {
                stmt.setString(i + 1, stati.get(i));
            }
            stmt.setString(stati.size() + 1, emailUtente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordini.add(creaOrdineDaResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    /**
     * Recupera gli ordini di un ristorante tramite il suo ID.
     *
     * @param idRistorante l'ID del ristorante.
     * @return una lista di ordini del ristorante.
     */
    public List<Ordine> getOrdiniByIdRistorante(int idRistorante) {
        List<Ordine> ordini = new ArrayList<>();
        String query = "SELECT * FROM Ordine WHERE idRistorante = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idRistorante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordini.add(creaOrdineDaResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    /**
     * Aggiorna lo stato di un ordine.
     *
     * @param idOrdine l'ID dell'ordine da aggiornare.
     * @param nuovoStato il nuovo stato dell'ordine.
     * @return true se l'aggiornamento è riuscito, false altrimenti.
     */
    public boolean aggiornaStatoOrdine(int idOrdine, String nuovoStato) {
        String query = "UPDATE Ordine SET stato = ? WHERE idOrdine = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nuovoStato);
            stmt.setInt(2, idOrdine);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Aggiorna l'email del corriere di un ordine.
     *
     * @param idOrdine l'ID dell'ordine da aggiornare.
     * @param email la nuova email del corriere.
     * @return true se l'aggiornamento è riuscito, false altrimenti.
     */
    public boolean aggiornaEmailCorriereOrdine(int idOrdine, String email) {
        String query = "UPDATE Ordine SET emailCorriere = ? WHERE idOrdine = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setInt(2, idOrdine);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Registra un nuovo ordine.
     *
     * @param emailUtente l'email dell'utente che effettua l'ordine.
     * @param indirizzo l'indirizzo di consegna dell'ordine.
     * @return true se la registrazione è riuscita, false altrimenti.
     */
    public boolean registraOrdine(String emailUtente, String indirizzo) {
        String insertOrdineSQL = "INSERT INTO Ordine (emailCliente, dataOraOrdine, stato, indirizzo, costo, idRistorante) VALUES (?, ?, ?, ?, ?, ?)";
        String insertDettagliSQL = "INSERT INTO DettaglioOrdine (idOrdine, idPiatto, quantita) SELECT ?, idPiatto, quantitaPiatti FROM Carrello WHERE emailUtente = ?";
        String deleteCarrelloSQL = "DELETE FROM Carrello WHERE emailUtente = ?";

        try {
            double costoTotale = CarrelloDAO.getInstance().calcolaCostoTotale(emailUtente);
            int idOrdine = inserisciOrdine(insertOrdineSQL, emailUtente, indirizzo, costoTotale);
            if (idOrdine != -1) {
                inserisciDettagliOrdine(insertDettagliSQL, idOrdine, emailUtente);
                svuotaCarrello(deleteCarrelloSQL, emailUtente);
                SessioneCarrello.setPieno(false);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int inserisciOrdine(String query, String emailUtente, String indirizzo, double costoTotale) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, emailUtente);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, StatoOrdine.PENDENTE.name());
            stmt.setString(4, indirizzo);
            stmt.setDouble(5, costoTotale);
            stmt.setInt(6, SessioneRistorante.getId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    private void inserisciDettagliOrdine(String query, int idOrdine, String emailUtente) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idOrdine);
            stmt.setString(2, emailUtente);
            stmt.executeUpdate();
        }
    }

    private void svuotaCarrello(String query, String emailUtente) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, emailUtente);
            stmt.executeUpdate();
        }
    }

    /**
     * Recupera il nome del ristorante associato a un ordine.
     *
     * @param ordine l'ordine per cui recuperare il nome del ristorante.
     * @return il nome del ristorante.
     */
    public String getNomeRistorante(Ordine ordine) {
        String nomeRistorante = null;
        String query = "SELECT nome FROM Ristorante WHERE idRistorante = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ordine.getIdRistorante());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nomeRistorante = rs.getString("nome");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomeRistorante;
    }
}
