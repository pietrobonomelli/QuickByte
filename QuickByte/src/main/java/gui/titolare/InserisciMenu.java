package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import database.DatabaseConnection;
import java.sql.*;
import gui.main.*;

public class InserisciMenu extends VBox {
    private TextField nomeMenuField;
    private Label nomeRistoranteLabel;
    private int idRistorante;

    public InserisciMenu() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        this.idRistorante = SessioneRistorante.getId();
        
        // Ottieni il nome del ristorante dal database
        String nomeRistorante = getNomeRistorante(idRistorante);
        
        nomeRistoranteLabel = new Label("Ristorante: " + nomeRistorante);
        Label nomeMenuLabel = new Label("Nome Menu:");
        nomeMenuField = new TextField();
        Button inserisciButton = new Button("Inserisci Menu");
        inserisciButton.setOnAction(e -> inserisciMenu());

        Button tornaButton = new Button("Torna ai menu");
        tornaButton.setOnAction(e -> switchToMenuTitolare());

        VBox formContainer = new VBox(10, nomeRistoranteLabel, nomeMenuLabel, nomeMenuField, inserisciButton, tornaButton);
        this.getChildren().add(formContainer);
    }

    private String getNomeRistorante(int idRistorante) {
        String nomeRistorante = "";
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome FROM Ristorante WHERE idRistorante = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idRistorante);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nomeRistorante = rs.getString("nome");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomeRistorante;
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
                stmt.setInt(2, idRistorante); // Inserisci l'ID del ristorante
                stmt.executeUpdate();
                showAlert("Successo", "Menu inserito correttamente.");
                nomeMenuField.clear();
                switchToMenuTitolare(); // Torna alla schermata MenuTitolare dopo l'inserimento
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'inserimento del menu.");
        }
    }

    private void switchToMenuTitolare() {
        MenuTitolare menuTitolareScreen = new MenuTitolare(); // Passa l'ID del ristorante
        this.getScene().setRoot(menuTitolareScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
