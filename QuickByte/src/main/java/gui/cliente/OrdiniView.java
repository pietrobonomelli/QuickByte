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
import model.Ordine;
import model.StatoOrdine;
import sessione.SessioneUtente;
import gui.main.Utilities;
import java.util.List;

public class OrdiniView extends VBox {

    private TableView<Ordine> table;
    private TableView<Ordine> tablePassati;

    public OrdiniView() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        Label titleLabel = new Label("I Tuoi Ordini");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Tabella ordini
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colId = new TableColumn<>("ID Ordine");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

        TableColumn<Ordine, Double> colCosto = new TableColumn<>("Costo (€)");
        colCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato()));

        TableColumn<Ordine, String> colEmail = new TableColumn<>("Email corriere");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCorriere()));

        TableColumn<Ordine, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDataOraOrdine()));

        TableColumn<Ordine, String> colIndirizzo = new TableColumn<>("Indirizzo");
        colIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ordine, Void> colAzione = new TableColumn<>("Azione");
        colAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button eliminaButton = new Button("Elimina");
            {
                eliminaButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    eliminaOrdine(ordine);
                });
            }

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

        table.getColumns().addAll(colId, colCosto, colStato, colEmail, colIndirizzo, colData, colAzione);
        loadOrdini();

        Button tornaAllaListaButton = new Button("Torna indetro");
		tornaAllaListaButton.setOnAction(event -> tornaAllaLista());
       
        // Aggiungi tutte le componenti nella scena
        this.getChildren().addAll(titleLabel, table, tornaAllaListaButton);
    }

    private void loadOrdini() {
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByEmailCliente(SessioneUtente.getEmail());
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
    	MenuCliente mainClienteScreen = new MenuCliente();
		Scene currentScene = this.getScene();
		currentScene.setRoot(mainClienteScreen);
    }
}
