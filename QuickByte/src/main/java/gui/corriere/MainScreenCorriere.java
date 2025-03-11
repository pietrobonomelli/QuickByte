package gui.corriere;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.LoginDAO;
import dao.OrdineDAO;
import model.Ordine;
import model.StatoOrdine;
import sessione.SessioneUtente;
import utilities.Utilities;
import gui.main.LoginScreen;

import java.util.Arrays;
import java.util.List;

public class MainScreenCorriere extends VBox {

    private TableView<Ordine> table;
    private TableView<Ordine> tablePassati;

    public MainScreenCorriere() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        Label titleLabel = new Label("Ordini Disponibili");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Tabella ordini
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colId = new TableColumn<>("ID Ordine");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());
        
        TableColumn<Ordine, String> colRistorante = new TableColumn<>("Ristorante");
        colRistorante.setCellValueFactory(data -> {
            Ordine ordine = data.getValue();
            String nomeRistorante = OrdineDAO.getInstance().getNomeRistorante(ordine);
            return new SimpleStringProperty(nomeRistorante);
        });

        TableColumn<Ordine, Double> colCosto = new TableColumn<>("Costo (€)");
        colCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

        TableColumn<Ordine, String> colEmail = new TableColumn<>("Email cliente");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));

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
            private final Button accettaButton = new Button("ACCETTA");
            {
                accettaButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    accettaOrdine(ordine);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(accettaButton);
                }
            }
        });

        table.getColumns().addAll(colId, colRistorante, colCosto, colStato, colEmail, colIndirizzo, colData, colAzione);
        loadOrdini();

        // Tabella ordini presi in carico
        tablePassati = new TableView<>();
        tablePassati.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colIdPassato = new TableColumn<>("ID Ordine");
        colIdPassato.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());
        
        TableColumn<Ordine, String> colRistorantePassato = new TableColumn<>("Ristorante");
        colRistorantePassato.setCellValueFactory(data -> {
            Ordine ordine = data.getValue();
            String nomeRistorante = OrdineDAO.getInstance().getNomeRistorante(ordine);
            return new SimpleStringProperty(nomeRistorante);
        });

        TableColumn<Ordine, Double> colCostoPassato = new TableColumn<>("Costo (€)");
        colCostoPassato.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colStatoPassato = new TableColumn<>("Stato");
        colStatoPassato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

        TableColumn<Ordine, String> colEmailPassato = new TableColumn<>("Email cliente");
        colEmailPassato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));

        TableColumn<Ordine, String> colDataPassato = new TableColumn<>("Data");
        colDataPassato.setCellValueFactory(data -> {
            // Usa il metodo getFormattedDataOraOrdine per ottenere la data formattata
            String formattedDate = data.getValue().getFormattedDataOraOrdine();
            return new SimpleStringProperty(formattedDate);
        });

        TableColumn<Ordine, String> colIndirizzoPassato = new TableColumn<>("Indirizzo");
        colIndirizzoPassato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));
        
        TableColumn<Ordine, Void> colAzionePassato = new TableColumn<>("    Azione     ");
        colAzionePassato.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button consegnatoButton = new Button("SEGNA COME CONSEGNATO");
            {
                consegnatoButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    consegnatoOrdine(ordine);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()).getStato() != StatoOrdine.IN_CONSEGNA) {
                    setGraphic(null);
                } else {
                    setGraphic(consegnatoButton);
                }
            }
        });


        tablePassati.getColumns().addAll(colIdPassato, colRistorantePassato, colCostoPassato, colStatoPassato, colEmailPassato, colIndirizzoPassato, colDataPassato, colAzionePassato);
        loadOrdiniPassati();

        // Titolo per la tabella ordini presi in carico
        Label titleOrdiniPassati = new Label("Ordini Presi in Carico");
        titleOrdiniPassati.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> switchToLoginScreen());
        logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        // Aggiungi tutte le componenti nella scena
        this.getChildren().addAll(titleLabel, table, titleOrdiniPassati, tablePassati, logoutButton);
    }

    private void loadOrdini() {
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByStato(StatoOrdine.ACCETTATO.name());
        ObservableList<Ordine> ordini = FXCollections.observableArrayList(ordiniList);
        table.setItems(ordini);
    }

    private void loadOrdiniPassati() {
        List<Ordine> ordiniListPassati = OrdineDAO.getInstance().getOrdiniPresiInCarico(SessioneUtente.getEmail(),
            Arrays.asList(StatoOrdine.IN_CONSEGNA.name(), StatoOrdine.CONSEGNATO.name(), StatoOrdine.ELIMINATO.name())
        );

        ObservableList<Ordine> ordini = FXCollections.observableArrayList(ordiniListPassati);
        tablePassati.setItems(ordini);
    }

    private void accettaOrdine(Ordine ordine) {
        String emailCorriere = SessioneUtente.getEmail(); // Recupera l'email del corriere dalla sessione

        if (emailCorriere == null || emailCorriere.isEmpty()) {
            Utilities.showAlert("Errore", "Errore nel recupero dell'email del corriere.");
            return;
        }

        boolean statoAggiornato = OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.IN_CONSEGNA.name());
        boolean emailAggiornata = OrdineDAO.getInstance().aggiornaEmailCorriereOrdine(ordine.getIdOrdine(), emailCorriere);

        if (statoAggiornato && emailAggiornata) {
            Utilities.showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
            
            // Svuota la tabella degli ordini presi in carico e ricaricala
            tablePassati.getItems().clear();  // Cancella gli ordini presi in carico nella tabella
            loadOrdiniPassati();  // Ricarica gli ordini presi in carico

            loadOrdini(); // Aggiorna la tabella degli ordini
        } else {
            Utilities.showAlert("Errore", "Non è stato possibile accettare l'ordine. Riprova.");
        }
    }
    
    private void consegnatoOrdine(Ordine ordine) {
        String emailCorriere = SessioneUtente.getEmail(); // Recupera l'email del corriere dalla sessione

        if (emailCorriere == null || emailCorriere.isEmpty()) {
            Utilities.showAlert("Errore", "Errore nel recupero dell'email del corriere.");
            return;
        }

        boolean statoAggiornato = OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.CONSEGNATO.name());

        if (statoAggiornato) {
            Utilities.showAlert("Successo", "Hai consegnato l'ordine " + ordine.getIdOrdine());
            
            loadOrdiniPassati();  // Ricarica gli ordini presi in carico
        } else {
            Utilities.showAlert("Errore", "Non è stato possibile accettare l'ordine. Riprova.");
        }
    }

    private void switchToLoginScreen() {
        LoginScreen loginScreen = new LoginScreen();
        this.getScene().setRoot(loginScreen);
    }
}
