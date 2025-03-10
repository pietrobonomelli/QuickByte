package daoTest;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import dao.OrdineDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.*;
import sessione.SessioneCarrello;
import sessione.SessioneRistorante;

public class OrdineDAOTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";
    private final Ordine testOrdine = new Ordine(1, StatoOrdine.PENDENTE.name(), 25.50, 
            "2023-01-01 10:00:00", "Via Test 123", "test@cliente.com", null, 1);
    private final Cliente testCliente = new Cliente("test@cliente.com", "pass", "Mario Rossi", "1234567890");
    private final Titolare testTitolare = new Titolare("test@titolare.com", "pass", "Luigi Bianchi", "0987654321");
    private final Corriere testCorriere = new Corriere("test@corriere.com", "pass", "Anna Verdi", "1122334455");

    @BeforeClass
    public static void initDatabase() throws Exception {
        DatabaseConnection.setDatabaseUrl(TEST_DB_URL);
        new DatabaseManager();
        DatabaseManager.createTables();
    }

    @Before
    public void setupDatabase() throws SQLException {
        SessioneRistorante.setId(1); // Imposta idRistorante valido
        insertTestData();
    }

    @After
    public void tearDown() {
        clearTestData();
        SessioneCarrello.setPieno(false);
    }

    private void insertTestData() throws SQLException {
        // Inserisce utenti di test
        UtenteDAO.getInstance().insertUtente(testCliente, "Cliente");
        UtenteDAO.getInstance().insertUtente(testTitolare, "Titolare");
        UtenteDAO.getInstance().insertUtente(testCorriere, "Corriere");
        
        // Inserisce ristorante di test
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO Ristorante (idRistorante, nome, telefono, indirizzo, emailTitolare) " +
                "VALUES (1, 'Test Risto', '123456', 'Via Risto', 'test@titolare.com')")) {
            stmt.executeUpdate();
        }
        
        // Inserisce ordine di test
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO Ordine (idOrdine, stato, costo, dataOraOrdine, indirizzo, emailCliente, idRistorante) " +
                "VALUES (1, 'PENDENTE', 25.50, '2023-01-01 10:00:00', 'Via Test 123', 'test@cliente.com', 1)")) {
            stmt.executeUpdate();
        }
    }

    private void clearTestData() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Elimina ordini
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ordine WHERE emailCliente = test@cliente.com")) {
                stmt.executeUpdate();
            }
            // Elimina ristorante
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ristorante WHERE emailTitolare = test@titolare.com")) {
                stmt.executeUpdate();
            }
            // Elimina utenti
            UtenteDAO.getInstance().deleteUtente(testCliente.getEmail());
            UtenteDAO.getInstance().deleteUtente(testTitolare.getEmail());
            UtenteDAO.getInstance().deleteUtente(testCorriere.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGetOrdineById_NonExisting() {
        Ordine result = OrdineDAO.getInstance().getOrdineById(999);
        assertNull(result);
    }

    @Test
    public void testUpdateStatoOrdine() {
        boolean result = OrdineDAO.getInstance().updateStatoOrdine(1, StatoOrdine.IN_CONSEGNA.name());
        assertTrue(result);
        
        Ordine updated = OrdineDAO.getInstance().getOrdineById(1);
        assertEquals(StatoOrdine.IN_CONSEGNA.name(), updated.getStato());
    }

    

    @Test
    public void testGetOrdiniByStato() {
        List<Ordine> ordini = OrdineDAO.getInstance().getOrdiniByStato(StatoOrdine.PENDENTE.name());
        assertFalse(ordini.isEmpty());
        assertTrue(ordini.stream().allMatch(o -> o.getStato().equals(StatoOrdine.PENDENTE.name())));
    }

    @Test
    public void testRegistraOrdine() throws SQLException {
        // Inserisce dati nel carrello
        String insertCarrelloSQL = "INSERT INTO Carrello (quantitaPiatti, idPiatto, emailUtente) VALUES (2, 1, 'test@cliente.com')";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(insertCarrelloSQL)) {
            stmt.executeUpdate();
        }
        
        boolean result = OrdineDAO.getInstance().registraOrdine("test@cliente.com", "Via Nuova 456");
        assertTrue(result);
        
        // Verifica che il carrello sia vuoto
        List<Ordine> ordini = OrdineDAO.getInstance().getOrdiniByEmailCliente("test@cliente.com");
        assertTrue(ordini.size() > 0);
    }

    @Test
    public void testGetNomeRistorante() {
        String nome = OrdineDAO.getInstance().getNomeRistorante(testOrdine);
        assertEquals("Test Risto", nome);
    }

    @Test
    public void testAggiornaEmailCorriereOrdine() {
        boolean result = OrdineDAO.getInstance().aggiornaEmailCorriereOrdine(1, "test@corriere.com");
        assertTrue(result);
        
        Ordine updated = OrdineDAO.getInstance().getOrdineById(1);
        assertEquals("test@corriere.com", updated.getEmailCorriere());
    }

}


