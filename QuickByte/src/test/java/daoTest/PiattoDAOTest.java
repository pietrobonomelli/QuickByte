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
    private static int TEST_ID_PIATTO;  // Variabile per l'ID del piatto

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
        piattoDAO = PiattoDAO.getInstance();  // Inizializzazione della variabile
        System.out.println("Inserimento dati di test...");
        PiattoDAO.setAlertEnabled(false);

        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");

            // Avvia una transazione esplicita
            conn.setAutoCommit(false);

            try {
                String checkEmailQuery = "SELECT COUNT(*) FROM Utente WHERE email = ?";

                // Poi la utilizzi nel PreparedStatement
                try (PreparedStatement ps = conn.prepareStatement(checkEmailQuery)) {
                    ps.setString(1, TEST_EMAIL);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println("L'email è già presente nel database.");
                        } else {
                            // Procedi con l'inserimento
                            String insertClienteQuery = "INSERT INTO Utente (email, password, nome, telefono, tipoUtente) VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement psInsert = conn.prepareStatement(insertClienteQuery)) {
                                psInsert.setString(1, TEST_EMAIL);
                                psInsert.setString(2, "password123");
                                psInsert.setString(3, "Cliente Test");
                                psInsert.setString(4, "1234567890");
                                psInsert.setString(5, "Cliente");
                                psInsert.executeUpdate();
                            }
                        }
                    }
                }

                // Inserisci un ristorante di test
                String insertRistoranteQuery = "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertRistoranteQuery)) {
                    ps.setString(1, "Ristorante Test");  // Nome del ristorante
                    ps.setString(2, "1234567890");       // Numero di telefono
                    ps.setString(3, "Via Roma 123");     // Indirizzo
                    ps.setString(4, TEST_EMAIL);         // Email del titolare
                    ps.executeUpdate();
                }

                // Recupera l'ID del ristorante appena inserito
                String getRistoranteIdQuery = "SELECT last_insert_rowid()";
                int ristoranteId;
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(getRistoranteIdQuery)) {
                    if (rs.next()) {
                        ristoranteId = rs.getInt(1);
                        System.out.println("ID del ristorante inserito: " + ristoranteId);
                    } else {
                        throw new SQLException("Impossibile ottenere l'ID del ristorante.");
                    }
                }
                TEST_ID_RISTORANTE = ristoranteId;

                // Inserisci un menu di test
                String insertMenuQuery = "INSERT INTO Menu (nome, idRistorante) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertMenuQuery)) {
                    ps.setString(1, "Menu1");
                    ps.setInt(2, ristoranteId);
                    ps.executeUpdate();
                }

                // Inserisci un piatto di test
                String insertPiattoQuery = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) " +
                        "VALUES ('PiattoTest', 1, '10.99', 'Latte', 'foto.jpg', 'Menu1', ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertPiattoQuery)) {
                    ps.setInt(1, ristoranteId);  // Usa l'ID del ristorante appena recuperato
                    ps.executeUpdate();
                }

                // Recupera l'ID del piatto appena inserito
                String getPiattoIdQuery = "SELECT last_insert_rowid()";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(getPiattoIdQuery)) {
                    if (rs.next()) {
                        TEST_ID_PIATTO = rs.getInt(1);
                        System.out.println("ID del piatto inserito: " + TEST_ID_PIATTO);
                    } else {
                        throw new SQLException("Impossibile ottenere l'ID del piatto.");
                    }
                }

                // Commit dei cambiamenti
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();  // Annulla la transazione in caso di errore
                throw e;
            } finally {
                conn.setAutoCommit(true); // Ripristina la modalità di autocommit
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Gestisci le eccezioni con stack trace se necessario
            throw e;
        }
    }

    @After
    public void tearDown() throws SQLException {
        Connection conn = DatabaseConnection.connect();
        
        // Rimuovi prima eventuali riferimenti in Carrello
        String deleteCarrelloQuery = "DELETE FROM Carrello WHERE idPiatto = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteCarrelloQuery)) {
            ps.setInt(1, TEST_ID_PIATTO); // idPiattoTest è il piatto di test usato nei metodi
            ps.executeUpdate();
        }
        
        // Ora puoi rimuovere il piatto senza violare vincoli di chiave esterna
        String deletePiattoQuery = "DELETE FROM Piatto WHERE idPiatto = ?";
        try (PreparedStatement ps = conn.prepareStatement(deletePiattoQuery)) {
            ps.setInt(1, TEST_ID_PIATTO);
            ps.executeUpdate();
        }

        conn.close();
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
        List<Piatto> piatti = piattoDAO.getPiattiByMenuAndIdRistorante("Menu1", TEST_ID_RISTORANTE);
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

