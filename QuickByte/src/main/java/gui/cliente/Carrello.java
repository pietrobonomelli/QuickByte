package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneCarrello;
import sessione.SessioneRistorante;
import sessione.SessioneUtente;
import javafx.scene.Scene;
import database.DatabaseConnection;
import gui.main.*;
import java.sql.*;

public class Carrello extends VBox {
    
    private String emailUtente;
    private int idRistorante;
    
    public Carrello() {
        super(10);
        this.setStyle("-fx-padding: 10;");
        
        this.emailUtente = SessioneUtente.getEmail();
        this.idRistorante = SessioneRistorante.getId();
        
        loadCarrello();      
    }
       
    private void loadCarrello() {
        this.getChildren().clear();
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT C.idPiatto, P.nome, C.quantitaPiatti FROM Carrello C " +
                           "JOIN Piatto P ON C.idPiatto = P.idPiatto WHERE C.emailUtente = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, this.emailUtente);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    int idPiatto = rs.getInt("idPiatto");
                    String nomePiatto = rs.getString("nome");
                    int quantita = rs.getInt("quantitaPiatti");
                    
                    HBox carrelloItem = new HBox(10);
                    carrelloItem.setStyle("-fx-padding: 10;");
                    
                    Label nomeLabel = new Label(nomePiatto + " (x" + quantita + ")");
                    Button rimuoviButton = new Button("Rimuovi");
                    
                    rimuoviButton.setOnAction(event -> rimuoviDalCarrello(idPiatto));
                    
                    carrelloItem.getChildren().addAll(nomeLabel, rimuoviButton);
                    this.getChildren().add(carrelloItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento del carrello.");
        }
        
        Button confermaOrdineButton = new Button("Conferma Ordine");
        confermaOrdineButton.setOnAction(event -> confermaOrdine());
        this.getChildren().add(confermaOrdineButton);
        
        Button tornaAllaListaButton = new Button("Torna indetro");
        tornaAllaListaButton.setOnAction(event -> tornaAllaLista());
        this.getChildren().add(tornaAllaListaButton);
    }
    
