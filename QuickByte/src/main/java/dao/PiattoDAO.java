package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import javafx.scene.control.Alert;
import model.Piatto;
import sessione.SessioneCarrello;

public class PiattoDAO {

	private static PiattoDAO instance;
	private Connection connection;
	private static boolean alertEnabled = true;

	
	
	private PiattoDAO() {
		try {
			this.connection = DatabaseConnection.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static PiattoDAO getInstance() {
		if(instance == null) {
			instance = new PiattoDAO();
		}
		return instance;
	}	   

	// Metodo per ottenere un piatto tramite il suo ID
	public Piatto getPiattoById(int idPiatto) throws SQLException {
		String query = "SELECT * FROM Piatto WHERE idPiatto = ?";
		try (PreparedStatement ps = this.connection.prepareStatement(query)) {
			ps.setInt(1, idPiatto);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Piatto(
							rs.getInt("idPiatto"),
							rs.getString("nome"),
							rs.getInt("disponibile") == 1,
							rs.getString("prezzo"),
							rs.getString("allergeni"),
							rs.getString("foto"),
							rs.getString("nomeMenu"),
							rs.getInt("idRistorante")
							);
				}
			}
		}
		return null;
	}

	// Metodo per aggiungere un piatto
	public void aggiungiPiatto(Piatto piatto) throws SQLException {
	    // Inizia una transazione
	    connection.setAutoCommit(false);
	    
	    try {
	        // Verifica che l'idRistorante esista
	        String checkRistoranteQuery = "SELECT 1 FROM Ristorante WHERE idRistorante = ?";
	        try (PreparedStatement checkPs = connection.prepareStatement(checkRistoranteQuery)) {
	            checkPs.setInt(1, piatto.getIdRistorante());
	            try (ResultSet rs = checkPs.executeQuery()) {
	                if (!rs.next()) {
	                    throw new SQLException("Ristorante con ID " + piatto.getIdRistorante() + " non esistente.");
	                }
	            }
	        }

	        // Inserisci il piatto se l'idRistorante è valido
	        String insertQuery = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu, idRistorante) " +
	                "VALUES (?, ?, ?, ?, ?, ?, ?)";
	        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
	            ps.setString(1, piatto.getNome());
	            ps.setInt(2, piatto.isDisponibile() ? 1 : 0);
	            ps.setString(3, piatto.getPrezzo());
	            ps.setString(4, piatto.getAllergeni());
	            ps.setString(5, piatto.getFoto());
	            ps.setString(6, piatto.getNomeMenu());
	            ps.setInt(7, piatto.getIdRistorante());
	            ps.executeUpdate();
	        }

