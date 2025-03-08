package daoTest;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;
import org.junit.*;
import dao.MenuDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import database.DatabaseManager;
import model.Menu;
import model.Titolare;

public class MenuDAOTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:src/main/resources/database_embedded.db";
    private final Titolare testTitolare = new Titolare("test@titolare.com", "password", "Test Titolare", "123456789");
    private final int TEST_RISTORANTE_ID = 1;
    private final String TEST_MENU_NAME = "Test Menu";

    @BeforeClass
    public static void initDatabase() throws Exception {
        DatabaseConnection.setDatabaseUrl(TEST_DB_URL);
        new DatabaseManager();
        DatabaseManager.createTables();
    }

    @Before
    public void setupDatabase() throws SQLException {
 
        UtenteDAO.getInstance().insertUtente(testTitolare, "Titolare");
        
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO Ristorante (idRistorante, nome, telefono, indirizzo, emailTitolare) " +
                "VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, TEST_RISTORANTE_ID);
            stmt.setString(2, "Ristorante Test");
            stmt.setString(3, "123456789");
            stmt.setString(4, "Via Test");
            stmt.setString(5, testTitolare.getEmail());
            stmt.executeUpdate();
        }
        
      
        Menu testMenu = new Menu(TEST_MENU_NAME, TEST_RISTORANTE_ID);
        MenuDAO.getInstance().aggiungiMenu(testMenu);
    }

    @After
    public void tearDown() {
        try (Connection conn = DatabaseConnection.connect()) {
   
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM Menu WHERE nome = ? AND idRistorante = ?")) {
                stmt.setString(1, TEST_MENU_NAME);
                stmt.setInt(2, TEST_RISTORANTE_ID);
                stmt.executeUpdate();
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM Ristorante WHERE idRistorante = ?")) {
                stmt.setInt(1, TEST_RISTORANTE_ID);
                stmt.executeUpdate();
            }
            
            UtenteDAO.getInstance().deleteUtente(testTitolare.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAggiungiMenu_Success() throws SQLException {
        Menu newMenu = new Menu("Nuovo Menu", TEST_RISTORANTE_ID);
        MenuDAO.getInstance().aggiungiMenu(newMenu);
        
        List<Menu> menus = MenuDAO.getInstance().getMenuByRistorante(TEST_RISTORANTE_ID);
        assertTrue("Il nuovo menu dovrebbe essere presente", 
                   menus.stream().anyMatch(m -> m.getNome().equals("Nuovo Menu")));
        
        MenuDAO.getInstance().rimuoviMenu("Nuovo Menu", TEST_RISTORANTE_ID);
    }

    @Test
    public void testGetMenuByRistorante_Existing() throws SQLException {
        List<Menu> menus = MenuDAO.getInstance().getMenuByRistorante(TEST_RISTORANTE_ID);
        assertFalse("Dovrebbero esserci menu per questo ristorante", menus.isEmpty());
        assertTrue("Il menu di test dovrebbe essere presente", 
                   menus.stream().anyMatch(m -> m.getNome().equals(TEST_MENU_NAME)));
    }

    @Test
    public void testRimuoviMenu_Success() throws SQLException {
        MenuDAO.getInstance().rimuoviMenu(TEST_MENU_NAME, TEST_RISTORANTE_ID);
        List<Menu> menus = MenuDAO.getInstance().getMenuByRistorante(TEST_RISTORANTE_ID);
        assertFalse("Il menu rimosso non dovrebbe essere presente", 
                    menus.stream().anyMatch(m -> m.getNome().equals(TEST_MENU_NAME)));
    }

    @Test
    public void testGetNomeRistorante_Existing() throws SQLException {
        String nomeRistorante = MenuDAO.getInstance().getNomeRistorante(TEST_RISTORANTE_ID);
        assertEquals("Il nome del ristorante dovrebbe corrispondere", 
                     "Test Risto", nomeRistorante);
    }

    @Test(expected = SQLException.class)
    public void testAggiungiMenu_Duplicate() throws SQLException {
        Menu duplicateMenu = new Menu(TEST_MENU_NAME, TEST_RISTORANTE_ID);
        MenuDAO.getInstance().aggiungiMenu(duplicateMenu);
    }

    @Test
    public void testGetMenuByRistorante_NonExisting() throws SQLException {
        List<Menu> menus = MenuDAO.getInstance().getMenuByRistorante(999);
        assertTrue("Non dovrebbero esserci menu per questo ristorante", menus.isEmpty());
    }
}