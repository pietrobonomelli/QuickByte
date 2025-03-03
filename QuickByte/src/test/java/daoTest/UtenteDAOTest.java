package daoTest;

import static org.junit.Assert.*;

import java.sql.*;
import java.util.List;

import org.junit.*;

import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.*;

public class UtenteDAOTest {
    private static final String TEST_DB_URL = "jdbc:sqlite::memory:";
    private final Cliente testCliente = new Cliente("test@cliente.com", "pass", "Mario Rossi", "1234567890");
    private final Titolare testTitolare = new Titolare("test@titolare.com", "pass", "Luigi Bianchi", "0987654321");
    private final Corriere testCorriere = new Corriere("test@corriere.com", "pass", "Anna Verdi", "1122334455");
    
    @Before
    public void setupDatabase() {
        DatabaseManager.createTables();  // Assicura che le tabelle esistano prima del test
        DatabaseManager.printExistingTables();  // Controlla se la tabella Utente è presente
    }

    @BeforeClass
    public static void initDatabase() throws Exception {
        // Configura il database in memoria per i test
        DatabaseConnection.setDatabaseUrl(TEST_DB_URL);
        
        new DatabaseManager();
        DatabaseManager.createTables();
    }

    @Before
    public void setUp() {
        insertTestData();
    }

    @After
    public void tearDown() {
        clearTestData();
    }

    private void insertTestData() {
        UtenteDAO.getInstance().insertUtente(testCliente, "Cliente");
        UtenteDAO.getInstance().insertUtente(testTitolare, "Titolare");
        UtenteDAO.getInstance().insertUtente(testCorriere, "Corriere");
    }

    private void clearTestData() {
        UtenteDAO.getInstance().deleteUtente(testCliente.getEmail());
        UtenteDAO.getInstance().deleteUtente(testTitolare.getEmail());
        UtenteDAO.getInstance().deleteUtente(testCorriere.getEmail());
    }

    @Test
    public void testGetUtenteByEmail_ExistingUser() {
        Utente result = UtenteDAO.getInstance().getUtenteByEmail(testCliente.getEmail());
        assertNotNull("Dovrebbe trovare l'utente", result);
        assertEquals("Email dovrebbe corrispondere", testCliente.getEmail(), result.getEmail());
    }

    @Test
    public void testGetUtenteByEmail_NonExistingUser() {
        Utente result = UtenteDAO.getInstance().getUtenteByEmail("nonexistent@test.com");
        assertNull("Non dovrebbe trovare utenti", result);
    }

    @Test
    public void testGetAllUtenti() {
        List<Utente> utenti = UtenteDAO.getInstance().getAllUtenti();
        assertTrue("Dovrebbero esserci almeno 3 utenti", utenti.size() >= 3);
    }

    @Test
    public void testInsertUtente_Success() {
        Cliente newCliente = new Cliente("new@test.com", "pass", "New User", "0011223344");
        boolean result = UtenteDAO.getInstance().insertUtente(newCliente, "Cliente");
        assertTrue("Inserimento dovrebbe riuscire", result);
        
        Utente retrieved = UtenteDAO.getInstance().getUtenteByEmail(newCliente.getEmail());
        assertNotNull("Utente dovrebbe esistere nel DB", retrieved);
    }

    @Test
    public void testDeleteUtente_Success() {
        boolean result = UtenteDAO.getInstance().deleteUtente(testCorriere.getEmail());
        assertTrue("Cancellazione dovrebbe riuscire", result);
        
        Utente deleted = UtenteDAO.getInstance().getUtenteByEmail(testCorriere.getEmail());
        assertNull("Utente dovrebbe essere cancellato", deleted);
    }

    @Test
    public void testUpdateUtente_Success() {
        String newPassword = "newpass123";
        testCliente.setPassword(newPassword);
        
        boolean result = UtenteDAO.getInstance().updateUtente(testCliente);
        assertTrue("Aggiornamento dovrebbe riuscire", result);
        
        Utente updated = UtenteDAO.getInstance().getUtenteByEmail(testCliente.getEmail());
        assertEquals("Password dovrebbe essere aggiornata", newPassword, updated.getPassword());
    }

    @Test
    public void testCreateUtenteDaResultSet_Cliente() throws SQLException {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Utente WHERE email = '" + testCliente.getEmail() + "'")) {
            
            rs.next();
            Utente result = UtenteDAO.getInstance().creaUtenteDaResultSet(rs);
            
            assertTrue("Dovrebbe essere un Cliente", result instanceof Cliente);
            assertEquals("Nome dovrebbe corrispondere", testCliente.getNome(), result.getNome());
        }
    }

    @Test(expected = SQLException.class)
    public void testInvalidUserType() throws SQLException {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Utente VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, "invalid@test.com");
            stmt.setString(2, "pass");
            stmt.setString(3, "Invalid User");
            stmt.setString(4, "12345");
            stmt.setString(5, "TipoSconosciuto");
            stmt.executeUpdate();
            
            Utente result = UtenteDAO.getInstance().getUtenteByEmail("invalid@test.com");
            assertNull("Dovrebbe restituire null", result);
        }
    }
}
