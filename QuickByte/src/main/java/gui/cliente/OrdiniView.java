package gui.cliente;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.OrdineDAO;
import model.*;
import sessione.SessioneUtente;
import utilities.Utilities;
import java.util.List;

/**
 * Classe che rappresenta la vista degli ordini del cliente.
 */
public class OrdiniView extends VBox {

    private TableView<Ordine> tabellaOrdini;

    /**
     * Costruttore della vista degli ordini del cliente.
     */
    public OrdiniView() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        Label etichettaTitolo = Utilities.createLabel("I Tuoi Ordini", "title");
        etichettaTitolo.getStyleClass().add("title");

        inizializzaTabellaOrdini();
        caricaOrdini();

        Button pulsanteIndietro = Utilities.createButton("⬅ INDIETRO", this::tornaAllaLista);

        this.getChildren().addAll(etichettaTitolo, tabellaOrdini, pulsanteIndietro);
    }

    /**
     * Inizializza la tabella degli ordini.
     */
    private void inizializzaTabellaOrdini() {
        tabellaOrdini = new TableView<>();
        tabellaOrdini.getStyleClass().add("table-view");
        tabellaOrdini.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colonnaId = new TableColumn<>("ID Ordine");
        colonnaId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

        TableColumn<Ordine, Double> colonnaCosto = new TableColumn<>("Costo (€)");
        colonnaCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colonnaStato = new TableColumn<>("Stato");
        colonnaStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

        TableColumn<Ordine, String> colonnaEmailCorriere = new TableColumn<>("Email corriere");
        colonnaEmailCorriere.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCorriere()));

        TableColumn<Ordine, String> colonnaData = new TableColumn<>("Data");
        colonnaData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedDataOraOrdine()));

        TableColumn<Ordine, String> colonnaIndirizzo = new TableColumn<>("Indirizzo");
        colonnaIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ordine, Void> colonnaAzioni = new TableColumn<>("Azione");
        colonnaAzioni.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button pulsanteElimina = Utilities.createButtonEmoji("", ":wastebasket:", () -> {
                Ordine ordine = getTableView().getItems().get(getIndex());
                eliminaOrdine(ordine);
            });

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !getTableView().getItems().get(getIndex()).getStato().equals(StatoOrdine.PENDENTE.name())) {
                    setGraphic(null);
                } else {
                    setGraphic(pulsanteElimina);
                }
            }
        });

        tabellaOrdini.getColumns().addAll(colonnaId, colonnaCosto, colonnaData, colonnaEmailCorriere, colonnaIndirizzo, colonnaStato, colonnaAzioni);
    }

    /**
     * Carica gli ordini nella tabella.
     */
    private void caricaOrdini() {
        List<Ordine> listaOrdini = OrdineDAO.getInstance().getOrdiniByEmailCliente(SessioneUtente.getEmail());
        ObservableList<Ordine> ordini = FXCollections.observableArrayList(listaOrdini);
        tabellaOrdini.setItems(ordini);
    }

    /**
     * Elimina un ordine.
     *
     * @param ordine L'ordine da eliminare.
     */
    private void eliminaOrdine(Ordine ordine) {
        String emailCliente = SessioneUtente.getEmail();

        if (emailCliente == null || emailCliente.isEmpty()) {
            Utilities.showAlert("Errore", "Errore nel recupero dell'email del corriere.");
            return;
        }

        boolean statoAggiornato = OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.ELIMINATO.name());

        if (statoAggiornato) {
            Utilities.showAlert("Successo", "Hai eliminato l'ordine " + ordine.getIdOrdine());
            caricaOrdini();
        } else {
            Utilities.showAlert("Errore", "Non è stato possibile eliminare l'ordine. Riprova.");
        }
    }

    /**
     * Torna alla lista principale.
     */
    private void tornaAllaLista() {
        MainScreenCliente schermataPrincipale = new MainScreenCliente();
        Scene scenaCorrente = this.getScene();
        scenaCorrente.setRoot(schermataPrincipale);
    }
}
