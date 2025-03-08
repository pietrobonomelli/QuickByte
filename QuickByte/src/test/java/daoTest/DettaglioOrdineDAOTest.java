package daoTest;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;
import org.junit.*;
import dao.DettaglioOrdineDAO;
import dao.PiattoDAO;
import dao.OrdineDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.*;


public class DettaglioOrdineDAOTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";
    
    // Dati di test
    private final Cliente testCliente = new Cliente("test_cliente@test.com", "pass123", "Test Cliente", "123456");
    private final Titolare testTitolare = new Titolare("test_titolare@test.com", "pass456", "Test Titolare", "789012");
    private final Ristorante testRistorante = new Ristorante(1, "Test Ristorante", "123456", "Via Test", testTitolare.getEmail());
    private final Piatto testPiatto = new Piatto(1, "Test Piatto", true, "10.00", "Nessuno", "foto.jpg", "MenuTest", 1);
    private final Ordine testOrdine = new Ordine(1, StatoOrdine.PENDENTE.name(), 20.00, 
            "2023-01-01 10:00:00", "Via Test 123", testCliente.getEmail(), null, 1);
    private final DettaglioOrdine testDettaglio = new DettaglioOrdine(1, 1, 2);

    @BeforeClass
    public static void initDatabase() throws Exception {
        DatabaseConnection.setDatabaseUrl(TEST_DB_URL);
        new DatabaseManager();
        DatabaseManager.createTables();
    }

    @Before
    public void setupDatabase() throws SQLException {
        clearAllData();
        insertTestData();
    }

    @After
    public void tearDown() {
        clearAllData();
    }

    private void insertTestData() throws SQLException {
        // Inserisce utenti
        UtenteDAO.getInstance().insertUtente(testCliente, "Cliente");
        UtenteDAO.getInstance().insertUtente(testTitolare, "Titolare");
        
        // Inserisce ristorante
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Ristorante (idRistorante, nome, telefono, indirizzo, emailTitolare) " +
                "VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, testRistorante.getIdRistorante());
            stmt.setString(2, testRistorante.getNome());
            stmt.setString(3, testRistorante.getTelefono());
            stmt.setString(4, testRistorante.getIndirizzo());
            stmt.setString(5, testRistorante.getEmailTitolare());
            stmt.executeUpdate();
        }
        
        // Inserisce menu e piatto
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().executeUpdate(
                "INSERT INTO Menu (nome, idRistorante) VALUES ('MenuTest', 1)");
            conn.createStatement().executeUpdate(
                "INSERT INTO Piatto (idPiatto, nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) " +
                "VALUES (1, 'Test Piatto', 1, '10.00', 'Nessuno', 'foto.jpg', 'MenuTest', 1)");
        }
        
        // Inserisce ordine
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Ordine (idOrdine, stato, costo, dataOraOrdine, indirizzo, emailCliente, idRistorante) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, testOrdine.getIdOrdine());
            stmt.setString(2, testOrdine.getStato());
            stmt.setDouble(3, testOrdine.getCosto());
            stmt.setString(4, testOrdine.getDataOraOrdine());
            stmt.setString(5, testOrdine.getIndirizzo());
            stmt.setString(6, testOrdine.getEmailCliente());
            stmt.setInt(7, testOrdine.getIdRistorante());
            stmt.executeUpdate();
        }
        
        // Inserisce dettaglio ordine di test
        DettaglioOrdineDAO.getInstance().aggiungiDettaglioOrdine(testDettaglio);
    }

    private void clearAllData() {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = OFF");
            conn.createStatement().executeUpdate("DELETE FROM DettaglioOrdine");
            conn.createStatement().executeUpdate("DELETE FROM Ordine");
            conn.createStatement().executeUpdate("DELETE FROM Piatto");
            conn.createStatement().executeUpdate("DELETE FROM Menu");
            conn.createStatement().executeUpdate("DELETE FROM Ristorante");
            UtenteDAO.getInstance().deleteUtente(testCliente.getEmail());
            UtenteDAO.getInstance().deleteUtente(testTitolare.getEmail());
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   /* @Test
    public void testAggiungiDettaglioOrdine_Success() throws SQLException {
        DettaglioOrdine nuovoDettaglio = new DettaglioOrdine(1, 2, 3);
        
        // Verifica che il piatto 2 non esista
        assertNull(PiattoDAO.getInstance().getPiattoById(2));
        
        // Crea un piatto valido
        Piatto nuovoPiatto = new Piatto(2, "Piatto Nuovo", true, "15.00", "Nessuno", "Foto", "MenuTest", 1);
        PiattoDAO.getInstance().aggiungiPiatto(nuovoPiatto);
        
        // Aggiungi dettaglio con piatto esistente
        boolean result = DettaglioOrdineDAO.getInstance().aggiungiDettaglioOrdine(nuovoDettaglio);
        assertTrue(result);
        
        // Verifica inserimento
        List<DettaglioOrdine> dettagli = DettaglioOrdineDAO.getInstance().getDettagliByOrdine(1);
        assertTrue(dettagli.stream().anyMatch(d -> 
            d.getIdOrdine() == 1 && 
            d.getIdPiatto() == 2 &&
            d.getQuantita() == 3));
    }

    @Test
    public void testGetDettagliByOrdine_Existing() throws SQLException {
        List<DettaglioOrdine> dettagli = DettaglioOrdineDAO.getInstance().getDettagliByOrdine(1);
        assertFalse(dettagli.isEmpty());
        assertEquals(2, dettagli.get(0).getQuantita());
    }*/

   

    

    @Test(expected = SQLException.class)
    public void testAggiungiDettaglioOrdine_InvalidPiatto() throws SQLException {
        DettaglioOrdine invalidDettaglio = new DettaglioOrdine(1, 999, 1);
        DettaglioOrdineDAO.getInstance().aggiungiDettaglioOrdine(invalidDettaglio);
    }

    @Test
    public void testGetDettagliByOrdine_NonExisting() throws SQLException {
        List<DettaglioOrdine> dettagli = DettaglioOrdineDAO.getInstance().getDettagliByOrdine(999);
        assertTrue(dettagli.isEmpty());
    }
}