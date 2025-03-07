package daoTest;

import static org.junit.Assert.*;
import org.junit.*;

import dao.CarrelloDAO;
import dao.OrdineDAO;
import database.DatabaseConnection;
import model.Ordine;
import model.StatoOrdine;
import sessione.SessioneRistorante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAOTest {

    private OrdineDAO ordineDAO;
    private static final String TEST_EMAIL_CLIENTE = "cliente@test.com";
    private static final String TEST_EMAIL_CORRIERE = "corriere@test.com";
    private static final String TEST_NOME_MENU = "menu1";
    
    private int TEST_ID_RISTORANTE;
    private int TEST_ID_PIATTO;
    private int TEST_ID_ORDINE;

    @BeforeClass
    public static void initDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        }
    }

    
 
    @Before
    public void setUp() throws SQLException {
        ordineDAO = OrdineDAO.getInstance();
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            conn.setAutoCommit(false);
            
            // Inserisci gli utenti
            insertUser(conn, TEST_EMAIL_CLIENTE, "Cliente Test", "Cliente");
            insertUser(conn, TEST_EMAIL_CORRIERE, "Corriere Test", "Corriere");
            
            // Commit della transazione per rendere effettive le modifiche
            conn.commit(); // Aggiungi questa linea per commettere le modifiche
            
            // Verifica che gli utenti siano stati inseriti
            if (checkIfUserExists(conn, TEST_EMAIL_CLIENTE)) {
                System.out.println("Cliente Test inserito correttamente.");
            } else {
                System.out.println("Errore nell'inserimento del Cliente Test.");
            }

            if (checkIfUserExists(conn, TEST_EMAIL_CORRIERE)) {
                System.out.println("Corriere Test inserito correttamente.");
            } else {
                System.out.println("Errore nell'inserimento del Corriere Test.");
            }

            TEST_ID_RISTORANTE = insertRistorante(conn, "Ristorante Test", TEST_EMAIL_CLIENTE);
            insertMenu(conn, TEST_NOME_MENU, TEST_ID_RISTORANTE); // Metodo aggiunto
            TEST_ID_PIATTO = insertPiatto(conn, TEST_NOME_MENU, TEST_ID_RISTORANTE);
            TEST_ID_ORDINE = insertOrdine(conn, TEST_EMAIL_CLIENTE, TEST_EMAIL_CORRIERE, TEST_ID_RISTORANTE);
            
            conn.commit();
        }
    }




    private boolean checkIfUserExists(Connection conn, String email) throws SQLException {
        String checkSql = "SELECT 1 FROM Utente WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();  // Restituisce true se l'utente esiste
            }
         
        }
    }
    @After
    public void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            conn.setAutoCommit(false);
            cleanTestData(conn);
            conn.commit();
        }
    }
    private void insertMenu(Connection conn, String nomeMenu, int idRistorante) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM Menu WHERE nome = ? AND idRistorante = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, nomeMenu);
            checkStmt.setInt(2, idRistorante);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Il menu esiste già: " + nomeMenu);
                    return;
                }
            }
        }

        String insertSql = "INSERT INTO Menu (nome, idRistorante) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, nomeMenu);
            ps.setInt(2, idRistorante);
            ps.executeUpdate();
        }
    }


    private void cleanTestData(Connection conn) throws SQLException {
    	 conn.createStatement().execute("PRAGMA foreign_keys = OFF;");

    	    try {
    	        // Elimina dalla tabella DettaglioOrdine
    	        deleteFromTable(conn, "DELETE FROM DettaglioOrdine WHERE idOrdine IN (SELECT idOrdine FROM Ordine WHERE idRistorante = ?)", TEST_ID_RISTORANTE);
    	        // Elimina dalla tabella Ordine
    	        deleteFromTable(conn, "DELETE FROM Ordine WHERE idRistorante = ?", TEST_ID_RISTORANTE);
    	        // Elimina dalla tabella Carrello
    	        deleteFromTable(conn, "DELETE FROM Carrello WHERE emailUtente = ?", TEST_EMAIL_CLIENTE);
    	        // Elimina dalla tabella Piatto
    	        deleteFromTable(conn, "DELETE FROM Piatto WHERE idPiatto = ?", TEST_ID_PIATTO);
    	        // Elimina dalla tabella Menu
    	        deleteFromTable(conn, "DELETE FROM Menu WHERE idRistorante = ?", TEST_ID_RISTORANTE);
    	        // Elimina dalla tabella Ristorante
    	        deleteFromTable(conn, "DELETE FROM Ristorante WHERE idRistorante = ?", TEST_ID_RISTORANTE);
    	        // Elimina dalla tabella Utente
    	        deleteFromTable(conn, "DELETE FROM Utente WHERE email IN (?, ?)", TEST_EMAIL_CLIENTE, TEST_EMAIL_CORRIERE);
    	    } finally {
    	        // Riabilita i vincoli di chiave esterna
    	        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
    	    }
    }



    private void deleteFromTable(Connection conn, String query, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) {
                    ps.setInt(i + 1, (Integer) params[i]);
                } else {
                    ps.setString(i + 1, params[i].toString());
                }
            }
            ps.executeUpdate();
        }
    }



    private void insertUser(Connection conn, String email, String nome, String tipoUtente) throws SQLException {
        // Verifica che l'utente non esista già
        String deleteSql = "DELETE FROM Utente WHERE email = ?";
        try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
            psDelete.setString(1, email);
            psDelete.executeUpdate(); // Rimuovi eventuali utenti esistenti con la stessa email
        }
     
        // Inserimento dell'utente
        String insertSql = "INSERT INTO Utente (email, password, nome, telefono, tipoUtente) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
            psInsert.setString(1, email);
            psInsert.setString(2, "password");  // Assicurati di avere un valore valido per la password
            psInsert.setString(3, nome);
            psInsert.setString(4, "1234567890");  // Telefono fittizio per il test
            psInsert.setString(5, tipoUtente);
            
            int affectedRows = psInsert.executeUpdate();
            // Se non è stato inserito alcun record, l'operazione è fallita
            if (affectedRows == 0) {
            	
                throw new SQLException("Inserting user failed, no rows affected.");
            }
        }
        
    }


    private int insertRistorante(Connection conn, String nome, String emailTitolare) throws SQLException {
        String sql = "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setString(2, "0987654321");
            ps.setString(3, "Indirizzo Test");
            ps.setString(4, emailTitolare);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get Ristorante ID");
                }
            }
        }
    }

    private int insertPiatto(Connection conn, String nomeMenu, int idRistorante) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM Menu WHERE nome = ? AND idRistorante = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, nomeMenu);
            checkStmt.setInt(2, idRistorante);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next() || rs.getInt(1) == 0) {
                    throw new SQLException("Errore: Menu non trovato per il piatto!");
                }
            }
        }

        String sql = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Piatto Test");
            ps.setInt(2, 1);
            ps.setString(3, "10.99");
            ps.setString(4, "None");
            ps.setString(5, "foto.jpg");
            ps.setString(6, nomeMenu);
            ps.setInt(7, idRistorante);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get Piatto ID");
                }
            }
        }
    }


    private int insertOrdine(Connection conn, String emailCliente, String emailCorriere, int idRistorante) throws SQLException {
        String sql = "INSERT INTO Ordine (stato, costo, dataOraOrdine, indirizzo, emailCliente, emailCorriere, idRistorante) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, StatoOrdine.PENDENTE.name());
            ps.setDouble(2, 25.99);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, "Indirizzo Test");
            ps.setString(5, emailCliente);
            ps.setString(6, emailCorriere);
            ps.setInt(7, idRistorante);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get Ordine ID");
                }
            }
        }
    }

    @Test
    public void testGetOrdineById() {
        Ordine ordine = ordineDAO.getOrdineById(TEST_ID_ORDINE);
        assertNotNull(ordine);
        assertEquals(TEST_EMAIL_CLIENTE, ordine.getEmailCliente());
        assertEquals(StatoOrdine.PENDENTE.name(), ordine.getStato());
    }

    @Test
    public void testUpdateStatoOrdine() {
        boolean success = ordineDAO.updateStatoOrdine(TEST_ID_ORDINE, StatoOrdine.CONSEGNATO.name());
        assertTrue(success);

        Ordine updatedOrdine = ordineDAO.getOrdineById(TEST_ID_ORDINE);
        assertEquals(StatoOrdine.CONSEGNATO.name(), updatedOrdine.getStato());
    }

    @Test
    public void testDeleteOrdine() {
        boolean deleted = ordineDAO.deleteOrdine(TEST_ID_ORDINE);
        assertTrue(deleted);

        Ordine ordine = ordineDAO.getOrdineById(TEST_ID_ORDINE);
        assertNull(ordine);
    }

    @Test
    public void testRegistraOrdine() throws SQLException {
        SessioneRistorante.setId(TEST_ID_RISTORANTE);

        // Inserisci nel carrello con commit esplicito
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Carrello (emailUtente, idPiatto, quantitaPiatti) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, TEST_EMAIL_CLIENTE);
                ps.setInt(2, TEST_ID_PIATTO);
                ps.setInt(3, 2);
                ps.executeUpdate();
            }
            conn.commit(); // Commit manuale
        }

        // Esegui registraOrdine
        boolean success = ordineDAO.registraOrdine(TEST_EMAIL_CLIENTE, "Indirizzo Test");
        assertTrue("Registrazione fallita", success);

        // Verifica i dettagli
        List<Ordine> ordini = ordineDAO.getOrdiniByEmailCliente(TEST_EMAIL_CLIENTE);
        assertFalse("Nessun ordine trovato", ordini.isEmpty());

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM DettaglioOrdine WHERE idOrdine = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, ordini.get(0).getIdOrdine());
                ResultSet rs = ps.executeQuery();
                assertTrue("Dettaglio ordine mancante", rs.next());
            }
        }
    }

    @Test
    public void testGetOrdiniByStato() {
        List<Ordine> ordini = ordineDAO.getOrdiniByStato(StatoOrdine.PENDENTE.name());
        assertFalse(ordini.isEmpty());
        assertEquals(StatoOrdine.PENDENTE.name(), ordini.get(0).getStato());
    }

    @Test
    public void testGetOrdiniByEmailCliente() {
        List<Ordine> ordini = ordineDAO.getOrdiniByEmailCliente(TEST_EMAIL_CLIENTE);
        assertFalse(ordini.isEmpty());
        assertEquals(TEST_EMAIL_CLIENTE, ordini.get(0).getEmailCliente());
    }

    @Test
    public void testAggiornaEmailCorriereOrdine() throws SQLException {
        String nuovoCorriereEmail = "nuovo_corriere@test.com";
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false); // Disabilita autocommit

            try {
                // Inserisci nuovo corriere
                insertUser(conn, nuovoCorriereEmail, "Nuovo Corriere", "Corriere");
                conn.commit(); // Commit esplicito per garantire la persistenza

                // Verifica che l'utente sia stato inserito
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Utente WHERE email = ?")) {
                    ps.setString(1, nuovoCorriereEmail);
                    try (ResultSet rs = ps.executeQuery()) {
                        assertTrue("Il corriere non è stato inserito", rs.next());
                    }
                }

                // Verifica che l'ordine esista
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Ordine WHERE idOrdine = ?")) {
                    ps.setInt(1, TEST_ID_ORDINE);
                    try (ResultSet rs = ps.executeQuery()) {
                        assertTrue("L'ordine non esiste", rs.next());
                    }
                }

                // Aggiorna email corriere nell'ordine
                boolean success = ordineDAO.aggiornaEmailCorriereOrdine(TEST_ID_ORDINE, nuovoCorriereEmail);
                assertTrue("Update failed", success);

                // Verifica aggiornamento
                Ordine updatedOrdine = ordineDAO.getOrdineById(TEST_ID_ORDINE);
                assertEquals(nuovoCorriereEmail, updatedOrdine.getEmailCorriere());

            } finally {
                // Pulizia: elimina il nuovo corriere
                String deleteSQL = "DELETE FROM Utente WHERE email = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
                    ps.setString(1, nuovoCorriereEmail);
                    ps.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            }
        }
    }


    @Test
    public void testGetOrdiniByIdRistorante() {
        List<Ordine> ordini = ordineDAO.getOrdiniByIdRistorante(TEST_ID_RISTORANTE);
        assertFalse(ordini.isEmpty());
        assertEquals(TEST_ID_RISTORANTE, ordini.get(0).getIdRistorante());
    }

    @Test
    public void testGetOrdiniPresiInCarico() {
        List<String> stati = new ArrayList<>();
        stati.add(StatoOrdine.IN_CONSEGNA.name());
        stati.add(StatoOrdine.CONSEGNATO.name());

        // Insert test order with specific status and corriere
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "INSERT INTO Ordine (stato, costo, dataOraOrdine, indirizzo, emailCliente, emailCorriere, idRistorante) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, StatoOrdine.IN_CONSEGNA.name());
                ps.setDouble(2, 30.0);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setString(4, "Indirizzo Test");
                ps.setString(5, TEST_EMAIL_CLIENTE);
                ps.setString(6, TEST_EMAIL_CORRIERE);
                ps.setInt(7, TEST_ID_RISTORANTE);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Ordine> ordini = ordineDAO.getOrdiniPresiInCarico(TEST_EMAIL_CORRIERE, stati);
        assertFalse(ordini.isEmpty());
        assertEquals(TEST_EMAIL_CORRIERE, ordini.get(0).getEmailCorriere());
        assertTrue(stati.contains(ordini.get(0).getStato()));
    }
}