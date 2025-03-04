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

      
        stmt.execute("DROP TABLE IF EXISTS Ristorante");

        
        stmt.execute("CREATE TABLE Ristorante (idRistorante INT PRIMARY KEY, emailTitolare VARCHAR(255))");

     
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (1, 'test@example.com')");
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (2, 'test@example.com')");
        stmt.execute("INSERT INTO Ristorante (idRistorante, emailTitolare) VALUES (3, 'altro@example.com')");
    }


    @Test
    public void testGetRistorantiByEmail() throws SQLException {
  
        SessioneUtente.setEmail("test@example.com");

        List<Integer> ristoranti = TitolareDAO.getInstance().getRistorantiByEmail();

        assertEquals("Il numero di ristoranti restituiti non è corretto.", 2, ristoranti.size());
        assertTrue("Il ristorante con ID 1 non è stato trovato.", ristoranti.contains(1));
        assertTrue("Il ristorante con ID 2 non è stato trovato.", ristoranti.contains(2));
        assertFalse("Il ristorante con ID 3 non dovrebbe essere incluso.", ristoranti.contains(3));
    }

    @Test
    public void testNoRistorantiForOtherUser() throws SQLException {
    
        SessioneUtente.setEmail("altro@example.com");

       
        List<Integer> ristoranti = TitolareDAO.getInstance().getRistorantiByEmail();

       
        assertEquals("Il numero di ristoranti restituiti per altro@example.com non è corretto.", 1, ristoranti.size());
        assertTrue("Il ristorante con ID 3 non è stato trovato.", ristoranti.contains(3));
    }

    @After
    public void tearDownDatabase() throws Exception {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE Ristorante");
        conn.close();
    }
}
