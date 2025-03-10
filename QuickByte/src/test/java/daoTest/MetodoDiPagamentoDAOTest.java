package daoTest;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.List;
import org.junit.*;
import dao.MetodoDiPagamentoDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.Cliente;
import model.MetodoDiPagamento;

public class MetodoDiPagamentoDAOTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";
    private final Cliente testCliente = new Cliente("test@cliente.com", "password123", "Test User", "1234567890");
    private final MetodoDiPagamento testMetodo = new MetodoDiPagamento(
        "Test User", 
        "1234567890123456", 
        "12/25", 
        testCliente.getEmail()
    );

    @BeforeClass
    public static void initDatabase() throws Exception {
        DatabaseConnection.setDatabaseUrl(TEST_DB_URL);
        new DatabaseManager();
        DatabaseManager.createTables();
    }

    @Before
    public void setupDatabase() throws SQLException {
        // Assicurati che l'utente di test esista
        UtenteDAO.getInstance().insertUtente(testCliente, "Cliente");
        // Inserisci il metodo di pagamento di test
        MetodoDiPagamentoDAO.getInstance().aggiungiMetodo(testMetodo);
    }

    @After
    public void tearDown() {
        try {
            // Rimuovi il metodo di pagamento
            MetodoDiPagamentoDAO.getInstance().rimuoviMetodo(testMetodo.getNumeroCarta());
            // Rimuovi l'utente di test
            UtenteDAO.getInstance().deleteUtente(testCliente.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAggiungiMetodo_Success() throws SQLException {
        MetodoDiPagamento nuovoMetodo = new MetodoDiPagamento(
            "New User", 
            "6543210987654321", 
            "11/26", 
            testCliente.getEmail()
        );
        
        MetodoDiPagamentoDAO.getInstance().aggiungiMetodo(nuovoMetodo);
        
        MetodoDiPagamento risultato = MetodoDiPagamentoDAO.getInstance()
            .getMetodoByNumeroCarta("6543210987654321");
            
        assertNotNull(risultato);
        assertEquals("New User", risultato.getNominativo());
        assertEquals("11/26", risultato.getScadenza());
        
        // Pulizia
        MetodoDiPagamentoDAO.getInstance().rimuoviMetodo("6543210987654321");
    }

    @Test
    public void testGetMetodoByNumeroCarta_Existing() throws SQLException {
        MetodoDiPagamento risultato = MetodoDiPagamentoDAO.getInstance()
            .getMetodoByNumeroCarta(testMetodo.getNumeroCarta());
            
        assertNotNull(risultato);
        assertEquals(testMetodo.getNumeroCarta(), risultato.getNumeroCarta());
        assertEquals("Test User", risultato.getNominativo());
    }

    @Test
    public void testGetMetodoByNumeroCarta_NonExisting() throws SQLException {
        MetodoDiPagamento risultato = MetodoDiPagamentoDAO.getInstance()
            .getMetodoByNumeroCarta("9999999999999999");
        assertNull(risultato);
    }

    @Test
    public void testAggiornaMetodo_Success() throws SQLException {
        MetodoDiPagamento metodoAggiornato = new MetodoDiPagamento(
            "Updated User", 
            testMetodo.getNumeroCarta(), 
            "01/27", 
            testCliente.getEmail()
        );
        
        MetodoDiPagamentoDAO.getInstance().aggiornaMetodo(metodoAggiornato);
        
        MetodoDiPagamento risultato = MetodoDiPagamentoDAO.getInstance()
            .getMetodoByNumeroCarta(testMetodo.getNumeroCarta());
            
        assertEquals("Updated User", risultato.getNominativo());
        assertEquals("01/27", risultato.getScadenza());
    }

    @Test
    public void testRimuoviMetodo_Success() throws SQLException {
        MetodoDiPagamentoDAO.getInstance().rimuoviMetodo(testMetodo.getNumeroCarta());
        
        MetodoDiPagamento risultato = MetodoDiPagamentoDAO.getInstance()
            .getMetodoByNumeroCarta(testMetodo.getNumeroCarta());
            
        assertNull(risultato);
    }

    @Test
    public void testGetMetodiPagamento() {
        List<String> metodi = MetodoDiPagamentoDAO.getInstance()
            .getMetodiPagamento(testCliente.getEmail());
            
        assertFalse(metodi.isEmpty());
        assertTrue(metodi.contains(testMetodo.getNumeroCarta() + " - Test User"));
    }

    @Test(expected = SQLException.class)
    public void testAggiungiMetodo_InvalidUser() throws SQLException {
        MetodoDiPagamento metodoInvalido = new MetodoDiPagamento(
            "Invalid User", 
            "1111222233334444", 
            "12/25", 
            "invalid@cliente.com"
        );
        MetodoDiPagamentoDAO.getInstance().aggiungiMetodo(metodoInvalido);
    }
}