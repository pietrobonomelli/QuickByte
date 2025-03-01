package databaseTest;

import org.junit.*;

import database.DatabaseManager;

import static org.junit.Assert.*;
import java.sql.*;

public class DatabaseManagerTest {
	private Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseManager.connect();
        assertNotNull("Connessione al database fallita!", connection);
        DatabaseManager.createTables();
    }

    @Test
    public void testTabelleCreate() throws SQLException {
        String[] tabelleDaVerificare = {
            "Utente", "Ordine", "Carrello", "MetodoDiPagamento", 
            "Ristorante", "Menu", "Piatto", "DettaglioOrdine", "Indirizzo"
        };

        DatabaseMetaData metaData = connection.getMetaData();

        for (String tabella : tabelleDaVerificare) {
            try (ResultSet rs = metaData.getTables(null, null, tabella, null)) {
                assertTrue("La tabella " + tabella + " non esiste!", rs.next());
            }
        }
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }}
