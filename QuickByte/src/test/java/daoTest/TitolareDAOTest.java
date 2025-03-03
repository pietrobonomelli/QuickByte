package daoTest;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import dao.TitolareDAO;
import database.DatabaseConnection;
import sessione.SessioneUtente;

public class TitolareDAOTest {

    private Connection conn;

    @Before
    public void setupDatabase() throws Exception {
        // Simuliamo una connessione a un database in-memory H2
        conn = DatabaseConnection.connect();
        Statement stmt = conn.createStatement();

        // Creazione della tabella di test
        stmt.execute("CREATE TABLE Ristorante (idRistorante INT PRIMARY KEY, emailTitolare VARCHAR(255))");

        // Inseriamo alcuni dati di test
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (1, 'test@example.com')");
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (2, 'test@example.com')");
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (3, 'altro@example.com')");
    }

    @Test
    public void testGetRistorantiByEmail() {
        try (MockedStatic<SessioneUtente> mockedSession = Mockito.mockStatic(SessioneUtente.class)) {
            // Simuliamo che l'utente loggato sia "test@example.com"
            mockedSession.when(SessioneUtente::getEmail).thenReturn("test@example.com");

            List<Integer> ristoranti = TitolareDAO.getInstance().getRistorantiByEmail();

            // Verifica che il metodo restituisca esattamente i due ristoranti dell'utente test@example.com
            assertEquals(2, ristoranti.size());
            assertTrue(ristoranti.contains(1));
            assertTrue(ristoranti.contains(2));
        }
    }

    @After
    public void tearDownDatabase() throws Exception {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE Ristorante"); // Pulisce il database di test
        conn.close();
    }
}
