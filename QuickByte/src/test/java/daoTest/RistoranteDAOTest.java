package daoTest;

import static org.junit.Assert.*;
import org.junit.*;
import dao.RistoranteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.Ristorante;
import java.sql.*;
import java.util.List;

public class RistoranteDAOTest {
    private RistoranteDAO ristoranteDAO;
    private static final String TEST_EMAIL = "titolare@test.com";

    @BeforeClass
    public static void initDatabase() throws Exception {
        System.out.println("Verifica connessione al database...");
        try (Connection conn = DatabaseConnection.connect()) {
            assertNotNull("Connessione fallita!", conn);
            System.out.println("Connessione stabilita con successo.");
            DatabaseManager.createTables();
        }
    }

    @Before
    public void setUp() throws SQLException {
        ristoranteDAO = RistoranteDAO.getInstance();
        System.out.println("Inserimento dati di test...");

        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");

            PreparedStatement checkUser = conn.prepareStatement("SELECT email FROM Utente WHERE email = ?");
            checkUser.setString(1, TEST_EMAIL);
            ResultSet rs = checkUser.executeQuery();

            if (!rs.next()) {
                PreparedStatement insertUser = conn.prepareStatement(
                    "INSERT INTO Utente (email, nome, password, telefono, tipoUtente) VALUES (?, ?, ?, ?, ?)");
                insertUser.setString(1, TEST_EMAIL);
                insertUser.setString(2, "Test");
                insertUser.setString(3, "password123");
                insertUser.setString(4, "12345");
                insertUser.setString(5, "Cliente");
                insertUser.executeUpdate();
            }
        }

        ristoranteDAO.inserisciRistorante("Risto1", "1111111111", "Via Test 1", TEST_EMAIL);
        ristoranteDAO.inserisciRistorante("Risto2", "2222222222", "Via Test 2", TEST_EMAIL);
    }


    @After
    public void tearDown() throws SQLException {
        System.out.println("Pulizia dati di test...");
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Ristorante WHERE emailTitolare = ?")) {
            ps.setString(1, TEST_EMAIL);
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetRistorantiByEmail() throws SQLException {
        System.out.println("Test: Recupero ristoranti per email...");
        List<Ristorante> ristoranti = ristoranteDAO.getRistorantiByEmail(TEST_EMAIL);
        assertEquals("Dovrebbero esserci 2 ristoranti", 2, ristoranti.size());
    }

    @Test
    public void testGetRistoranti() throws SQLException {
        System.out.println("Test: Recupero tutti i ristoranti...");
        List<Ristorante> ristoranti = ristoranteDAO.getRistoranti();
        assertTrue("Dovrebbero esserci almeno 2 ristoranti", ristoranti.size() >= 2);
    }

    @Test
    public void testGetRistoranteByNome() throws SQLException {
        System.out.println("Test: Recupero ristorante per nome...");
        Ristorante r = ristoranteDAO.getRistoranteByNome("Risto1", TEST_EMAIL);
        assertNotNull("Ristorante non trovato", r);
        assertEquals("Nome errato", "Risto1", r.getNome());
    }

    @Test
    public void testRimuoviRistorante() throws SQLException {
        System.out.println("Test: Rimozione ristorante...");
        Ristorante r = ristoranteDAO.getRistoranteByNome("Risto1", TEST_EMAIL);
        ristoranteDAO.rimuoviRistorante(r.getIdRistorante());
        assertNull("Ristorante non rimosso", ristoranteDAO.getRistoranteByNome("Risto1", TEST_EMAIL));
    }

    @Test
    public void testInserisciRistorante() throws SQLException {
        System.out.println("Test: Inserimento nuovo ristorante...");
        ristoranteDAO.inserisciRistorante("NuovoRisto", "3333333333", "Via Nuova 3", TEST_EMAIL);
        assertNotNull("Nuovo ristorante non trovato", ristoranteDAO.getRistoranteByNome("NuovoRisto", TEST_EMAIL));
    }

    @Test
    public void testAggiornaRistorante() throws SQLException {
        System.out.println("Test: Aggiornamento ristorante...");
        Ristorante r = ristoranteDAO.getRistoranteByNome("Risto1", TEST_EMAIL);
        r.setTelefono("0000000000");
        assertTrue("Aggiornamento fallito", ristoranteDAO.aggiornaRistorante(r, "Risto1"));
        assertEquals("Telefono non aggiornato", "0000000000", ristoranteDAO.getRistoranteByNome("Risto1", TEST_EMAIL).getTelefono());
    }

    @Test
    public void testGetRistoranteByNome_NonEsistente() throws SQLException {
        System.out.println("Test: Recupero ristorante inesistente...");
        assertNull("Ristorante dovrebbe essere nullo", ristoranteDAO.getRistoranteByNome("Inesistente", TEST_EMAIL));
    }
}
