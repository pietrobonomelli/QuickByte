package gui.corriere;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.OrdineDAO;
import model.Ordine;
import model.StatoOrdine;
import sessione.SessioneUtente;
import utilities.Utilities;
import gui.main.LoginScreen;

import java.util.Arrays;
import java.util.List;

public class MainScreenCorriere extends VBox {

    private TableView<Ordine> tabellaOrdini;
    private TableView<Ordine> tabellaOrdiniPassati;

    public MainScreenCorriere() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        Label titoloLabel = Utilities.createLabel("Ordini Disponibili: ", "title");

        // Inizializza la tabella degli ordini
        tabellaOrdini = creaTabellaOrdini();
        caricaOrdini();

        // Inizializza la tabella degli ordini presi in carico
        tabellaOrdiniPassati = creaTabellaOrdiniPassati();
        caricaOrdiniPassati();

        // Titolo per la tabella ordini presi in carico
        Label titoloOrdiniPassati = Utilities.createLabel("Ordini Presi in Carico", "title");
        Button bottoneLogout = Utilities.createButtonLogout("Logout", this::passaASchermataLogin);

        // Aggiungi tutte le componenti nella scena
        this.getChildren().addAll(titoloLabel, tabellaOrdini, titoloOrdiniPassati, tabellaOrdiniPassati, bottoneLogout);
    }

    /**
     * Crea e configura la tabella degli ordini disponibili.
     */
    private TableView<Ordine> creaTabellaOrdini() {
        TableView<Ordine> tabella = new TableView<>();
        tabella.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colonnaId = new TableColumn<>("ID Ordine");
        colonnaId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

        TableColumn<Ordine, String> colonnaRistorante = new TableColumn<>("Ristorante");
        colonnaRistorante.setCellValueFactory(data -> {
            Ordine ordine = data.getValue();
            String nomeRistorante = OrdineDAO.getInstance().getNomeRistorante(ordine);
            return new SimpleStringProperty(nomeRistorante);
        });

        TableColumn<Ordine, Double> colonnaCosto = new TableColumn<>("Costo (€)");
        colonnaCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colonnaStato = new TableColumn<>("Stato");
        colonnaStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

        TableColumn<Ordine, String> colonnaEmail = new TableColumn<>("Email cliente");
        colonnaEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));

        TableColumn<Ordine, String> colonnaData = new TableColumn<>("Data");
        colonnaData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedDataOraOrdine()));

        TableColumn<Ordine, String> colonnaIndirizzo = new TableColumn<>("Indirizzo");
        colonnaIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ordine, Void> colonnaAzione = new TableColumn<>("Azione");
        colonnaAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button bottoneAccetta = Utilities.createButton("ACCETTA", () -> {
                Ordine ordine = getTableView().getItems().get(getIndex());
                accettaOrdine(ordine);
            });

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(bottoneAccetta);
                }
            }
        });

        tabella.getColumns().addAll(colonnaId, colonnaRistorante, colonnaCosto, colonnaStato, colonnaEmail, colonnaIndirizzo, colonnaData, colonnaAzione);
        return tabella;
    }

    /**
     * Crea e configura la tabella degli ordini presi in carico.
     */
    private TableView<Ordine> creaTabellaOrdiniPassati() {
        TableView<Ordine> tabella = new TableView<>();
        tabella.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colonnaId = new TableColumn<>("ID Ordine");
        colonnaId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

        TableColumn<Ordine, String> colonnaRistorante = new TableColumn<>("Ristorante");
        colonnaRistorante.setCellValueFactory(data -> {
            Ordine ordine = data.getValue();
            String nomeRistorante = OrdineDAO.getInstance().getNomeRistorante(ordine);
            return new SimpleStringProperty(nomeRistorante);
        });

        TableColumn<Ordine, Double> colonnaCosto = new TableColumn<>("Costo (€)");
        colonnaCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colonnaStato = new TableColumn<>("Stato");
        colonnaStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

        TableColumn<Ordine, String> colonnaEmail = new TableColumn<>("Email cliente");
        colonnaEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));

        TableColumn<Ordine, String> colonnaData = new TableColumn<>("Data");
        colonnaData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedDataOraOrdine()));

        TableColumn<Ordine, String> colonnaIndirizzo = new TableColumn<>("Indirizzo");
        colonnaIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ordine, Void> colonnaAzione = new TableColumn<>("Azione");
        colonnaAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button bottoneConsegnato = Utilities.createButton("SEGNA COME CONSEGNATO", () -> {
                Ordine ordine = getTableView().getItems().get(getIndex());
                segnaOrdineComeConsegnato(ordine);
            });

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()).getStato() != StatoOrdine.IN_CONSEGNA) {
                    setGraphic(null);
                } else {
                    setGraphic(bottoneConsegnato);
                }
            }
        });

        tabella.getColumns().addAll(colonnaId, colonnaRistorante, colonnaCosto, colonnaStato, colonnaEmail, colonnaIndirizzo, colonnaData, colonnaAzione);
        return tabella;
    }

    /**
     * Carica gli ordini disponibili nella tabella.
     */
    private void caricaOrdini() {
        List<Ordine> listaOrdini = OrdineDAO.getInstance().getOrdiniByStato(StatoOrdine.ACCETTATO.name());
        ObservableList<Ordine> ordini = FXCollections.observableArrayList(listaOrdini);
        tabellaOrdini.setItems(ordini);
    }

    /**
     * Carica gli ordini presi in carico nella tabella.
     */
    private void caricaOrdiniPassati() {
        List<Ordine> listaOrdiniPassati = OrdineDAO.getInstance().getOrdiniPresiInCarico(
                SessioneUtente.getEmail(),
                Arrays.asList(StatoOrdine.IN_CONSEGNA.name(), StatoOrdine.CONSEGNATO.name(), StatoOrdine.ELIMINATO.name())
        );

        ObservableList<Ordine> ordini = FXCollections.observableArrayList(listaOrdiniPassati);
        tabellaOrdiniPassati.setItems(ordini);
    }

    /**
     * Accetta un ordine e aggiorna il suo stato.
     */
    private void accettaOrdine(Ordine ordine) {
        String emailCorriere = SessioneUtente.getEmail();

        if (emailCorriere == null || emailCorriere.isEmpty()) {
            Utilities.showAlert("Errore", "Errore nel recupero dell'email del corriere.");
            return;
        }

        boolean statoAggiornato = OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.IN_CONSEGNA.name());
        boolean emailAggiornata = OrdineDAO.getInstance().aggiornaEmailCorriereOrdine(ordine.getIdOrdine(), emailCorriere);

        if (statoAggiornato && emailAggiornata) {
            Utilities.showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());

            // Aggiorna le tabelle
            tabellaOrdiniPassati.getItems().clear();
            caricaOrdiniPassati();
            caricaOrdini();
        } else {
            Utilities.showAlert("Errore", "Non è stato possibile accettare l'ordine. Riprova.");
        }
    }

    /**
     * Segna un ordine come consegnato.
     */
    private void segnaOrdineComeConsegnato(Ordine ordine) {
        String emailCorriere = SessioneUtente.getEmail();

        if (emailCorriere == null || emailCorriere.isEmpty()) {
            Utilities.showAlert("Errore", "Errore nel recupero dell'email del corriere.");
            return;
        }

        boolean statoAggiornato = OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.CONSEGNATO.name());

        if (statoAggiornato) {
            Utilities.showAlert("Successo", "Hai consegnato l'ordine " + ordine.getIdOrdine());
            caricaOrdiniPassati();
        } else {
            Utilities.showAlert("Errore", "Non è stato possibile segnare l'ordine come consegnato. Riprova.");
        }
    }

    /**
     * Passa alla schermata di login.
     */
    private void passaASchermataLogin() {
        LoginScreen loginScreen = new LoginScreen();
        this.getScene().setRoot(loginScreen);
    }
}
