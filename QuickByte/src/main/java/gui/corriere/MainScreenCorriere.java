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
import gui.main.LoginScreen;
import sessione.SessioneUtente;
import java.sql.SQLException;
import java.util.List;

public class MainScreenCorriere extends VBox {

    private String email;
    private TableView<Ordine> table;

    public MainScreenCorriere() {
        super(10);
        this.email = SessioneUtente.getEmail();
        this.setStyle("-fx-padding: 10;");

        Label titleLabel = new Label("Ordini Disponibili");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordine, Integer> colId = new TableColumn<>("ID Ordine");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

        TableColumn<Ordine, Double> colCosto = new TableColumn<>("Costo (€)");
        colCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato()));
        
        TableColumn<Ordine, String> colEmail = new TableColumn<>("Email cliente");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));
        
        TableColumn<Ordine, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDataOraOrdine()));
        
        TableColumn<Ordine, String> colIndirizzo = new TableColumn<>("Indirizzo");
        colIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));

        TableColumn<Ordine, Void> colAzione = new TableColumn<>("Azione");
        colAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button accettaButton = new Button("Accetta");
            private final Button rifiutaButton = new Button("Rifiuta");
            private final HBox buttonContainer = new HBox(5, accettaButton, rifiutaButton); // Spazio tra i pulsanti
            
            {
                accettaButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    accettaOrdine(ordine);
                });

                rifiutaButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    rifiutaOrdine(ordine);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonContainer);
                }
            }
        });


        table.getColumns().addAll(colId, colCosto, colStato, colEmail, colIndirizzo, colData, colAzione);
        loadOrdini();

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> switchToLoginScreen());
        logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        
        this.getChildren().addAll(titleLabel, table, logoutButton);
    }

    private void loadOrdini() {
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByStato(StatoOrdine.ACCETTATO.name());
		ObservableList<Ordine> ordini = FXCollections.observableArrayList(ordiniList);
		table.setItems(ordini);
    }

    private void accettaOrdine(Ordine ordine) {
        OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.IN_CONSEGNA.name());
        showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
        loadOrdini(); // Ricarica gli ordini
    }
    
    private void rifiutaOrdine(Ordine ordine) {
        OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.IN_CONSEGNA.name());	//TODO crea stato RIFIUTATO 
        showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
        loadOrdini(); // Ricarica gli ordini
    }
    
    private void switchToLoginScreen() {
        LoginScreen loginScreen = new LoginScreen();
        this.getScene().setRoot(loginScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}