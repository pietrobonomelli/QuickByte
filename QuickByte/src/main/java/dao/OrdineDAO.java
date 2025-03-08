package dao;

import database.DatabaseConnection;

import model.*;
import sessione.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdineDAO {	
	private static OrdineDAO instance;
	private Connection connection;

	private OrdineDAO() {
        try {
            this.connection = DatabaseConnection.connect();
            // Enable foreign key constraints for this connection
            try (Statement stmt = this.connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public static OrdineDAO getInstance() {
		if(instance == null) {
			instance = new OrdineDAO();
		}
		return instance;
	}

	// Recuperare un ordine tramite ID
	public Ordine getOrdineById(int idOrdine) {
		String query = "SELECT * FROM Ordine WHERE idOrdine = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, idOrdine);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return creaOrdineDaResultSet(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Recuperare tutti gli ordini
	public List<Ordine> getAllOrdini() {
		List<Ordine> ordini = new ArrayList<>();
		String query = "SELECT * FROM Ordine";

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				ordini.add(creaOrdineDaResultSet(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordini;
	}

	// Aggiornare lo stato di un ordine
	public boolean updateStatoOrdine(int idOrdine, String nuovoStato) {
		String query = "UPDATE Ordine SET stato = ? WHERE idOrdine = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setString(1, nuovoStato);
			stmt.setInt(2, idOrdine);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Eliminare un ordine tramite ID
	public boolean deleteOrdine(int idOrdine) {
		String query = "DELETE FROM Ordine WHERE idOrdine = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setInt(1, idOrdine);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	private Ordine creaOrdineDaResultSet(ResultSet rs) throws SQLException {
		int idOrdine = rs.getInt("idOrdine");
		String stato = rs.getString("stato");
		double costo = rs.getDouble("costo");
		String dataOraOrdine = rs.getString("dataOraOrdine");
		String indirizzo = rs.getString("indirizzo");
		String emailCliente = rs.getString("emailCliente");
		String emailCorriere = rs.getString("emailCorriere");
		int idRistorante = rs.getInt("idRistorante");

		return new Ordine(idOrdine, stato, costo, dataOraOrdine, indirizzo, emailCliente, emailCorriere, idRistorante);
	}


	public List<Ordine> getOrdiniByStato(String stato) {
		List<Ordine> ordini = new ArrayList<>();
		String query = "SELECT * FROM Ordine WHERE stato = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setString(1, stato);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ordini.add(creaOrdineDaResultSet(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordini;
	}
	
	public List<Ordine> getOrdiniByEmailCliente(String emailCliente) {
		List<Ordine> ordini = new ArrayList<>();
		String query = "SELECT * FROM Ordine WHERE emailCliente = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setString(1, emailCliente);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ordini.add(creaOrdineDaResultSet(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordini;
	}

	public List<Ordine> getOrdiniPresiInCarico(String emailUtente, List<String> stati) {
	    List<Ordine> ordini = new ArrayList<>();

	    if (stati == null || stati.isEmpty()) {
	        return ordini;
	    }

	    String placeholders = String.join(",", Collections.nCopies(stati.size(), "?"));
	    String query = "SELECT * FROM Ordine WHERE stato IN (" + placeholders + ") AND emailCorriere = ?";

	    int numStati = 0;
	    
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        for (int i = 0; i < stati.size(); i++) {
	            stmt.setString(i + 1, stati.get(i));
	            numStati++;
	        }
	        
	        stmt.setString(numStati + 1, emailUtente);

	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            ordini.add(new Ordine(
	                rs.getInt("idOrdine"),
	                rs.getString("stato"),
	                rs.getDouble("costo"),
	                rs.getString("emailCliente"),
	                rs.getString("indirizzo"),
	                rs.getString("dataOraOrdine"),
	                rs.getString("emailCorriere"),
	                rs.getInt("idRistorante")
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return ordini;
	}
	
	public List<Ordine> getOrdiniByIdRistorante(int idRistorante) {
		List<Ordine> ordini = new ArrayList<>();
		System.out.println("ID RISTORANTE IN GET ORDINI BY ID: " + idRistorante);

		StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Ordine WHERE idRistorante = ?");

		try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
			stmt.setInt(1, idRistorante);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				System.out.println("Dati ordine: ID=" + rs.getInt("idOrdine") + ", Costo=" + rs.getDouble("costo"));
				Ordine ordine = creaOrdineDaResultSet(rs);
				System.out.println("Ordine creato: " + ordine);
				ordini.add(ordine);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordini;
	}


	// Aggiornare lo stato di un ordine
	public boolean aggiornaStatoOrdine(int idOrdine, String nuovoStato) {
		String query = "UPDATE Ordine SET stato = ? WHERE idOrdine = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, nuovoStato);
			stmt.setInt(2, idOrdine);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Aggiornare l'email del corriere di un ordine  
	public boolean aggiornaEmailCorriereOrdine(int idOrdine, String email) {
	    String query = "UPDATE Ordine SET emailCorriere = ? WHERE idOrdine = ?";
	    try (Connection conn = DatabaseConnection.connect();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, email);
	        stmt.setInt(2, idOrdine);
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	public boolean registraOrdine(String emailUtente, String indirizzo) {
		String insertOrdineSQL = "INSERT INTO Ordine (emailCliente, dataOraOrdine, stato, indirizzo, costo, idRistorante) VALUES (?, ?, ?, ?, ?, ?)";
		String insertDettagliSQL = "INSERT INTO DettaglioOrdine (idOrdine, idPiatto, quantita) SELECT ?, idPiatto, quantitaPiatti FROM Carrello WHERE emailUtente = ?";
		String deleteCarrelloSQL = "DELETE FROM Carrello WHERE emailUtente = ?";

		try {
			double costoTotale = CarrelloDAO.getInstance().calcolaCostoTotale(emailUtente);

			try (PreparedStatement stmt = connection.prepareStatement(insertOrdineSQL, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, emailUtente);
				stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				stmt.setString(3, StatoOrdine.PENDENTE.name());
				stmt.setString(4, indirizzo);
				stmt.setDouble(5, costoTotale);
				stmt.setInt(6, SessioneRistorante.getId());
				stmt.executeUpdate();

				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int idOrdine = generatedKeys.getInt(1);

						try (PreparedStatement carrelloStmt = connection.prepareStatement(insertDettagliSQL)) {
							carrelloStmt.setInt(1, idOrdine);
							carrelloStmt.setString(2, emailUtente);
							carrelloStmt.executeUpdate();
						}

						try (PreparedStatement deleteStmt = connection.prepareStatement(deleteCarrelloSQL)) {
							deleteStmt.setString(1, emailUtente);
							deleteStmt.executeUpdate();
						}

						SessioneCarrello.setPieno(false);
						return true;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public String getNomeRistorante(Ordine ordine) {
	    String nomeRistorante = null;
	    String query = "SELECT nome FROM Ristorante WHERE idRistorante = ?";

	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, ordine.getIdRistorante());

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            nomeRistorante = rs.getString("nome");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return nomeRistorante;
	}



}