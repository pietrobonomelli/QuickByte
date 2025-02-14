package gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import java.sql.*;

public class ModificaMenu extends VBox {

    private VBox container;
    private String nomeMenu;
    private int idRistorante;

    public ModificaMenu(String nomeMenu, int idRistorante) {
        super(10);
        this.idRistorante = idRistorante;
        this.nomeMenu = nomeMenu;
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadPiatti();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button tornaButton = new Button("Torna a gestione menu");
        tornaButton.setOnAction(e -> switchToGestisciMenu());
        buttonContainer.getChildren().add(tornaButton);

        this.getChildren().add(buttonContainer);
    }

    private void loadPiatti() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome FROM Piatto WHERE nomeMenu = (SELECT nome FROM Menu WHERE idMenu = ? LIMIT 1)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeMenu);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String nomePiatto = rs.getString("nome");
                    HBox piattoBox = new HBox(10);
                    piattoBox.setStyle("-fx-padding: 10;");
                    Label nomePiattoLabel = new Label(nomePiatto);

                    Button eliminaButton = new Button("Elimina");
                    eliminaButton.setOnAction(e -> eliminaPiatto(nomePiatto));

                    Button modificaButton = new Button("Modifica");
                    modificaButton.setOnAction(e -> switchToModificaPiatto(nomePiatto));

                    piattoBox.getChildren().addAll(nomePiattoLabel, eliminaButton, modificaButton);
                    container.getChildren().add(piattoBox);
                }

                ComboBox<String> piattiComboBox = new ComboBox<>(getListaPiattiDisponibili());
                Button inserisciPiattoButton = new Button("Inserisci Piatto");
                inserisciPiattoButton.setOnAction(e -> switchToInserisciPiatto(piattiComboBox.getValue()));

                HBox inserisciPiattoBox = new HBox(10);
                inserisciPiattoBox.getChildren().addAll(piattiComboBox, inserisciPiattoButton);
                container.getChildren().add(inserisciPiattoBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void eliminaPiatto(String nomePiatto) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "DELETE FROM Piatto WHERE nome = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomePiatto);
                stmt.executeUpdate();
                container.getChildren().clear();
                loadPiatti();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del piatto.");
        }
    }

    private void switchToModificaPiatto(String nomePiatto) {
        ModificaPiatto modificaPiattoScreen = new ModificaPiatto(nomePiatto, nomeMenu, idRistorante);
        this.getScene().setRoot(modificaPiattoScreen);
    }

    private void switchToInserisciPiatto(String nomePiatto) {
        InserisciPiatto inserisciPiattoScreen = new InserisciPiatto(nomePiatto);
        this.getScene().setRoot(inserisciPiattoScreen);
    }

    private ObservableList<String> getListaPiattiDisponibili() {
        ObservableList<String> piattiDisponibili = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT DISTINCT nome FROM Piatto";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    piattiDisponibili.add(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return piattiDisponibili;
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
