package gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import java.sql.*;

public class GestioneRistoranti extends VBox {

    private VBox container;
    private int idRistorante;
    
    public GestioneRistoranti() {
        super(10);
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadRistoranti();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button tornaButton = new Button("Torna alla pagina principale");
        tornaButton.setOnAction(e -> switchToMainScreen());
        buttonContainer.getChildren().add(tornaButton);

        Button inserisciRistoranteButton = new Button("Inserisci nuovo Ristorante");
        inserisciRistoranteButton.setOnAction(e -> switchToInserisciRistorante());
        buttonContainer.getChildren().add(inserisciRistoranteButton);

        this.getChildren().add(buttonContainer);
    }

    private void loadRistoranti() {
        String emailTitolare = SessioneUtente.getEmail();

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome FROM Ristorante WHERE emailTitolare = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, emailTitolare);
                ResultSet rs = stmt.executeQuery();
                ObservableList<String> ristoranti = FXCollections.observableArrayList();
                while (rs.next()) {
                    ristoranti.add(rs.getString("nome"));
                }

                for (String ristorante : ristoranti) {
                    HBox ristoranteBox = new HBox(10);
                    ristoranteBox.setStyle("-fx-padding: 10;");
                    Label nomeRistorante = new Label(ristorante);

                    Button eliminaButton = new Button("Elimina");
                    eliminaButton.setOnAction(e -> confermaEliminazione(ristorante));

                    Button modificaButton = new Button("Modifica");
                    modificaButton.setOnAction(e -> switchToModificaRistorante(ristorante));
                    
                    Button gestisciMenuButton = new Button("Gestisci Menu");
                    gestisciMenuButton.setOnAction(e -> switchToGestisciMenu(ristorante));

                    ristoranteBox.getChildren().addAll(nomeRistorante, eliminaButton, modificaButton, gestisciMenuButton);
                    container.getChildren().add(ristoranteBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void confermaEliminazione(String ristorante) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Stai per eliminare " + ristorante);
        alert.setContentText("Sei sicuro?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                eliminaRistorante(ristorante);
            }
        });
    }

    private void eliminaRistorante(String ristorante) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "DELETE FROM Ristorante WHERE nome = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, ristorante);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    showAlert("Successo", "Ristorante eliminato con successo.");
                    container.getChildren().clear();
                    loadRistoranti();
                } else {
                    showAlert("Errore", "Impossibile eliminare il ristorante.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del ristorante.");
        }
    }

    private void switchToModificaRistorante(String ristorante) {
        ModificaRistorante modificaRistoranteScreen = new ModificaRistorante(ristorante);
        this.getScene().setRoot(modificaRistoranteScreen);
    }

    private void switchToGestisciMenu(String ristorante) {
        // Recupera l'ID del ristorante dal database
        int id = getRistoranteIdByName(ristorante);
        // Imposta l'ID nella sessione
        SessioneRistorante.setId(id);

        // Crea la schermata per gestire il menu, passando l'ID dalla sessione
        GestisciMenu gestisciMenuScreen = new GestisciMenu(SessioneRistorante.getId());
        this.getScene().setRoot(gestisciMenuScreen);
    }

    private void switchToInserisciRistorante() {
        InserisciRistorante inserisciRistoranteScreen = new InserisciRistorante();
        this.getScene().setRoot(inserisciRistoranteScreen);
    }

    private void switchToMainScreen() {
        MainScreenTitolare mainScreenTitolare = new MainScreenTitolare();
        this.getScene().setRoot(mainScreenTitolare);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private int getRistoranteIdByName(String nome) {
        int id = -1;
        String emailTitolare = SessioneUtente.getEmail();
        
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT idRistorante FROM Ristorante WHERE nome = ? AND emailTitolare = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nome);
                stmt.setString(2, emailTitolare);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    id = rs.getInt("idRistorante");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante il recupero dell'ID del ristorante.");
        }
        return id;
    }

}