package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import gui.main.*;
import java.sql.*;

public class MenuCliente extends VBox {

    private int idRistorante;
    private VBox container;

    public MenuCliente() {
        super(10);

        // Recupero l'ID del ristorante dalla sessione
        this.idRistorante = SessioneRistorante.getId();

        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadMenu();
        this.getChildren().add(container);
    }

    private void loadMenu() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Query per recuperare i menu del ristorante
            String query = "SELECT nome FROM Menu WHERE idRistorante = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, this.idRistorante);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String nomeMenu = rs.getString("nome");

                    // Crea una Label per ogni menu, simile a come si fanno i ristoranti
                    Label menuLabel = new Label(nomeMenu);
                    menuLabel.setStyle("-fx-padding: 10; -fx-font-size: 14px;");

                    // Gestisci il click sulla label
                    menuLabel.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            // Salva il nome del menu selezionato in SessioneMenu
                            SessioneMenu.setNome(nomeMenu);
                            // Passa alla schermata PiattiCliente
                            switchToPiattiCliente();
                        }
                    });

                    // Aggiungi la Label all'interfaccia
                    container.getChildren().add(menuLabel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei menu.");
        }

        // Aggiungi il pulsante "Torna alla lista dei ristoranti"
        Button tornaAllaListaRistorantiButton = new Button("Torna alla lista dei ristoranti");
        tornaAllaListaRistorantiButton.setOnAction(event -> tornaAllaListaRistoranti());

        // Aggiungi il pulsante alla schermata
        container.getChildren().add(tornaAllaListaRistorantiButton);
    }

    private void switchToPiattiCliente() {
        PiattiCliente piattiClienteScreen = new PiattiCliente(); // La schermata PiattiCliente prenderà il nome del menu dalla sessione
        this.getScene().setRoot(piattiClienteScreen);
    }

    private void tornaAllaListaRistoranti() {
        MainScreenCliente mainScreenCliente = new MainScreenCliente();  // MainScreenCliente è la schermata con i ristoranti
        this.getScene().setRoot(mainScreenCliente);    // Cambia la scena per mostrare la lista dei ristoranti
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
