package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneMenu;
import sessione.SessioneRistorante;
import javafx.scene.input.MouseButton;
import database.DatabaseConnection;
import dao.MenuDAO;
import model.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

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
            MenuDAO menuDAO = new MenuDAO(conn);
            List<Menu> menuList = menuDAO.getMenuByRistorante(idRistorante);
            
            if (menuList.isEmpty()) {
                showAlert("Info", "Nessun menu disponibile.");
            }

            ObservableList<String> menuNames = FXCollections.observableArrayList();
            for (Menu menu : menuList) {
                menuNames.add(menu.getNome());
            }

            // Crea una lista di Label per ogni menu
            for (String nomeMenu : menuNames) {
                Label menuLabel = new Label(nomeMenu);
                menuLabel.setStyle("-fx-padding: 10; -fx-font-size: 14px;");

                // Gestisci il click sulla label
                menuLabel.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        // Salva il nome del menu selezionato in SessioneMenu
                        SessioneMenu.setNome(nomeMenu);
                        // Passa alla schermata PiattiCliente
                        try {
							switchToPiattiCliente();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                });

                // Aggiungi la Label all'interfaccia
                container.getChildren().add(menuLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei menu.");
        }

        Button carrelloButton = new Button("Vai al Carrello");
        carrelloButton.setOnAction(event -> switchToCarrello());
        this.getChildren().add(carrelloButton);

        // Aggiungi il pulsante "Torna alla lista dei ristoranti"
        Button tornaAllaListaRistorantiButton = new Button("Torna alla lista dei ristoranti");
        tornaAllaListaRistorantiButton.setOnAction(event -> tornaAllaListaRistoranti());
        container.getChildren().add(tornaAllaListaRistorantiButton);
    }

    private void switchToPiattiCliente() throws SQLException {
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
    
    private void switchToCarrello() {
        CarrelloView carrelloScreen = new CarrelloView();
        this.getScene().setRoot(carrelloScreen);
    }
}
