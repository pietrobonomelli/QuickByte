package daoTest;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;
import org.junit.*;
import dao.IndirizzoDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.Cliente;
import model.Indirizzo;

public class IndirizzoDAOTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";
    private final Cliente testCliente = new Cliente("test_cliente@test.com", "pass123", "Test User", "123456789");
    private final Indirizzo testIndirizzo = new Indirizzo(
        0, 
        "Via Test 123", 
        "Test City", 
        "12345", 
        "Test Province", 
        testCliente.getEmail()
    );
    
    private int testIndirizzoId;

    @BeforeClass
    public static void initDatabase() throws Exception {
        DatabaseConnection.setDatabaseUrl(TEST_DB_URL);
        new DatabaseManager();
        DatabaseManager.createTables();
    }

    @Before
    public void setupDatabase() throws SQLException {
        // 1. Pulizia preventiva
        clearAllData();
        
        // 2. Inserisci utente di test
        UtenteDAO.getInstance().insertUtente(testCliente, "Cliente");
        
        // 3. Inserisci indirizzo di test e ottieni l'ID generato
        IndirizzoDAO.getInstance().aggiungiIndirizzo(testIndirizzo);
        testIndirizzoId = getIndirizzoIdFromDatabase();
    }

    @After
    public void tearDown() {
        clearAllData();
    }

    private int getIndirizzoIdFromDatabase() throws SQLException {
        String query = "SELECT idIndirizzo FROM Indirizzo WHERE emailUtente = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, testCliente.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idIndirizzo");
            }
        }
        return -1;
    }

    private void clearAllData() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Elimina indirizzi dell'utente
            try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Indirizzo WHERE emailUtente = ?")) {
                stmt.setString(1, testCliente.getEmail());
                stmt.executeUpdate();
            }
            // Elimina utente di test
            UtenteDAO.getInstance().deleteUtente(testCliente.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAggiungiIndirizzo_Success() throws SQLException {
        Indirizzo nuovoIndirizzo = new Indirizzo(
            0, 
            "Via Nuova", 
            "New City", 
            "54321", 
            "New Province", 
            testCliente.getEmail()
        );
        
        IndirizzoDAO.getInstance().aggiungiIndirizzo(nuovoIndirizzo);
        
        List<Indirizzo> indirizzi = IndirizzoDAO.getInstance().getIndirizziByUtente(testCliente.getEmail());
        assertTrue(indirizzi.stream().anyMatch(i -> 
            i.getIndirizzo().equals("Via Nuova") &&
            i.getCitta().equals("New City")
        ));
    }

    @Test
    public void testGetIndirizziByUtente_Existing() throws SQLException {
        List<Indirizzo> indirizzi = IndirizzoDAO.getInstance().getIndirizziByUtente(testCliente.getEmail());
        assertFalse(indirizzi.isEmpty());
        assertEquals(testIndirizzo.getIndirizzo(), indirizzi.get(0).getIndirizzo());
    }

    @Test
    public void testAggiornaIndirizzo_Success() throws SQLException {
        Indirizzo indirizzoAggiornato = new Indirizzo(
            testIndirizzoId, 
            "Via Aggiornata", 
            "City Update", 
            "54321", 
            "Prov Update", 
            testCliente.getEmail()
        );
        
        IndirizzoDAO.getInstance().aggiornaIndirizzo(indirizzoAggiornato);
        
        List<Indirizzo> indirizzi = IndirizzoDAO.getInstance().getIndirizziByUtente(testCliente.getEmail());
        assertTrue(indirizzi.stream().anyMatch(i -> 
            i.getIndirizzo().equals("Via Aggiornata")
        ));
    }

    @Test
    public void testRimuoviIndirizzo_Success() throws SQLException {
        IndirizzoDAO.getInstance().rimuoviIndirizzo(testIndirizzoId);
        
        List<Indirizzo> indirizzi = IndirizzoDAO.getInstance().getIndirizziByUtente(testCliente.getEmail());
        assertTrue(indirizzi.isEmpty());
    }

    @Test
    public void testGetIndirizzi_Existing() {
        List<String> indirizzi = IndirizzoDAO.getInstance().getIndirizzi(testCliente.getEmail());
        assertFalse(indirizzi.isEmpty());
        assertTrue(indirizzi.contains("Via Test 123"));
    }

    @Test
    public void testGetIndirizzi_NonExistingUser() {
        List<String> indirizzi = IndirizzoDAO.getInstance().getIndirizzi("nonexistent@test.com");
        assertTrue(indirizzi.isEmpty());
    }

    @Test(expected = SQLException.class)
    public void testAggiungiIndirizzo_InvalidUser() throws SQLException {
        Indirizzo invalidIndirizzo = new Indirizzo(
            0, 
            "Via Invalid", 
            "Invalid City", 
            "00000", 
            "Invalid Prov", 
            "invalid@user.com"
        );
        IndirizzoDAO.getInstance().aggiungiIndirizzo(invalidIndirizzo);
    }
}