package daoTest;

import static org.junit.Assert.*;
import org.junit.*;
import dao.PiattoDAO;
import database.DatabaseConnection;
import model.Piatto;
import java.sql.*;
import java.util.List;

public class PiattoDAOTest {

    private PiattoDAO piattoDAO;
    private static final int TEST_ID_RISTORANTE = 1;
    private static final String TEST_EMAIL = "cliente@test.com";
    
    @BeforeClass
    public static void initDatabase() throws Exception {
        System.out.println("Verifica connessione al database...");
        try (Connection conn = DatabaseConnection.connect()) {
            assertNotNull("Connessione fallita!", conn);
            System.out.println("Connessione stabilita con successo.");
        }
    }

    @Before
    public void setUp() throws SQLException {
        piattoDAO = PiattoDAO.getInstance();
        System.out.println("Inserimento dati di test...");

        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            
            // Avvia una transazione esplicita
            conn.setAutoCommit(false);

            try {
            	// Inserisci un cliente di test se non esiste
                String insertClienteQuery = "INSERT INTO Utente (email, nome) VALUES (?, 'Cliente Test')";
                try (PreparedStatement ps = conn.prepareStatement(insertClienteQuery)) {
                    ps.setString(1, TEST_EMAIL);
                    ps.executeUpdate();
                }
                // Inserisci un ristorante di test se non esiste
            	String insertRistoranteQuery = "INSERT INTO Ristorante (idRistorante, nome, telefono, indirizzo) VALUES (?, 'Ristorante Test', ?, ?)";
            	try (PreparedStatement ps = conn.prepareStatement(insertRistoranteQuery)) {
            	    ps.setInt(1, TEST_ID_RISTORANTE);
            	    ps.setString(2, "1234567890"); // Esempio di numero di telefono
            	    ps.setString(3, "Via Roma 123"); // Esempio di indirizzo
            	    ps.executeUpdate();
            	}

                // Verifica che il ristorante sia stato inserito correttamente
                String checkRistoranteQuery = "SELECT COUNT(*) FROM Ristorante WHERE idRistorante = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkRistoranteQuery)) {
                    ps.setInt(1, TEST_ID_RISTORANTE);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int count = rs.getInt(1);
                        System.out.println("Numero di ristoranti trovati: " + count);
                        if (count == 0) {
                            throw new SQLException("Ristorante test con ID " + TEST_ID_RISTORANTE + " non trovato.");
                        }
                    }
                }

                

                // Inserisci un piatto di test
                String insertPiattoQuery = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) " +
                                           "VALUES ('PiattoTest', 1, '10.99', 'Latte', 'foto.jpg', 'Menu1', ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertPiattoQuery)) {
                    ps.setInt(1, TEST_ID_RISTORANTE);
                    ps.executeUpdate();
                }

                // Commit dei cambiamenti
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();  // Annulla la transazione in caso di errore
                throw e;
            } finally {
                conn.setAutoCommit(true); // Ripristina la modalità di autocommit
            }
        }
    }





    @After
    public void tearDown() throws SQLException {
        System.out.println("Pulizia dati di test...");
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("DELETE FROM Piatto WHERE nome = 'PiattoTest'");
        }
    }

    @Test
    public void testGetPiattoById() throws SQLException {
        System.out.println("Test: Recupero piatto per ID...");
        Piatto piatto = piattoDAO.getPiattoById(1);
        assertNotNull("Piatto non trovato", piatto);
        assertEquals("Nome del piatto errato", "PiattoTest", piatto.getNome());
    }

    @Test
    public void testAggiungiPiatto() throws SQLException {
        System.out.println("Test: Aggiunta piatto...");
        Piatto piatto = new Piatto(0, "PiattoNuovo", true, "12.99", "Nessuno", "foto2.jpg", "Menu2", TEST_ID_RISTORANTE);
        piattoDAO.aggiungiPiatto(piatto);

        Piatto piattoRecuperato = piattoDAO.getPiattoById(piatto.getIdPiatto());
        assertNotNull("Piatto non aggiunto", piattoRecuperato);
        assertEquals("Nome piatto errato", "PiattoNuovo", piattoRecuperato.getNome());
    }

    @Test
    public void testAggiornaPiatto() throws SQLException {
        System.out.println("Test: Aggiornamento piatto...");
        Piatto piatto = piattoDAO.getPiattoById(1);
        assertNotNull("Piatto non trovato", piatto);

        piatto.setNome("PiattoTestAggiornato");
        piattoDAO.aggiornaPiatto(piatto);

        Piatto piattoAggiornato = piattoDAO.getPiattoById(1);
        assertEquals("Nome piatto non aggiornato", "PiattoTestAggiornato", piattoAggiornato.getNome());
    }

    @Test
    public void testRimuoviPiatto() throws SQLException {
        System.out.println("Test: Rimozione piatto...");
        Piatto piatto = piattoDAO.getPiattoById(1);
        assertNotNull("Piatto non trovato", piatto);

        piattoDAO.rimuoviPiatto(piatto.getIdPiatto());
        Piatto piattoRimosso = piattoDAO.getPiattoById(1);
        assertNull("Piatto non rimosso", piattoRimosso);
    }

    @Test
    public void testGetPiattiByMenuAndIdRistorante() throws SQLException {
        System.out.println("Test: Recupero piatti per menu e ID ristorante...");
        List<Piatto> piatti = piattoDAO.getPiattiByMenuAndIdRistorante("Menu1", TEST_ID_RISTORANTE);
        assertTrue("Dovrebbero esserci piatti", piatti.size() > 0);
    }

    @Test
    public void testAggiungiPiattoAlCarrello() throws SQLException {
        System.out.println("Test: Aggiunta piatto al carrello...");
        
        Piatto piatto = piattoDAO.getPiattoById(1);
        assertNotNull("Il piatto con id 1 non esiste", piatto);
        
        String checkClienteQuery = "SELECT 1 FROM Utente WHERE email = ?";
        try (Connection conn = DatabaseConnection.connect(); 
             PreparedStatement ps = conn.prepareStatement(checkClienteQuery)) {
            ps.setString(1, TEST_EMAIL);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue("Il cliente con email " + TEST_EMAIL + " non esiste", rs.next());
            }
        }

        piattoDAO.aggiungiPiattoAlCarrello(1, TEST_EMAIL);

        String checkCarrelloQuery = "SELECT quantitaPiatti FROM Carrello WHERE idPiatto = 1 AND emailUtente = ?";
        try (Connection conn = DatabaseConnection.connect(); 
             PreparedStatement ps = conn.prepareStatement(checkCarrelloQuery)) {
            ps.setString(1, TEST_EMAIL);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue("Piatto non trovato nel carrello", rs.next());
                assertEquals("Quantità piatti errata", 1, rs.getInt("quantitaPiatti"));
            }
        }
    }


    @Test
    public void testSvuotaCarrello() throws SQLException {
        System.out.println("Test: Svuotamento carrello...");
        piattoDAO.svuotaCarrello(TEST_EMAIL);

        String checkCarrelloQuery = "SELECT * FROM Carrello WHERE emailUtente = ?";
        try (Connection conn = DatabaseConnection.connect(); 
             PreparedStatement ps = conn.prepareStatement(checkCarrelloQuery)) {
            ps.setString(1, TEST_EMAIL);
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse("Carrello non vuoto", rs.next());
            }
        }
    }
}
