package dao;

import java.sql.*;
import database.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import sessione.*;

public class TitolareDAO {

	public List<Integer> getRistorantiByEmail() {
	     List<Integer> ristoranti = new ArrayList<>();
	     String titolare = SessioneUtente.getEmail(); // Prendi l'id del titolare dalla sessione

	     String sql = "SELECT idRistorante FROM Ristorante WHERE emailTitolare = ?"; // Esegui la query per trovare tutti i ristoranti

	     try (Connection conn = DatabaseConnection.connect(); // Usa la connessione del tuo DatabaseConnection
	          PreparedStatement stmt = conn.prepareStatement(sql)) {

	         stmt.setString(1, titolare); // Imposta l'id del titolare nella query

	         try (ResultSet rs = stmt.executeQuery()) {
	             while (rs.next()) {
	                 int ristoranteId = rs.getInt("idRistorante");
	                 ristoranti.add(ristoranteId); // Aggiungi ogni id ristorante alla lista
	             }
	         }
	     } catch (SQLException e) {
	         e.printStackTrace(); // Gestisci l'eccezione in modo appropriato
	     }

	     return ristoranti;
	 }

}
