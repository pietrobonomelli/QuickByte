package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneMenu;
import sessione.SessionePiatto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import java.sql.*;
import gui.main.*;

public class PiattiTitolare extends VBox {

    private VBox container;
    private String nomeMenu;
    private int idPiatto;

    public PiattiTitolare() {
        super(10);
        this.nomeMenu = SessioneMenu.getNome();
        this.idPiatto = SessionePiatto.getId();
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadPiatti();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button tornaButton = new Button("Torna ai menu");
        tornaButton.setOnAction(e -> switchToMenuTitolare());
        
        Button inserisciPiattoButton = new Button("Inserisci Piatto");
        inserisciPiattoButton.setOnAction(e -> switchToInserisciPiatto());

        buttonContainer.getChildren().addAll(tornaButton, inserisciPiattoButton);
        this.getChildren().add(buttonContainer);
    }

    private void loadPiatti() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome FROM Piatto WHERE nomeMenu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, this.nomeMenu);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String nomePiatto = rs.getString("nome");
                    HBox piattoBox = new HBox(10);
                    piattoBox.setStyle("-fx-padding: 10;");
                    Label nomePiattoLabel = new Label(nomePiatto);
                    
                    MenuButton menuButton = new MenuButton("⋮");
                    MenuItem eliminaItem = new MenuItem("Elimina");
                    eliminaItem.setOnAction(e -> eliminaPiatto(nomePiatto));
                    
                    MenuItem modificaItem = new MenuItem("Modifica");
                    modificaItem.setOnAction(e -> switchToModificaPiatto());
                    
                    menuButton.getItems().addAll(modificaItem, eliminaItem);
                    piattoBox.getChildren().addAll(nomePiattoLabel, menuButton);
                    container.getChildren().add(piattoBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void eliminaPiatto(String nomePiatto) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "DELETE FROM Piatto WHERE idPiatto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPiatto);
                stmt.executeUpdate();
                container.getChildren().clear();
                loadPiatti();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del piatto.");
        }
    }

    private void switchToModificaPiatto() {
        ModificaPiatto modificaPiattoScreen = new ModificaPiatto();
        this.getScene().setRoot(modificaPiattoScreen);
    }

    private void switchToInserisciPiatto() {
        InserisciPiatto inserisciPiattoScreen = new InserisciPiatto();
        this.getScene().setRoot(inserisciPiattoScreen);
    }

    private void switchToMenuTitolare() {
        MenuTitolare MenuScreen = new MenuTitolare(); // Da sostituire con la gestione corretta
        this.getScene().setRoot(MenuScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
