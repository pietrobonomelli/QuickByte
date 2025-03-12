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

public class OrdiniView extends VBox {

    private TableView<Ordine> table;

    public OrdiniView() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        Label titleLabel = Utilities.createLabel("I Tuoi Ordini", "title");
        titleLabel.getStyleClass().add("title");


        // Tabella ordini
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colId = new TableColumn<>("ID Ordine");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

        TableColumn<Ordine, Double> colCosto = new TableColumn<>("Costo (€)");
        colCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

        TableColumn<Ordine, String> colEmail = new TableColumn<>("Email corriere");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCorriere()));

        TableColumn<Ordine, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> {
            // Usa il metodo getFormattedDataOraOrdine per ottenere la data formattata
            String formattedDate = data.getValue().getFormattedDataOraOrdine();
            return new SimpleStringProperty(formattedDate);
        });

        TableColumn<Ordine, String> colIndirizzo = new TableColumn<>("Indirizzo");
        colIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ordine, Void> colAzione = new TableColumn<>("Azione");
        colAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button eliminaButton = Utilities.createButtonEmoji("", ":wastebasket:", () -> {
                Ordine ordine = getTableView().getItems().get(getIndex());
                eliminaOrdine(ordine);
            });

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !getTableView().getItems().get(getIndex()).getStato().equals(StatoOrdine.PENDENTE.name())) {
                    setGraphic(null);
                } else {
                    setGraphic(eliminaButton);
                }
            }
        });

        table.getColumns().addAll(colId, colCosto, colData, colEmail, colIndirizzo, colStato, colAzione);
        loadOrdini();

        Button tornaAllaListaButton = Utilities.createButton("⬅ INDIETRO", this::tornaAllaLista);

        // Aggiungi tutte le componenti nella scena
        this.getChildren().addAll(titleLabel, table, tornaAllaListaButton);
    }

    private void loadOrdini() {
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByEmailCliente(SessioneUtente.getEmail());
        System.out.println("Numero di ordini caricati: " + ordiniList.size()); // Debug
        ObservableList<Ordine> ordini = FXCollections.observableArrayList(ordiniList);
        table.setItems(ordini);
    }

    private void eliminaOrdine(Ordine ordine) {
        String emailCliente = SessioneUtente.getEmail();

        if (emailCliente == null || emailCliente.isEmpty()) {
            Utilities.showAlert("Errore", "Errore nel recupero dell'email del corriere.");
            return;
        }

        boolean statoAggiornato = OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.ELIMINATO.name());

        if (statoAggiornato) {
            Utilities.showAlert("Successo", "Hai eliminato l'ordine " + ordine.getIdOrdine());

            loadOrdini(); // Aggiorna la tabella degli ordini
        } else {
            Utilities.showAlert("Errore", "Non è stato possibile eliminare l'ordine. Riprova.");
        }
    }

    private void tornaAllaLista() {
        MainScreenCliente mainClienteScreen = new MainScreenCliente();
        Scene currentScene = this.getScene();
        currentScene.setRoot(mainClienteScreen);
    }
}
