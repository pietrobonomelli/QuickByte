package daoTest;

import static org.junit.Assert.*;
import org.junit.*;
import dao.LoginDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.Cliente;
import model.Corriere;
import model.Titolare;
import java.sql.SQLException;

public class LoginDAOTest {
    private static final String SYSTEM_DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";
    
    private final Cliente testCliente = new Cliente(
        "test_cliente@test.com", 
        "password123", 
        "Test Cliente", 
        "123456789"
    );
    
    private final Titolare testTitolare = new Titolare(
        "test_titolare@test.com", 
        "password456", 
        "Test Titolare", 
        "987654321"
    );
    
    private final Corriere testCorriere = new Corriere(
        "test_corriere@test.com", 
        "password789", 
        "Test Corriere", 
        "1122334455"
    );

    @BeforeClass
    public static void initDatabase() throws Exception {
        DatabaseConnection.setDatabaseUrl(SYSTEM_DB_URL);
        new DatabaseManager();
    }

    @Before
    public void setupDatabase() throws SQLException {
        insertTestData();
    }

    @After
    public void tearDown() {
        clearTestData();
    }

    private void insertTestData() throws SQLException {
        UtenteDAO dao = UtenteDAO.getInstance();
        dao.insertUtente(testCliente, "Cliente");
        dao.insertUtente(testTitolare, "Titolare");
        dao.insertUtente(testCorriere, "Corriere");
    }

    private void clearTestData() {
        UtenteDAO dao = UtenteDAO.getInstance();
        dao.deleteUtente(testCliente.getEmail());
        dao.deleteUtente(testTitolare.getEmail());
        dao.deleteUtente(testCorriere.getEmail());
    }

    @Test
    public void testVerifyUser_Success() {
        assertTrue("Autenticazione valida dovrebbe riuscire",
            LoginDAO.getInstance().verifyUser(testCliente.getEmail(), "password123"));
    }

    @Test
    public void testVerifyUser_WrongPassword() {
        assertFalse("Password errata dovrebbe fallire",
            LoginDAO.getInstance().verifyUser(testCliente.getEmail(), "wrongpass"));
    }

    @Test
    public void testVerifyUser_NonExistingUser() {
        assertFalse("Utente inesistente dovrebbe fallire",
            LoginDAO.getInstance().verifyUser("nonexistent@test.com", "password"));
    }

    @Test
    public void testGetUserType_Valid() {
        assertEquals("Tipo utente cliente non corrisponde", "Cliente",
            LoginDAO.getInstance().getUserType(testCliente.getEmail()));
        assertEquals("Tipo utente titolare non corrisponde", "Titolare",
            LoginDAO.getInstance().getUserType(testTitolare.getEmail()));
        assertEquals("Tipo utente corriere non corrisponde", "Corriere",
            LoginDAO.getInstance().getUserType(testCorriere.getEmail()));
    }

    @Test
    public void testGetUserType_Invalid() {
        assertNull("Tipo utente inesistente dovrebbe essere null",
            LoginDAO.getInstance().getUserType("invalid@test.com"));
    }
}
