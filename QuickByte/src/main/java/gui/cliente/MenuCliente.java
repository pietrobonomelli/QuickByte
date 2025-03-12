package gui.cliente;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.*;
import utilities.Utilities;
import database.DatabaseConnection;
import dao.MenuDAO;
import model.Menu;
import java.sql.*;
import java.util.List;

public class MenuCliente extends VBox {

    private TableView<Menu> table;
    private String nomeRistorante;

    public MenuCliente() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        try (Connection conn = DatabaseConnection.connect()) {
        	nomeRistorante = MenuDAO.getInstance().getNomeRistorante(SessioneRistorante.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento del nome del ristorante.");
        }

        // Etichetta del titolo
        Label titleLabel = Utilities.createLabel("Menù disponibili del ristorante: " + nomeRistorante, "title");
        titleLabel.getStyleClass().add("title");

        // Tabella per i menu
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Menu, String> colNome = new TableColumn<>("Menù");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Menu, Void> colAzione = new TableColumn<>("Piatti");
        colAzione.setCellFactory(param -> new TableCell<Menu, Void>() {
            private final Button vediPiattiButton = Utilities.createButtonEmoji("", ":pencil:", () -> {
                Menu menu = getTableView().getItems().get(getIndex());
                SessioneMenu.setNome(menu.getNome());
                try {
                    switchToPiattiCliente();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

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

        Button carrelloButton = Utilities.createButton("🛒 CARRELLO", this::switchToCarrello);
        Button tornaAllaListaRistorantiButton = Utilities.createButton("⬅ INDIETRO", this::tornaAllaListaRistoranti);

        HBox buttonBox = new HBox(10, tornaAllaListaRistorantiButton, carrelloButton);
        buttonBox.setSpacing(10);

        this.getChildren().addAll(titleLabel, table, buttonBox);
    }

    private void loadMenu() {
        try (Connection conn = DatabaseConnection.connect()) {
            List<Menu> menuList = MenuDAO.getInstance().getMenuByRistorante(SessioneRistorante.getId());
            ObservableList<Menu> menuData = FXCollections.observableArrayList(menuList);
            table.setItems(menuData);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento dei menu.");
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

    private void switchToCarrello() {
        CarrelloView carrelloScreen = new CarrelloView();
        this.getScene().setRoot(carrelloScreen);
    }
}
