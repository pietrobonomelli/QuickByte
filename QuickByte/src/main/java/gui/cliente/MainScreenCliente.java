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

/**
 * Classe che rappresenta la schermata principale del cliente.
 */
public class MainScreenCliente extends VBox {

    private TableView<Ristorante> tabellaRistoranti;

    /**
     * Costruttore della schermata principale del cliente.
     */
    public MainScreenCliente() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        svuotaCarrello();

        Label titolo = new Label("Ristoranti Disponibili");
        titolo.getStyleClass().add("title");

        inizializzaTabellaRistoranti();
        caricaRistoranti();

        Button pulsanteLogout = Utilities.createButtonLogout("Logout", this::passaASchermataLogin);
        Button pulsanteProfilo = Utilities.createButton("Modifica Profilo", this::passaASchermataProfilo);
        Button pulsanteOrdini = Utilities.createButton("I TUOI ORDINI", this::passaASchermataOrdini);

        HBox boxPulsanti = new HBox(10, pulsanteLogout, pulsanteProfilo, pulsanteOrdini);
        boxPulsanti.setSpacing(10);

        this.getChildren().addAll(titolo, tabellaRistoranti, boxPulsanti);
    }

    /**
     * Svuota il carrello dell'utente al login.
     */
    private void svuotaCarrello() {
        try {
            CarrelloDAO.getInstance().svuotaCarrello(SessioneUtente.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inizializza la tabella dei ristoranti.
     */
    private void inizializzaTabellaRistoranti() {
        tabellaRistoranti = new TableView<>();
        tabellaRistoranti.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tabellaRistoranti.getStyleClass().add("table-view");

        TableColumn<Ristorante, Integer> colonnaId = new TableColumn<>("ID Ristorante");
        colonnaId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdRistorante()).asObject());

        TableColumn<Ristorante, String> colonnaNome = new TableColumn<>("Nome");
        colonnaNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Ristorante, String> colonnaIndirizzo = new TableColumn<>("Indirizzo");
        colonnaIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ristorante, String> colonnaTelefono = new TableColumn<>("Telefono");
        colonnaTelefono.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelefono()));

        TableColumn<Ristorante, String> colonnaEmail = new TableColumn<>("Email titolare");
        colonnaEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailTitolare()));

        TableColumn<Ristorante, Void> colonnaMenu = new TableColumn<>("Menù");
        colonnaMenu.setCellFactory(param -> new TableCell<Ristorante, Void>() {
            private final Button pulsanteSeleziona = Utilities.createButtonEmoji("", ":fork_knife_plate:", () -> {
                Ristorante ristorante = getTableView().getItems().get(getIndex());
                SessioneRistorante.setId(ristorante.getIdRistorante());
                passaASchermataMenuCliente();
            });

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pulsanteSeleziona);
            }
        });

        tabellaRistoranti.getColumns().addAll(colonnaId, colonnaNome, colonnaIndirizzo, colonnaTelefono, colonnaEmail, colonnaMenu);
    }

    /**
     * Carica i ristoranti nella tabella.
     */
    private void caricaRistoranti() {
        try {
            List<Ristorante> listaRistoranti = RistoranteDAO.getInstance().getRistoranti();
            ObservableList<Ristorante> ristoranti = FXCollections.observableArrayList(listaRistoranti);
            tabellaRistoranti.setItems(ristoranti);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore di connessione al database.");
        }
    }

    /**
     * Passa alla schermata del menù del cliente.
     */
    private void passaASchermataMenuCliente() {
        MenuCliente schermataMenuCliente = new MenuCliente();
        schermataMenuCliente.getStylesheets().add("style/style.css");
        this.getScene().setRoot(schermataMenuCliente);
    }

    /**
     * Passa alla schermata di login.
     */
    private void passaASchermataLogin() {
        LoginScreen schermataLogin = new LoginScreen();
        schermataLogin.getStylesheets().add("style/style.css");
        this.getScene().setRoot(schermataLogin);
    }
    
    /**
     * Passa alla schermata di modifica del profilo.
     */
    private void passaASchermataProfilo() {
        ModificaProfilo schermataProfilo = new ModificaProfilo();
        schermataProfilo.getStylesheets().add("style/style.css");
        this.getScene().setRoot(schermataProfilo);
    }


    /**
     * Passa alla schermata degli ordini del cliente.
     */
    private void passaASchermataOrdini() {
        OrdiniView schermataOrdini = new OrdiniView();
        schermataOrdini.getStylesheets().add("style/style.css");
        this.getScene().setRoot(schermataOrdini);
    }
}
