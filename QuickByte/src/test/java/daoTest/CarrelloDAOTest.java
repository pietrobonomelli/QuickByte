package daoTest;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;
import org.junit.*;
import dao.CarrelloDAO;
import dao.PiattoDAO;
import dao.RistoranteDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.*;

public class CarrelloDAOTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";
    
    // Dati di test
    private final Cliente testCliente = new Cliente("test_cliente@test.com", "pass123", "Test Cliente", "123456789");
    private final Titolare testTitolare = new Titolare("test_titolare@test.com", "pass456", "Test Titolare", "987654321");
    private Ristorante testRistorante;
    private Piatto testPiatto;
    private Carrello testCarrello;

    @BeforeClass
    public static void initDatabase() throws Exception {
        DatabaseConnection.setDatabaseUrl(TEST_DB_URL);
        new DatabaseManager();
        DatabaseManager.createTables();
    }

    @Before
    public void setupDatabase() throws SQLException {
        clearAllData();
        
        // 1. Inserisci utenti
        UtenteDAO.getInstance().insertUtente(testCliente, "Cliente");
        UtenteDAO.getInstance().insertUtente(testTitolare, "Titolare");
        
        // 2. Inserisci ristorante e recupera ID generato
        RistoranteDAO.getInstance().inserisciRistorante(
            "Test Risto", 
            "123456", 
            "Via Test", 
            testTitolare.getEmail()
        );
        
        testRistorante = RistoranteDAO.getInstance().getRistoranteByNome(
            "Test Risto", 
            testTitolare.getEmail()
        );
        assertNotNull("Ristorante non inserito", testRistorante);
        
        // 3. Inserisci menu
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Menu (nome, idRistorante) VALUES ('Test Menu', ?)")) {
            stmt.setInt(1, testRistorante.getIdRistorante());
            stmt.executeUpdate();
        }
        
        // 4. Inserisci piatto e recupera ID generato
        testPiatto = new Piatto(
            0, 
            "Test Piatto", 
            true, 
            "10.00", 
            "Nessuno", 
            "FotoTest", 
            "Test Menu", 
            testRistorante.getIdRistorante()
        );
        
        PiattoDAO.getInstance().aggiungiPiatto(testPiatto);
        
        // Recupera l'ID del piatto inserito
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT idPiatto FROM Piatto WHERE nome = ? AND idRistorante = ?")) {
            stmt.setString(1, testPiatto.getNome());
            stmt.setInt(2, testRistorante.getIdRistorante());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                testPiatto.setIdPiatto(rs.getInt("idPiatto"));
            }
        }
        
        // 5. Inserisci carrello
        testCarrello = new Carrello(
            0, 
            2, 
            testPiatto.getIdPiatto(), 
            testCliente.getEmail()
        );
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Carrello (quantitaPiatti, idPiatto, emailUtente) VALUES (?, ?, ?)")) {
            stmt.setInt(1, testCarrello.getQuantitaPiatti());
            stmt.setInt(2, testCarrello.getIdPiatto());
            stmt.setString(3, testCarrello.getEmailUtente());
            stmt.executeUpdate();
        }
    }

    @After
    public void tearDown() {
        clearAllData();
    }

    private void clearAllData() {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().execute("PRAGMA foreign_keys = OFF");
            conn.createStatement().executeUpdate("DELETE FROM Carrello");
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

    @Test
    public void testGetCarrelloByUtente_Existing() throws SQLException {
        List<Carrello> carrello = CarrelloDAO.getInstance().getCarrelloByUtente(testCliente.getEmail());
        assertFalse(carrello.isEmpty());
        assertEquals(2, carrello.get(0).getQuantitaPiatti());
        assertEquals(testPiatto.getIdPiatto(), carrello.get(0).getIdPiatto());
    }

    /*@Test
    public void testRimuoviDalCarrello_Success() throws SQLException {
        // Recupera l'idCarrello generato
        int idCarrello = -1;
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT idCarrello FROM Carrello WHERE emailUtente = ? AND idPiatto = ?")) {
            stmt.setString(1, testCliente.getEmail());
            stmt.setInt(2, testPiatto.getIdPiatto());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idCarrello = rs.getInt("idCarrello");
            }
        }
        
        assertTrue(CarrelloDAO.getInstance().rimuoviDalCarrello(idCarrello));
        List<Carrello> carrello = CarrelloDAO.getInstance().getCarrelloByUtente(testCliente.getEmail());
        assertTrue(carrello.isEmpty());
    }
*/
    @Test
    public void testSvuotaCarrello_Success() throws SQLException {
        CarrelloDAO.getInstance().svuotaCarrello(testCliente.getEmail());
        List<Carrello> carrello = CarrelloDAO.getInstance().getCarrelloByUtente(testCliente.getEmail());
        assertTrue(carrello.isEmpty());
    }

    @Test
    public void testCalcolaCostoTotale() {
        double costo = CarrelloDAO.getInstance().calcolaCostoTotale(testCliente.getEmail());
        assertEquals(20.0, costo, 0.001); // 2 * 10.00 = 20.00
    }

    @Test
    public void testGetNomePiattoById_Existing() throws SQLException {
        String nome = CarrelloDAO.getInstance().getNomePiattoById(testPiatto.getIdPiatto());
        assertEquals("Test Piatto", nome);
    }

    /*@Test
    public void testAggiornaQuantita_Success() throws SQLException {
        // Recupera l'idCarrello generato
        int idCarrello = -1;
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT idCarrello FROM Carrello WHERE emailUtente = ? AND idPiatto = ?")) {
            stmt.setString(1, testCliente.getEmail());
            stmt.setInt(2, testPiatto.getIdPiatto());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idCarrello = rs.getInt("idCarrello");
            }
        }
        
        assertTrue(CarrelloDAO.getInstance().aggiornaQuantita(idCarrello, 5));
        List<Carrello> carrello = CarrelloDAO.getInstance().getCarrelloByUtente(testCliente.getEmail());
        assertEquals(5, carrello.get(0).getQuantitaPiatti());
    }*/
}