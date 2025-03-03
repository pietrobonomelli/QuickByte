package gui.cliente;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneMenu;
import sessione.SessioneRistorante;
import database.DatabaseConnection;
import dao.MenuDAO;
import model.Menu;
import java.sql.*;
import java.util.List;

public class MenuCliente extends VBox {

    private TableView<Menu> table;

    public MenuCliente() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        Label titleLabel = new Label("Menù disponibili");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Menu, String> colNome = new TableColumn<>("Menù");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Menu, Void> colAzione = new TableColumn<>("Piatti");
        colAzione.setCellFactory(param -> new TableCell<Menu, Void>() {
            private final Button vediPiattiButton = new Button("Vedi Piatti");
            {
                vediPiattiButton.setOnAction(event -> {
                    Menu menu = getTableView().getItems().get(getIndex());
                    SessioneMenu.setNome(menu.getNome());
                    try {
						switchToPiattiCliente();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(vediPiattiButton);
                }
            }
        });

        table.getColumns().addAll(colNome, colAzione);
        loadMenu();

        Button carrelloButton = new Button("Vai al Carrello");
        carrelloButton.setOnAction(event -> switchToCarrello());

        Button tornaAllaListaRistorantiButton = new Button("Torna alla lista dei ristoranti");
        tornaAllaListaRistorantiButton.setOnAction(event -> tornaAllaListaRistoranti());

        this.getChildren().addAll(titleLabel, table, carrelloButton, tornaAllaListaRistorantiButton);
    }

    private void loadMenu() {
        try (Connection conn = DatabaseConnection.connect()) {
            List<Menu> menuList = MenuDAO.getInstance().getMenuByRistorante(SessioneRistorante.getId());
            ObservableList<Menu> menuData = FXCollections.observableArrayList(menuList);
            table.setItems(menuData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei menu.");
        }
    }

    private void switchToPiattiCliente() throws SQLException {
        PiattiCliente piattiClienteScreen = new PiattiCliente();
        this.getScene().setRoot(piattiClienteScreen);
    }

    private void tornaAllaListaRistoranti() {
        MainScreenCliente mainScreenCliente = new MainScreenCliente();
        this.getScene().setRoot(mainScreenCliente);
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
