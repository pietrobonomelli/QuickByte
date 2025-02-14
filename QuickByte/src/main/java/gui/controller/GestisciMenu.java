package gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import java.sql.*;

public class GestisciMenu extends VBox {

    private VBox container;
    private int ristorante;

    public GestisciMenu(int ristorante) {
        super(10);
        this.ristorante = ristorante;
        System.out.println("Ristorante passato a GestisciMenu: " + ristorante);  // Debug
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadMenu();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button tornaButton = new Button("Torna a gestione ristoranti");
        tornaButton.setOnAction(e -> switchToGestioneRistoranti());
        buttonContainer.getChildren().add(tornaButton);

        Button inserisciMenuButton = new Button("Inserisci Menu");
        inserisciMenuButton.setOnAction(e -> switchToInserisciMenu(ristorante));
        buttonContainer.getChildren().add(inserisciMenuButton);

        this.getChildren().add(buttonContainer);
    }

    private void loadMenu() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome FROM Menu WHERE idRistorante = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, ristorante);
                ResultSet rs = stmt.executeQuery();
                ObservableList<String> menuList = FXCollections.observableArrayList();
                while (rs.next()) {
                    String nomeMenu = rs.getString("nome");
                    HBox menuBox = new HBox(10);
                    menuBox.setStyle("-fx-padding: 10;");
                    Label nomeMenuLabel = new Label(nomeMenu);

                    Button eliminaButton = new Button("Elimina");
                    eliminaButton.setOnAction(e -> confermaEliminazione(nomeMenu));

                    Button modificaButton = new Button("Modifica");
                    modificaButton.setOnAction(e -> switchToModificaMenu(nomeMenu, ristorante));

                    menuBox.getChildren().addAll(nomeMenuLabel, eliminaButton, modificaButton);
                    container.getChildren().add(menuBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void confermaEliminazione(String nomeMenu) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Stai per eliminare un menu");
        alert.setContentText("Sei sicuro?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                eliminaMenu(nomeMenu);
            }
        });
    }

    private void eliminaMenu(String nomeMenu) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "DELETE FROM Menu WHERE nome = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeMenu);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    showAlert("Successo", "Menu eliminato con successo.");
                    container.getChildren().clear();
                    loadMenu();
                } else {
                    showAlert("Errore", "Impossibile eliminare il menu.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del menu.");
        }
    }

    private void switchToModificaMenu(String nomeMenu, int ristorante) {
        ModificaMenu modificaMenuScreen = new ModificaMenu(nomeMenu, ristorante);
        this.getScene().setRoot(modificaMenuScreen);
    }

    private void switchToInserisciMenu(int ristorante) {
        InserisciMenu inserisciMenuScreen = new InserisciMenu(ristorante);
        this.getScene().setRoot(inserisciMenuScreen);
    }

    private void switchToGestioneRistoranti() {
        GestioneRistoranti gestioneRistorantiScreen = new GestioneRistoranti();
        this.getScene().setRoot(gestioneRistorantiScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
