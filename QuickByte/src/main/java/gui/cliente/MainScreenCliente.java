package gui.cliente;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import gui.main.*;
import javafx.scene.layout.*;
import sessione.*;
import utilities.Utilities;
import dao.CarrelloDAO;
import dao.RistoranteDAO;
import model.Ristorante;
import java.sql.SQLException;
import java.util.List;
import com.pavlobu.emojitextflow.EmojiTextFlow;

public class MainScreenCliente extends VBox {

    private TableView<Ristorante> table;

    public MainScreenCliente() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        // Clear the cart on login for convenience
        try {
            CarrelloDAO.getInstance().svuotaCarrello(SessioneUtente.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Title label
        Label titleLabel = new Label("Ristoranti Disponibili");
        titleLabel.getStyleClass().add("title");

        // Create the restaurant table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        TableColumn<Ristorante, Integer> colId = new TableColumn<>("ID Ristorante");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdRistorante()).asObject());

        TableColumn<Ristorante, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Ristorante, String> colIndirizzo = new TableColumn<>("Indirizzo");
        colIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ristorante, String> colTelefono = new TableColumn<>("Telefono");
        colTelefono.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelefono()));

        TableColumn<Ristorante, String> colEmail = new TableColumn<>("Email titolare");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailTitolare()));

        TableColumn<Ristorante, Void> colMenu = new TableColumn<>("Menù");
        colMenu.setCellFactory(param -> new TableCell<Ristorante, Void>() {
            private final Button selezionaButton = Utilities.createButtonEmoji("", ":fork_knife_plate:", () -> {
                Ristorante ristorante = getTableView().getItems().get(getIndex());
                SessioneRistorante.setId(ristorante.getIdRistorante());
                switchToMenuCliente();
            });

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : selezionaButton);
            }
        });

        table.getColumns().addAll(colId, colNome, colIndirizzo, colTelefono, colEmail, colMenu);
        loadRistoranti();

        Button logoutButton = Utilities.createButtonLogout("Logout", this::switchToLoginScreen);
        Button ordiniButton = Utilities.createButton("I TUOI ORDINI", this::switchToOrdiniCliente);

        HBox buttonBox = new HBox(10, logoutButton, ordiniButton);
        buttonBox.setSpacing(10);

        this.getChildren().addAll(titleLabel, table, buttonBox);
    }

    private void loadRistoranti() {
        try {
            List<Ristorante> ristorantiList = RistoranteDAO.getInstance().getRistoranti();
            ObservableList<Ristorante> ristoranti = FXCollections.observableArrayList(ristorantiList);
            table.setItems(ristoranti);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void switchToMenuCliente() {
        MenuCliente menuClienteScreen = new MenuCliente();
        menuClienteScreen.getStylesheets().add("style/style.css");
        this.getScene().setRoot(menuClienteScreen);
    }

    private void switchToLoginScreen() {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.getStylesheets().add("style/style.css");
        this.getScene().setRoot(loginScreen);
    }

    private void switchToOrdiniCliente() {
        OrdiniView ordiniScreen = new OrdiniView();
        ordiniScreen.getStylesheets().add("style/style.css");
        this.getScene().setRoot(ordiniScreen);
    }
}
