package daoTest;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.*;
import dao.TitolareDAO;
import database.DatabaseConnection;
import sessione.SessioneUtente;

public class TitolareDAOTest {

    private Connection conn;

    @Before
    public void setupDatabase() throws Exception {
        conn = DatabaseConnection.connect();
        Statement stmt = conn.createStatement();

        stmt.execute("PRAGMA foreign_keys = OFF");

        stmt.execute("DROP TABLE IF EXISTS Ristorante");
        stmt.execute("DROP TABLE IF EXISTS Utente");

        
        stmt.execute("CREATE TABLE Utente (" +
                "email TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "nome TEXT, " +
                "telefono TEXT, " +
                "tipoUtente TEXT" +
                ");");

        stmt.execute("INSERT INTO Utente (email) VALUES ('test@example.com')");
        stmt.execute("INSERT INTO Utente (email) VALUES ('altro@example.com')");

        stmt.execute("CREATE TABLE Ristorante (" +
                "idRistorante INT PRIMARY KEY, " +
                "emailTitolare VARCHAR(255), " +
                "FOREIGN KEY(emailTitolare) REFERENCES Utente(email)" +
                ");");

        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (1, 'test@example.com')");
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (2, 'test@example.com')");
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (3, 'altro@example.com')");

        stmt.execute("PRAGMA foreign_keys = ON");
    }

    @Test
    public void testGetRistorantiByEmail() throws SQLException {
        SessioneUtente.setEmail("test@example.com");
        List<Integer> ristoranti = TitolareDAO.getInstance().getRistorantiByEmail();
        assertEquals(2, ristoranti.size());
        assertTrue(ristoranti.contains(1));
        assertTrue(ristoranti.contains(2));
        assertFalse(ristoranti.contains(3));
    }

    @Test
    public void testNoRistorantiForOtherUser() throws SQLException {
        SessioneUtente.setEmail("altro@example.com");
        List<Integer> ristoranti = TitolareDAO.getInstance().getRistorantiByEmail();
        assertEquals(1, ristoranti.size());
        assertTrue(ristoranti.contains(3));
    }

    @After
    public void tearDownDatabase() throws Exception {
        Statement stmt = conn.createStatement();
    
        stmt.execute("PRAGMA foreign_keys = OFF");
        stmt.execute("DROP TABLE IF EXISTS Ristorante");
        stmt.execute("DROP TABLE IF EXISTS Utente");
        conn.close();
    }
}