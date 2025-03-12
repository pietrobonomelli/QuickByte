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

/**
 * Classe che rappresenta la vista del menù del cliente.
 */
public class MenuCliente extends VBox {

    private TableView<Menu> tabellaMenu;
    private String nomeRistorante;

    /**
     * Costruttore della vista del menù del cliente.
     */
    public MenuCliente() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        caricaNomeRistorante();

        Label etichettaTitolo = Utilities.createLabel("Menù disponibili del ristorante: " + nomeRistorante, "title");
        etichettaTitolo.getStyleClass().add("title");

        inizializzaTabellaMenu();
        caricaMenu();

        Button pulsanteCarrello = Utilities.createButton("🛒 CARRELLO", this::passaACarrello);
        Button pulsanteIndietro = Utilities.createButton("⬅ INDIETRO", this::tornaAllaListaRistoranti);

        HBox boxPulsanti = new HBox(10, pulsanteIndietro, pulsanteCarrello);
        boxPulsanti.setSpacing(10);

        this.getChildren().addAll(etichettaTitolo, tabellaMenu, boxPulsanti);
    }

    /**
     * Carica il nome del ristorante.
     */
    private void caricaNomeRistorante() {
        try (Connection conn = DatabaseConnection.connect()) {
            nomeRistorante = MenuDAO.getInstance().getNomeRistorante(SessioneRistorante.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento del nome del ristorante.");
        }
    }

    /**
     * Inizializza la tabella dei menù.
     */
    private void inizializzaTabellaMenu() {
        tabellaMenu = new TableView<>();
        tabellaMenu.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tabellaMenu.getStyleClass().add("table-view");

        TableColumn<Menu, String> colonnaNome = new TableColumn<>("Menù");
        colonnaNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Menu, Void> colonnaAzioni = new TableColumn<>("Piatti");
        colonnaAzioni.setCellFactory(param -> new TableCell<Menu, Void>() {
            private final Button pulsanteVediPiatti = Utilities.createButtonEmoji("", ":pencil:", () -> {
                Menu menu = getTableView().getItems().get(getIndex());
                SessioneMenu.setNome(menu.getNome());
                try {
                    passaAPiattiCliente();
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
                    setGraphic(pulsanteVediPiatti);
                }
            }
        });

        tabellaMenu.getColumns().addAll(colonnaNome, colonnaAzioni);
    }

    /**
     * Carica i menù nella tabella.
     */
    private void caricaMenu() {
        try (Connection conn = DatabaseConnection.connect()) {
            List<Menu> listaMenu = MenuDAO.getInstance().getMenuByRistorante(SessioneRistorante.getId());
            ObservableList<Menu> datiMenu = FXCollections.observableArrayList(listaMenu);
            tabellaMenu.setItems(datiMenu);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento dei menu.");
        }
    }

    /**
     * Passa alla schermata dei piatti del cliente.
     *
     * @throws SQLException Se si verifica un errore SQL.
     */
    private void passaAPiattiCliente() throws SQLException {
        PiattiCliente schermataPiattiCliente = new PiattiCliente();
        this.getScene().setRoot(schermataPiattiCliente);
    }

    /**
     * Torna alla lista dei ristoranti.
     */
    private void tornaAllaListaRistoranti() {
        MainScreenCliente schermataPrincipale = new MainScreenCliente();
        this.getScene().setRoot(schermataPrincipale);
    }

    /**
     * Passa alla schermata del carrello.
     */
    private void passaACarrello() {
        CarrelloView schermataCarrello = new CarrelloView();
        this.getScene().setRoot(schermataCarrello);
    }
}
