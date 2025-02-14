package gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import database.DatabaseConnection;
import java.sql.*;

public class InserisciMenu extends VBox {
    private TextField nomeMenuField;
    private int idRistorante;

    public InserisciMenu(int idRistorante) {
        super(10);
        this.setStyle("-fx-padding: 10;");

        this.idRistorante = idRistorante;
        
        Label nomeMenuLabel = new Label("Nome Menu:");
        nomeMenuField = new TextField();
        Button inserisciButton = new Button("Inserisci Menu");
        inserisciButton.setOnAction(e -> inserisciMenu());
        
        Button tornaButton = new Button("Torna a gestione menu");
        tornaButton.setOnAction(e -> switchToGestisciMenu());

        VBox formContainer = new VBox(10, nomeMenuLabel, nomeMenuField, inserisciButton, tornaButton);
        this.getChildren().add(formContainer);
    }

    private void inserisciMenu() {
        String nomeMenu = nomeMenuField.getText().trim();
        if (nomeMenu.isEmpty()) {
            showAlert("Errore", "Il nome del menu non può essere vuoto.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO Menu (nome, idRistorante) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeMenu);
                stmt.setInt(2, idRistorante); // Inserisci qui l'ID del ristorante
                stmt.executeUpdate();
                showAlert("Successo", "Menu inserito correttamente.");
                nomeMenuField.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'inserimento del menu.");
        }
    }

    private void switchToGestisciMenu() {
        GestisciMenu gestisciMenuScreen = new GestisciMenu(idRistorante); // Da sostituire con il ristorante corretto
        this.getScene().setRoot(gestisciMenuScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
