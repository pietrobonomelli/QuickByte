package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PopolaDatabase {
	private PopolaDatabase() {}
	
	public static void popolaDatabase() {
	    String insertUtenti = "INSERT INTO Utente (email, password, nome, telefono, tipoUtente) VALUES " +
	            "('cliente@gmail.com', 'cliente', 'Mario Rossi', '1234567890', 'Cliente'), " +
	            "('titolare@gmail.com', 'titolare', 'Luca Bianchi', '0987654321', 'Titolare'), " +
	            "('corriere@gmail.com', 'corriere', 'Anna Verdi', '1122334455', 'Corriere'), " +
	            "('titolare2@gmail.com', 'titolare', 'Giovanni Neri', '2233445566', 'Titolare');";

	    String insertRistoranti = "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES " +
	            "('Pizzeria Bella Napoli', '3331112222', 'Via Roma 10, Napoli', 'titolare@gmail.com'), " +
	            "('Trattoria da Mario', '4445556666', 'Corso Italia 25, Milano', 'titolare2@gmail.com'), " +
	            "('Sushi Yama', '7778889999', 'Piazza Duomo 3, Firenze', 'titolare2@gmail.com');";

	    String insertMenu = "INSERT INTO Menu (nome, idRistorante) VALUES " +
	            "('Pizza Special', 1), " +
	            "('Antipasti', 1), " +
	            "('Pasta & Risotti', 2), " +
	            "('Sushi Rolls', 3), " +
	            "('Nigiri Special', 3);";

	    String insertPiatti = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) VALUES " +
	            "('Margherita', 1, '6.50', 'Glutine, Lattosio', '', 'Pizza Special', 1), " +
	            "('Diavola', 1, '7.50', 'Glutine, Lattosio', '', 'Pizza Special', 1), " +
	            "('Bruschette Miste', 1, '5.00', 'Glutine', '', 'Antipasti', 1), " +
	            "('Risotto ai Funghi', 1, '10.00', 'Lattosio', '', 'Pasta & Risotti', 2), " +
	            "('Carbonara', 1, '9.00', 'Glutine, Lattosio', '', 'Pasta & Risotti', 2), " +
	            "('Philadelphia Roll', 1, '12.00', 'Pesce, Glutine', '', 'Sushi Rolls', 3), " +
	            "('California Roll', 1, '11.00', 'Pesce, Glutine', '', 'Sushi Rolls', 3), " +
	            "('Nigiri Salmone', 1, '8.00', 'Pesce', '', 'Nigiri Special', 3), " +
	            "('Nigiri Tonno', 1, '8.50', 'Pesce', '', 'Nigiri Special', 3), " +
	            "('Nigiri Gambero', 1, '9.00', 'Pesce, Crostacei', '', 'Nigiri Special', 3);";

	    String insertIndirizzi = "INSERT INTO Indirizzo (indirizzo, citta, cap, provincia, emailUtente) VALUES " +
	            "('Via Milano 5', 'Milano', '20121', 'MI', 'cliente@gmail.com'), " +
	            "('Via Napoli 10', 'Napoli', '80100', 'NA', 'titolare@gmail.com'), " +
	            "('Corso Torino 15', 'Torino', '10100', 'TO', 'corriere@gmail.com'), " +
	            "('Piazza Roma 20', 'Roma', '00100', 'RM', 'titolare2@gmail.com');";

	    String insertMetodiPagamento = "INSERT INTO MetodoDiPagamento (nominativo, numeroCarta, scadenza, emailCliente) VALUES " +
	            "('Mario Rossi', '1234567812345678', '12/26', 'cliente@gmail.com'), " +
	            "('Giovanni Neri', '8765432187654321', '06/25', 'titolare2@gmail.com'), " +
	            "('Anna Verdi', '1122334455667788', '03/27', 'corriere@gmail.com');";

	    try (Connection conn = DatabaseConnection.connect();
	         Statement stmt = conn.createStatement()) {
	        stmt.execute("PRAGMA foreign_keys = ON;");
	        stmt.execute(insertUtenti);
	        stmt.execute(insertRistoranti);
	        stmt.execute(insertMenu);
	        stmt.execute(insertPiatti);
	        stmt.execute(insertIndirizzi);
	        stmt.execute(insertMetodiPagamento);
	        System.out.println("Dati inseriti con successo!");
	    } catch (SQLException e) {
	        System.err.println("Errore durante l'inserimento dei dati: " + e.getMessage());
	    }
	}



}
