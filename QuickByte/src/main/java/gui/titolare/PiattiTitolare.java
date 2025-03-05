package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.PiattoDAO;
import gui.main.Utilities;
import model.Piatto;
import sessione.SessioneMenu;
import sessione.SessionePiatto;
import sessione.SessioneRistorante;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.List;

public class PiattiTitolare extends VBox {

    private VBox container;
    private String nomeMenu;

    public PiattiTitolare() throws SQLException {
        super(10);
        this.nomeMenu = SessioneMenu.getNome();
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        
        // Titolo in grande
        Label titleLabel = new Label("Piatti");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        container.getChildren().add(titleLabel);
        
        // Carica i piatti
        loadPiatti();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button tornaButton = new Button("Torna ai menu");
        tornaButton.setOnAction(e -> switchToMenuTitolare());
        
        Button inserisciPiattoButton = new Button("Inserisci Piatto");
        inserisciPiattoButton.setOnAction(e -> switchToInserisciPiatto());

        buttonContainer.getChildren().addAll(tornaButton, inserisciPiattoButton);
        this.getChildren().add(buttonContainer);
    }

    private void loadPiatti() {
        try {
            // Ottenere piatti dal database tramite DAO
            List<Piatto> piatti = PiattoDAO.getInstance().getPiattiByMenuAndIdRistorante(nomeMenu, SessioneRistorante.getId());
            
            // Creazione della TableView per i piatti
            TableView<Piatto> tablePiatti = new TableView<>();
            tablePiatti.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            // Creazione delle colonne
            TableColumn<Piatto, String> colNomePiatto = new TableColumn<>("Nome Piatto");
            colNomePiatto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

            TableColumn<Piatto, String> colPrezzoPiatto = new TableColumn<>("Prezzo");
            colPrezzoPiatto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrezzo()));

            TableColumn<Piatto, String> colAllergeni = new TableColumn<>("Allergeni");
            colAllergeni.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAllergeni()));

            TableColumn<Piatto, String> colDisponibile = new TableColumn<>("Disponibilità");
            colDisponibile.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isDisponibile() ? "✅" : "❌"));

            TableColumn<Piatto, Void> colModifica = new TableColumn<>("Modifica");
            colModifica.setCellFactory(param -> new TableCell<Piatto, Void>() {
                private final Button modificaButton = new Button("Modifica");

                {
                    modificaButton.setOnAction(event -> {
                        Piatto piatto = getTableView().getItems().get(getIndex());
                        try {
                            SessionePiatto.setId(piatto.getIdPiatto()); // Salviamo l'ID del piatto selezionato
                            switchToModificaPiatto();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(modificaButton);
                    }
                }
            });

            TableColumn<Piatto, Void> colElimina = new TableColumn<>("Elimina");
            colElimina.setCellFactory(param -> new TableCell<Piatto, Void>() {
                private final Button eliminaButton = new Button("Elimina");

                {
                    eliminaButton.setOnAction(event -> {
                        Piatto piatto = getTableView().getItems().get(getIndex());
                        eliminaPiatto(piatto.getIdPiatto());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(eliminaButton);
                    }
                }
            });

            // Aggiungere le colonne alla TableView
            tablePiatti.getColumns().addAll(colNomePiatto, colPrezzoPiatto, colAllergeni, colDisponibile, colModifica, colElimina);

            // Popolare la tabella con i piatti
            ObservableList<Piatto> piattiList = FXCollections.observableArrayList(piatti);
            tablePiatti.setItems(piattiList);

            // Aggiungere la TableView alla scena
            container.getChildren().add(tablePiatti);

        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void eliminaPiatto(int idPiatto) {
        try {
            PiattoDAO.getInstance().rimuoviPiatto(idPiatto);
            loadPiatti(); // Ricarica i piatti dopo l'eliminazione
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'eliminazione del piatto.");
        }
    }

    private void switchToModificaPiatto() throws SQLException {
        ModificaPiatto modificaPiattoScreen = new ModificaPiatto();
        this.getScene().setRoot(modificaPiattoScreen);
    }

    private void switchToInserisciPiatto() {
        InserisciPiatto inserisciPiattoScreen = new InserisciPiatto();
        this.getScene().setRoot(inserisciPiattoScreen);
    }

    private void switchToMenuTitolare() {
        MenuTitolare MenuScreen = new MenuTitolare();
        this.getScene().setRoot(MenuScreen);
    }
}
