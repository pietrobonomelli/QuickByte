package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import gui.main.*;

import java.sql.*;

public class MainScreenTitolare extends VBox {

    private String email;
    private VBox container;

    public MainScreenTitolare() {
        super(10);

        this.email = SessioneUtente.getEmail();

        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadRistoranti();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button inserisciRistoranteButton = new Button("Inserisci nuovo Ristorante");
        inserisciRistoranteButton.setOnAction(e -> switchToInserisciRistorante());
        buttonContainer.getChildren().add(inserisciRistoranteButton);

        this.getChildren().add(buttonContainer);
    }

    private void loadRistoranti() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT idRistorante, nome FROM Ristorante WHERE emailTitolare = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, this.email);
                ResultSet rs = stmt.executeQuery();
                ObservableList<String> ristoranti = FXCollections.observableArrayList();
                while (rs.next()) {
                    ristoranti.add(rs.getString("nome"));
                }

                for (String ristorante : ristoranti) {
                    HBox ristoranteBox = new HBox(10);
                    ristoranteBox.setStyle("-fx-padding: 10;");
                    Label nomeRistorante = new Label(ristorante);

                    nomeRistorante.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            setRistoranteInSessione(ristorante);
                            switchToMenuTitolare();
                        }
                    });

                    MenuButton menuButton = new MenuButton("...");
                    MenuItem modificaItem = new MenuItem("Modifica");
                    MenuItem eliminaItem = new MenuItem("Elimina");

                    modificaItem.setOnAction(e -> switchToModificaRistorante(ristorante));
                    eliminaItem.setOnAction(e -> confermaEliminazione(ristorante));

                    menuButton.getItems().addAll(modificaItem, eliminaItem);

                    ristoranteBox.getChildren().addAll(nomeRistorante, menuButton);
                    container.getChildren().add(ristoranteBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void setRistoranteInSessione(String nomeRistorante) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT idRistorante FROM Ristorante WHERE nome = ? AND emailTitolare = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeRistorante);
                stmt.setString(2, this.email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int idRistorante = rs.getInt("idRistorante");
                    SessioneRistorante.setId(idRistorante); // Imposta l'ID del ristorante in sessione
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void switchToMenuTitolare() {
        MenuTitolare menuTitolareScreen = new MenuTitolare(); // Ora la schermata MenuTitolare prende l'ID dal sessione
        this.getScene().setRoot(menuTitolareScreen);
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

    private void switchToInserisciRistorante() {
        InserisciRistorante inserisciRistoranteScreen = new InserisciRistorante();
        this.getScene().setRoot(inserisciRistoranteScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