	        // Commetti la transazione
	        connection.commit();
	    } catch (SQLException e) {
	        // In caso di errore, annulla la transazione
	        connection.rollback();
	        throw new SQLException("Errore durante l'aggiunta del piatto: " + e.getMessage(), e);
	    } finally {
	        // Riattiva la modalità di auto-commit
	        connection.setAutoCommit(true);
	    }
	}




	// Metodo per ottenere tutti i piatti di un menu per un determinato ristorante
	public List<Piatto> getPiattiByMenuAndIdRistorante(String nomeMenu, int idRistorante) throws SQLException {
		String selectQuery = "SELECT * FROM Piatto WHERE nomeMenu = ? AND idRistorante = ?";
		List<Piatto> piatti = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
			ps.setString(1, nomeMenu);
			ps.setInt(2, idRistorante);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int idPiatto = rs.getInt("idPiatto");
					String nome = rs.getString("nome");
					int disponibile = rs.getInt("disponibile");
					boolean isDisponibile = disponibile == 1;
					String prezzo = rs.getString("prezzo");
					String allergeni = rs.getString("allergeni");
					String foto = rs.getString("foto");
					piatti.add(new Piatto(idPiatto, nome, isDisponibile, prezzo, allergeni, foto, nomeMenu, idRistorante));
				}
			}
		}
		return piatti;
	}

	// Metodo per aggiornare un piatto
	public void aggiornaPiatto(Piatto piatto) throws SQLException {
		String updateQuery = "UPDATE Piatto SET disponibile = ?, prezzo = ?, allergeni = ?, foto = ? " +
				"WHERE idPiatto = ?";
		try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
			ps.setInt(1, piatto.isDisponibile() ? 1 : 0);
			ps.setString(2, piatto.getPrezzo());
			ps.setString(3, piatto.getAllergeni());
			ps.setInt(4, piatto.getIdPiatto());
			ps.executeUpdate();
		}
	}

	// Metodo per eliminare un piatto
	public void rimuoviPiatto(int idPiatto) throws SQLException {
		String deleteQuery = "DELETE FROM Piatto WHERE idPiatto = ?";
		try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
			ps.setInt(1, idPiatto);
			ps.executeUpdate();
		}
	}

	// Metodo per aggiungere un piatto al carrello
	public void aggiungiPiattoAlCarrello(int idPiatto, String emailCliente) throws SQLException {
	    // Inizia una transazione
	    connection.setAutoCommit(false);
	    
	    try {
	        String checkClienteQuery = "SELECT 1 FROM Utente WHERE email = ?";
	        try (PreparedStatement checkPs = connection.prepareStatement(checkClienteQuery)) {
	            checkPs.setString(1, emailCliente);
	            try (ResultSet rs = checkPs.executeQuery()) {
	                if (!rs.next()) {
	                    throw new SQLException("Cliente con email " + emailCliente + " non esistente.");
	                }
	            }
	        }

	        String checkCarrelloQuery = "SELECT quantitaPiatti FROM Carrello WHERE idPiatto = ? AND emailUtente = ?";
	        String updateCarrelloQuery = "UPDATE Carrello SET quantitaPiatti = quantitaPiatti + 1 WHERE idPiatto = ? AND emailUtente = ?";
	        String insertCarrelloQuery = "INSERT INTO Carrello (idPiatto, emailUtente, quantitaPiatti) VALUES (?, ?, ?)";
	        
	        try (PreparedStatement checkPs = connection.prepareStatement(checkCarrelloQuery)) {
	            checkPs.setInt(1, idPiatto);
	            checkPs.setString(2, emailCliente);
	            try (ResultSet rs = checkPs.executeQuery()) {
	                if (rs.next()) {
	                    // Il piatto è già presente nel carrello, aggiorniamo la quantità
	                    try (PreparedStatement updatePs = connection.prepareStatement(updateCarrelloQuery)) {
	                        updatePs.setInt(1, idPiatto);
	                        updatePs.setString(2, emailCliente);
	                        updatePs.executeUpdate();
	                    }
	                } else {
	                    // Il piatto non è presente nel carrello, lo inseriamo
	                    try (PreparedStatement insertPs = connection.prepareStatement(insertCarrelloQuery)) {
	                        insertPs.setInt(1, idPiatto);
	                        insertPs.setString(2, emailCliente);
	                        insertPs.setInt(3, 1); // Quantità iniziale di 1
	                        insertPs.executeUpdate();
	                    }
	                }
	            }
	        }
	        
	        // Commetti la transazione
	        connection.commit();
            showAlert("Successo", "Piatto aggiunto al carrello!");
	    } catch (SQLException e) {
	        // In caso di errore, annulla la transazione
	        connection.rollback();
	        throw e;
	    } finally {
	        // Riattiva la modalità di auto-commit
	        connection.setAutoCommit(true);
	    }
	}


	public static void setAlertEnabled(boolean enabled) {
	    alertEnabled = enabled;
	}

	private void showAlert(String title, String message) {
	    if (!alertEnabled) return;
	    Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}

	// Metodo per svuotare il carrello
	public void svuotaCarrello(String emailUtente) throws SQLException {
		String deleteCarrelloQuery = "DELETE FROM Carrello WHERE emailUtente = ?";
		try (PreparedStatement ps = connection.prepareStatement(deleteCarrelloQuery)) {
			ps.setString(1, emailUtente);
			ps.executeUpdate();
			SessioneCarrello.setPieno(false);
			SessioneCarrello.setIdRistorante(0);
		}
	}
	
	// Metodo per aggiornare la foto di un piatto
	public void aggiornaFotoPiatto(int idPiatto, String nomeFoto) throws SQLException {
	    String updateQuery = "UPDATE Piatto SET foto = ? WHERE idPiatto = ?";
	    try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
	        ps.setString(1, nomeFoto);
	        ps.setInt(2, idPiatto);
	        ps.executeUpdate();
	    }
	}
	
	// Metodo per ottenere il nome della foto di un piatto
	public String mostraFoto(int idPiatto) throws SQLException {
	    String query = "SELECT foto FROM Piatto WHERE idPiatto = ?";
	    try (PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setInt(1, idPiatto);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("foto");
	            }
	        }
	    }
	    return null;
	}


}
