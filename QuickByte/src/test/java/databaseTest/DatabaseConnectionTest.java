package databaseTest;

import org.junit.*;

import database.DatabaseConnection;

import java.sql.*;
import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseConnection.connect();
    }

    @After
    public void tearDown() {
        DatabaseConnection.closeConnection(connection);
    }

    @Test
    public void testConnect() {
        try {
            assertNotNull("La connessione non dovrebbe essere null", connection);
            assertFalse("La connessione non dovrebbe essere chiusa", connection.isClosed());
            assertTrue("La connessione dovrebbe essere valida", connection.isValid(2));
            
        } catch (SQLException e) {
            fail("Eccezione SQL non prevista: " + e.getMessage());
        }
    }

    @Test
    public void testForeignKeysEnabled() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_keys")) {
            
            assertTrue("Il resultset dovrebbe avere un valore", rs.next());
            assertEquals("Le foreign keys dovrebbero essere abilitate", 1, rs.getInt(1));
            
        } catch (SQLException e) {
            fail("Eccezione SQL non prevista: " + e.getMessage());
        }
    }

    @Test
    public void testCloseConnection() {
        try {
            DatabaseConnection.closeConnection(connection);
            assertTrue("La connessione dovrebbe essere chiusa", connection.isClosed());
            
        } catch (SQLException e) {
            fail("Eccezione SQL non prevista: " + e.getMessage());
        }
    }
}
