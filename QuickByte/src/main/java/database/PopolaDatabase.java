package database;

import java.sql.Connection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.sql.Statement;

public class PopolaDatabase {
    private PopolaDatabase() {}

    /**
     * Popola il Database. Richiamato in RegisterScreen.
     */
    public static void popolaDatabase() {
        // Hash delle password
        String passwordCliente1 = BCrypt.hashpw("cliente", BCrypt.gensalt());
        String passwordCliente2 = BCrypt.hashpw("cliente", BCrypt.gensalt());
        String passwordTitolare1 = BCrypt.hashpw("titolare", BCrypt.gensalt());
        String passwordTitolare2 = BCrypt.hashpw("titolare", BCrypt.gensalt());
        String passwordCorriere = BCrypt.hashpw("corriere", BCrypt.gensalt());

        String insertUtenti = "INSERT INTO Utente (email, password, nome, telefono, tipoUtente) VALUES " +
                "('cliente1@gmail.com', '" + passwordCliente1 + "', 'Mario Rossi', '1234567890', 'Cliente'), " +
                "('cliente2@gmail.com', '" + passwordCliente2 + "', 'Giuseppe Verdi', '0987654321', 'Cliente'), " +
                "('titolare1@gmail.com', '" + passwordTitolare1 + "', 'Luca Bianchi', '1122334455', 'Titolare'), " +
                "('titolare2@gmail.com', '" + passwordTitolare2 + "', 'Giovanni Neri', '2233445566', 'Titolare'), " +
                "('corriere@gmail.com', '" + passwordCorriere + "', 'Anna Verdi', '3344556677', 'Corriere');";

        String insertRistoranti = "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES " +
                "('Pizzeria Bella Napoli', '3331112222', 'Via Roma 10, Napoli', 'titolare1@gmail.com'), " +
                "('Trattoria da Mario', '4445556666', 'Corso Italia 25, Milano', 'titolare2@gmail.com'), " +
                "('Sushi Yama', '7778889999', 'Piazza Duomo 3, Firenze', 'titolare2@gmail.com'), " +
                "('Osteria del Mare', '5556667777', 'Via Lungomare 45, Genova', 'titolare1@gmail.com');";

        String insertMenu = "INSERT INTO Menu (nome, idRistorante) VALUES " +
                "('Pizza Special', 1), " +
                "('Antipasti', 1), " +
                "('Pasta & Risotti', 2), " +
                "('Sushi Rolls', 3), " +
                "('Nigiri Special', 3), " +
                "('Frutti di Mare', 4);";

        String insertPiatti = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) VALUES " +
                "('Margherita', 1, '6.50', 'Glutine, Lattosio', 'pizza.jpg', 'Pizza Special', 1), " +
                "('Diavola', 1, '7.50', 'Glutine, Lattosio', 'pizza.jpg', 'Pizza Special', 1), " +
                "('Bruschette Miste', 1, '5.00', 'Glutine', 'bruschette.jpg', 'Antipasti', 1), " +
                "('Risotto ai Funghi', 1, '10.00', 'Lattosio', 'risotto.jpg', 'Pasta & Risotti', 2), " +
                "('Carbonara', 1, '9.00', 'Glutine, Lattosio', 'patate.jpg', 'Pasta & Risotti', 2), " +
                "('Philadelphia Roll', 1, '12.00', 'Pesce, Glutine', 'sushi.jpg', 'Sushi Rolls', 3), " +
                "('California Roll', 1, '11.00', 'Pesce, Glutine', 'sushi.jpg', 'Sushi Rolls', 3), " +
                "('Nigiri Salmone', 1, '8.00', 'Pesce', 'sushi.jpg', 'Nigiri Special', 3), " +
                "('Nigiri Tonno', 1, '8.50', 'Pesce', 'sushi.jpg', 'Nigiri Special', 3), " +
                "('Nigiri Gambero', 1, '9.00', 'Pesce, Crostacei', 'sushi.jpg', 'Nigiri Special', 3), " +
                "('Spaghetti alle Vongole', 1, '13.00', 'Glutine, Pesce', 'spaghetti.jpg', 'Frutti di Mare', 4), " +
                "('Fritto Misto', 1, '15.00', 'Pesce, Crostacei', 'fritto.jpg', 'Frutti di Mare', 4);";

