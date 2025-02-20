package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneRistorante;
import javafx.scene.input.MouseButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import gui.main.*;

import java.sql.*;

public class MainScreenCliente extends VBox {

    private VBox container;

    public MainScreenCliente() {
        super(10);
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        this.getChildren().add(container);
        loadRistoranti();
    }

    private void loadRistoranti() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT idRistorante, nome FROM Ristorante";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                ObservableList<String> ristoranti = FXCollections.observableArrayList();
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    int idRistorante = rs.getInt("idRistorante");
                    ristoranti.add(nome);

                    // Aggiungi il ristorante all'interfaccia grafica
                    HBox ristoranteBox = new HBox(10);
                    ristoranteBox.setStyle("-fx-padding: 10;");
                    Label nomeRistorante = new Label(nome);

                    nomeRistorante.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            // Salva l'ID del ristorante in SessioneRistorante
                            SessioneRistorante.setId(idRistorante);
                            switchToMenuCliente();
                        }
                    });

                    ristoranteBox.getChildren().addAll(nomeRistorante);
                    container.getChildren().add(ristoranteBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void switchToMenuCliente() {
        MenuCliente menuClienteScreen = new MenuCliente(); // La schermata MenuCliente prende l'ID del ristorante dalla sessione
        this.getScene().setRoot(menuClienteScreen);
    }
    

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
