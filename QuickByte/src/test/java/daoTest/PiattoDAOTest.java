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
    private static int TEST_ID_RISTORANTE;
    private static final String TEST_EMAIL = "cliente@test.com";
    private static int TEST_ID_PIATTO;

    @BeforeClass
    public static void initDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        }
    }

    @Before
    public void setUp() throws SQLException {
        piattoDAO = PiattoDAO.getInstance();
        PiattoDAO.setAlertEnabled(false);
 
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            System.out.println("1111133330");
            
            System.out.println("111113333t");
            
            // Inserisci Utente
            try (PreparedStatement checkUser = conn.prepareStatement(
            	    "SELECT COUNT(*) FROM Utente WHERE email = ?")) {
            	    checkUser.setString(1, TEST_EMAIL);
            	    try (ResultSet rs = checkUser.executeQuery()) {
            	        if (rs.next() && rs.getInt(1) > 0) {
            	            System.out.println("Utente già esistente, skip inserimento.");
            	        } else {
            	            try (PreparedStatement ps = conn.prepareStatement(
            	                "INSERT INTO Utente (email, password, nome, telefono, tipoUtente) VALUES (?, ?, ?, ?, ?)")) {
            	                ps.setString(1, TEST_EMAIL);
            	                ps.setString(2, "password123");
            	                ps.setString(3, "Cliente Test");
            	                ps.setString(4, "123456789");
            	                ps.setString(5, "Cliente");
            	                ps.executeUpdate();
            	            }
            	        }
            	    }
            	}

    
            // Inserisci Ristorante
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Ristorante Test");
                ps.setString(2, "1234567890");
                ps.setString(3, "Via Roma 123");
                ps.setString(4, TEST_EMAIL);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) TEST_ID_RISTORANTE = rs.getInt(1);
                }
            }
            System.out.println("11111");
            // Inserisci Menu
            try (PreparedStatement checkMenu = conn.prepareStatement(
            	    "SELECT COUNT(*) FROM Menu WHERE nome = ? AND idRistorante = ?")) {
            	    checkMenu.setString(1, "Menu Speciale");
            	    checkMenu.setInt(2, TEST_ID_RISTORANTE);
            	    try (ResultSet rs = checkMenu.executeQuery()) {
            	        if (rs.next() && rs.getInt(1) > 0) {
            	            System.out.println("Menu già esistente, skip inserimento.");
            	        } else {
            	            try (PreparedStatement ps = conn.prepareStatement(
            	                "INSERT INTO Menu (nome, idRistorante) VALUES (?, ?)")) {
            	                ps.setString(1, "Menu Speciale");
            	                ps.setInt(2, TEST_ID_RISTORANTE);
            	                ps.executeUpdate();
            	            }
            	        }
            	    }
            	}


            // Inserisci Piatto
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "PiattoTest");
                ps.setInt(2, 1);
                ps.setString(3, "10.99");
                ps.setString(4, "Latte");
                ps.setString(5, "foto.jpg");
                ps.setString(6, "Menu1");
                ps.setInt(7, TEST_ID_RISTORANTE);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) TEST_ID_PIATTO = rs.getInt(1);
                }
            }
      

            conn.commit();
        } catch (SQLException e) {
            throw e;
        }
    }

    @After
    public void tearDown() throws SQLException {
    	try (Connection conn = DatabaseConnection.connect()) {
    	    conn.createStatement().execute("PRAGMA foreign_keys = OFF;");
    	    conn.setAutoCommit(false);
    	    cleanTestData(conn);
    	    conn.commit();
    	    conn.createStatement().execute("PRAGMA foreign_keys = ON;");
    	}
    }

    private void cleanTestData(Connection conn) throws SQLException {
        try {
            // Disabilita temporaneamente i vincoli di chiave esterna
            conn.createStatement().execute("PRAGMA foreign_keys = OFF;");

            // Ordine di eliminazione corretto per dipendenze
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Carrello WHERE emailUtente = ?")) {
                ps.setString(1, TEST_EMAIL);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Piatto WHERE idRistorante = ?")) {
                ps.setInt(1, TEST_ID_RISTORANTE);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Menu WHERE idRistorante = ?")) {
                ps.setInt(1, TEST_ID_RISTORANTE);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Ristorante WHERE idRistorante = ?")) {
                ps.setInt(1, TEST_ID_RISTORANTE);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Utente WHERE email = ?")) {
                ps.setString(1, TEST_EMAIL);
                ps.executeUpdate();
            }

            // Riabilita i vincoli
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }


    @Test
    public void testGetPiattoById() throws SQLException {
        System.out.println("Test: Recupero piatto per ID...");
        Piatto piatto = piattoDAO.getPiattoById(TEST_ID_PIATTO);
        assertNotNull("Piatto non trovato", piatto);
        assertEquals("Nome del piatto errato", "PiattoTest", piatto.getNome());
    }


    @Test
    public void testAggiornaPiatto() throws SQLException {
        System.out.println("Test: Aggiornamento piatto...");
        Piatto piatto = piattoDAO.getPiattoById(TEST_ID_PIATTO);
        assertNotNull("Piatto non trovato", piatto);

        piatto.setNome("PiattoTestAggiornato");
        piattoDAO.aggiornaPiatto(piatto);

        Piatto piattoAggiornato = piattoDAO.getPiattoById(TEST_ID_PIATTO);
        assertEquals("Nome piatto non aggiornato", "PiattoTestAggiornato", piattoAggiornato.getNome());
    }

    @Test
    public void testRimuoviPiatto() throws SQLException {
        System.out.println("Test: Rimozione piatto...");
        Piatto piatto = piattoDAO.getPiattoById(TEST_ID_PIATTO);
        assertNotNull("Piatto non trovato", piatto);

        piattoDAO.rimuoviPiatto(piatto.getIdPiatto());
        Piatto piattoRimosso = piattoDAO.getPiattoById(TEST_ID_PIATTO);
        assertNull("Piatto non rimosso", piattoRimosso);
    }

    @Test
    public void testGetPiattiByMenuAndIdRistorante() throws SQLException {
        System.out.println("Test: Recupero piatti per menu e ID ristorante...");
        List<Piatto> piatti = piattoDAO.getQualsiasiPiattiByMenuAndIdRistorante("Menu1", TEST_ID_RISTORANTE);
        assertTrue("Dovrebbero esserci piatti", piatti.size() > 0);
    }

    @Test
    public void testAggiungiPiattoAlCarrello() throws SQLException {
        System.out.println("Test: Aggiunta piatto al carrello...");

        Piatto piatto = piattoDAO.getPiattoById(TEST_ID_PIATTO);
        assertNotNull("Il piatto con id " + TEST_ID_PIATTO + " non esiste", piatto);

        String checkClienteQuery = "SELECT 1 FROM Utente WHERE email = ?";
        try (Connection conn = DatabaseConnection.connect(); 
             PreparedStatement ps = conn.prepareStatement(checkClienteQuery)) {
            ps.setString(1, TEST_EMAIL);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue("Il cliente con email " + TEST_EMAIL + " non esiste", rs.next());
            }
        }

        piattoDAO.aggiungiPiattoAlCarrello(TEST_ID_PIATTO, TEST_EMAIL);

        String checkCarrelloQuery = "SELECT quantitaPiatti FROM Carrello WHERE idPiatto = ? AND emailUtente = ?";
        try (Connection conn = DatabaseConnection.connect(); 
             PreparedStatement ps = conn.prepareStatement(checkCarrelloQuery)) {
            ps.setInt(1, TEST_ID_PIATTO);
            ps.setString(2, TEST_EMAIL);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue("Piatto non trovato nel carrello", rs.next());
                assertEquals("Quantità piatti errata", 1, rs.getInt("quantitaPiatti"));
            }
        }
    }

    public void svuotaCarrello(String emailUtente) throws SQLException {
        String deleteQuery = "DELETE FROM Carrello WHERE emailUtente = ?";
        try (Connection conn = DatabaseConnection.connect(); 
             PreparedStatement ps = conn.prepareStatement(deleteQuery)) {
            ps.setString(1, emailUtente);
            ps.executeUpdate();
        }
    }
}