        String insertIndirizzi = "INSERT INTO Indirizzo (indirizzo, citta, cap, provincia, emailUtente) VALUES " +
                "('Via Milano 5', 'Milano', '20121', 'MI', 'cliente1@gmail.com'), " +
                "('Via Napoli 10', 'Napoli', '80100', 'NA', 'titolare1@gmail.com'), " +
                "('Corso Torino 15', 'Torino', '10100', 'TO', 'corriere@gmail.com'), " +
                "('Piazza Roma 20', 'Roma', '00100', 'RM', 'titolare2@gmail.com'), " +
                "('Via Venezia 30', 'Venezia', '30100', 'VE', 'cliente2@gmail.com');";

        String insertMetodiPagamento = "INSERT INTO MetodoDiPagamento (nominativo, numeroCarta, scadenza, emailCliente) VALUES " +
                "('Mario Rossi', '1234567812345678', '12/26', 'cliente1@gmail.com'), " +
                "('Giovanni Neri', '8765432187654321', '06/25', 'titolare2@gmail.com'), " +
                "('Anna Verdi', '1122334455667788', '03/27', 'corriere@gmail.com'), " +
                "('Giuseppe Verdi', '9876543210987654', '11/24', 'cliente2@gmail.com');";

        String insertOrdini = "INSERT INTO Ordine (stato, costo, emailCliente, indirizzo, dataOraOrdine, emailCorriere, idRistorante) VALUES " +
                "('PENDENTE', 15.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741177800, NULL, 1), " +
                "('ACCETTATO', 20.50, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741179600, NULL, 1), " +
                "('IN_CONSEGNA', 12.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741181400, 'corriere@gmail.com', 1), " +
                "('CONSEGNATO', 18.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741183200, 'corriere@gmail.com', 1), " +
                "('RIFIUTATO', 22.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741185000, NULL, 1), " +

                "('PENDENTE', 30.00, 'cliente2@gmail.com', 'Via Venezia 30, Venezia', 1741186800, NULL, 2), " +
                "('ACCETTATO', 25.50, 'cliente2@gmail.com', 'Via Venezia 30, Venezia', 1741188600, NULL, 2), " +
                "('IN_CONSEGNA', 19.00, 'cliente2@gmail.com', 'Via Venezia 30, Venezia', 1741190400, 'corriere@gmail.com', 2), " +
                "('CONSEGNATO', 27.00, 'cliente2@gmail.com', 'Via Venezia 30, Venezia', 1741192200, 'corriere@gmail.com', 2), " +
                "('ELIMINATO', 33.00, 'cliente2@gmail.com', 'Via Venezia 30, Venezia', 1741194000, NULL, 2), " +

                "('PENDENTE', 40.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741195800, NULL, 3), " +
                "('ACCETTATO', 35.50, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741197600, NULL, 3), " +
                "('IN_CONSEGNA', 28.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741199400, 'corriere@gmail.com', 3), " +
                "('CONSEGNATO', 32.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741201200, 'corriere@gmail.com', 3), " +
                "('RIFIUTATO', 45.00, 'cliente1@gmail.com', 'Via Milano 5, Milano', 1741203000, NULL, 3);";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(insertUtenti);
            stmt.execute(insertRistoranti);
            stmt.execute(insertMenu);
            stmt.execute(insertPiatti);
            stmt.execute(insertIndirizzi);
            stmt.execute(insertMetodiPagamento);
            stmt.execute(insertOrdini);
            System.out.println("Dati inseriti con successo!");
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento dei dati: " + e.getMessage());
        }
    }
}