    private void rimuoviDalCarrello(int idPiatto) {
        String sql = "DELETE FROM Carrello WHERE idPiatto = ? AND emailUtente = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPiatto);
            pstmt.setString(2, this.emailUtente);
            pstmt.executeUpdate();
            
            loadCarrello();
            
        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
        }
    }
    
    private void confermaOrdine() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Recupera i metodi di pagamento salvati
            String getCarteSQL = "SELECT numeroCarta, nominativo FROM MetodoDiPagamento WHERE emailCliente = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(getCarteSQL)) {
                pstmt.setString(1, this.emailUtente);
                ResultSet rs = pstmt.executeQuery();

                ChoiceDialog<String> sceltaCarta = new ChoiceDialog<>("Aggiungi metodo di pagamento");
                while (rs.next()) {
                    String carta = rs.getString("numeroCarta") + " - " + rs.getString("nominativo");
                    sceltaCarta.getItems().add(carta);
                }

                sceltaCarta.setTitle("Metodo di Pagamento");
                sceltaCarta.setHeaderText("Seleziona un metodo di pagamento:");
                sceltaCarta.setContentText("Metodo di pagamento:");

                sceltaCarta.showAndWait().ifPresent(cartaSelezionata -> {
                    if (cartaSelezionata.equals("Aggiungi metodo di pagamento")) {
                        this.getChildren().setAll(new MetodoDiPagamentoForm());
                    } else {
                        selezionaIndirizzo(cartaSelezionata);  // Passa il metodo di pagamento selezionato alla selezione dell'indirizzo
                    }
                });
            }
        } catch (SQLException e) {
            showAlert("Errore", "Errore nel recupero dei metodi di pagamento.");
            e.printStackTrace();
        }
    }

    private void selezionaIndirizzo(String metodoPagamento) {
        try (Connection conn = DatabaseConnection.connect()) {
            // Recupera gli indirizzi salvati
            String getIndirizziSQL = "SELECT indirizzo FROM Indirizzo WHERE emailUtente = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(getIndirizziSQL)) {
                pstmt.setString(1, this.emailUtente);
                ResultSet rs = pstmt.executeQuery();

                ChoiceDialog<String> sceltaIndirizzo = new ChoiceDialog<>("Aggiungi indirizzo");
                while (rs.next()) {
                    sceltaIndirizzo.getItems().add(rs.getString("indirizzo"));
                }

                sceltaIndirizzo.setTitle("Indirizzo di Consegna");
                sceltaIndirizzo.setHeaderText("Seleziona un indirizzo di consegna:");
                sceltaIndirizzo.setContentText("Indirizzo:");

                sceltaIndirizzo.showAndWait().ifPresent(indirizzoSelezionato -> {
                    if (indirizzoSelezionato.equals("Aggiungi indirizzo")) {
                        this.getChildren().setAll(new IndirizzoForm());
                    } else {
                        registraOrdine(indirizzoSelezionato);  // Passa anche il metodo di pagamento alla registrazione dell'ordine
                    }
                });
            }
        } catch (SQLException e) {
            showAlert("Errore", "Errore nel recupero degli indirizzi.");
            e.printStackTrace();
        }
    }

    private void registraOrdine(String indirizzo) {
        try (Connection conn = DatabaseConnection.connect()) {
            double costoTotale = calcolaCostoTotale(conn);
            String insertOrdineSQL = "INSERT INTO Ordine (emailCliente, dataOraOrdine, stato, pagato, indirizzo, costo, idRistorante) VALUES (?, ?, 'Pagato', 1, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertOrdineSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, this.emailUtente);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setString(3, indirizzo);
                stmt.setDouble(4, costoTotale);
                stmt.setInt(5, this.idRistorante);

                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idOrdine = generatedKeys.getInt(1);
                        String insertDettagliSQL = "INSERT INTO DettaglioOrdine (idOrdine, idPiatto, quantita) SELECT ?, idPiatto, quantitaPiatti FROM Carrello WHERE emailUtente = ?";
                        try (PreparedStatement carrelloStmt = conn.prepareStatement(insertDettagliSQL)) {
                            carrelloStmt.setInt(1, idOrdine);
                            carrelloStmt.setString(2, emailUtente);
                            carrelloStmt.executeUpdate();
                        }

                        String deleteCarrelloSQL = "DELETE FROM Carrello WHERE emailUtente = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteCarrelloSQL)) {
                            deleteStmt.setString(1, emailUtente);
                            deleteStmt.executeUpdate();
                        }

                        SessioneCarrello.setPieno(false);
                        showAlert("Ordine Confermato", "Il tuo ordine è stato pagato con successo!");
                        loadCarrello();
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Errore", "Errore durante la conferma dell'ordine.");
            e.printStackTrace();
        }
    }


    // Metodo per calcolare il costo totale dell'ordine
    private double calcolaCostoTotale(Connection conn) throws SQLException {
        double costoTotale = 0.0;

        // Seleziona i piatti e le quantità dal carrello
        String selectCarrelloSQL = "SELECT p.prezzo, c.quantitaPiatti FROM Carrello c JOIN Piatto p ON c.idPiatto = p.idPiatto WHERE c.emailUtente = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectCarrelloSQL)) {
            selectStmt.setString(1, emailUtente);
            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    double prezzo = rs.getDouble("prezzo");
                    int quantita = rs.getInt("quantitaPiatti");
                    costoTotale += prezzo * quantita;  // Aggiungi il costo per ciascun piatto
                }
            }
        }

        return costoTotale;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void tornaAllaLista() {
        MenuCliente mainClienteScreen = new MenuCliente();  // Torna alla schermata dei piatti
        Scene currentScene = this.getScene();
        currentScene.setRoot(mainClienteScreen);
    }
}
