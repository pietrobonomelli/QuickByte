package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.*;
import model.*;
import sessione.SessioneRistorante;
import sessione.SessioneUtente;
import utilities.Utilities;
import java.sql.SQLException;
import gui.main.*;
import com.pavlobu.emojitextflow.EmojiTextFlow;

public class MainScreenTitolare extends VBox {

    private String email;
    private TableView<Ristorante> tableView;
    
    public MainScreenTitolare() {
        super(10);
        this.email = SessioneUtente.getEmail();
        this.setStyle("-fx-padding: 10;");

        Label titolo = new Label("Gestione Ristoranti");
        titolo.getStyleClass().add("title");
        
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        setupTable();

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button inserisciRistoranteButton = new Button("Inserisci nuovo Ristorante");
        inserisciRistoranteButton.setOnAction(e -> switchToInserisciRistorante());

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> switchToLoginScreen());
        logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        buttonContainer.getChildren().addAll(inserisciRistoranteButton, logoutButton);

        // Aggiungi gli elementi nell'ordine corretto: Titolo -> Tabella -> Pulsanti
        this.getChildren().addAll(titolo, tableView, buttonContainer);

        loadRistoranti(); // Carica i ristoranti
    }

    private void setupTable() {
        // Crea la colonna per il nome del ristorante
        TableColumn<Ristorante, String> colNome = new TableColumn<>("Gestisci menu ed ordinazioni");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        // Crea la colonna per il pulsante "Visualizza Menu"
        TableColumn<Ristorante, Void> colVisualizzaMenu = new TableColumn<>("Gestisci menu ed ordinazioni");
        colVisualizzaMenu.setCellFactory(param -> new TableCell<Ristorante, Void>() {
        	private final EmojiTextFlow emojiTextFlow0 = new EmojiTextFlow();
            private final Button visualizzaMenuButton = new Button("");
            {
            	emojiTextFlow0.parseAndAppend(":clipboard:");
            	visualizzaMenuButton.setGraphic(emojiTextFlow0);

            	visualizzaMenuButton.getStyleClass().add("table-button-emoji");
                visualizzaMenuButton.setOnAction(event -> {
                    Ristorante ristorante = getTableView().getItems().get(getIndex());
                    switchToMenuTitolare(ristorante.getIdRistorante());  
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(visualizzaMenuButton);
                }
            }
        });

                
        // Crea la colonna per il pulsante per gestire il ristorante
        TableColumn<Ristorante, Void> colModifica = new TableColumn<>("Modifica");
        colModifica.setCellFactory(param -> new TableCell<Ristorante, Void>() {
        	private final EmojiTextFlow emojiTextFlow1 = new EmojiTextFlow();
            private final Button modificaButton = new Button();
            {            	
            	emojiTextFlow1.parseAndAppend(":pencil:");
                modificaButton.setGraphic(emojiTextFlow1);

            	modificaButton.getStyleClass().add("table-button-emoji");
                modificaButton.setOnAction(event -> {
                    Ristorante ristorante = getTableView().getItems().get(getIndex());
                    switchToModificaRistorante(ristorante.getNome());  
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

        // Crea la colonna per il pulsante "Elimina"
        TableColumn<Ristorante, Void> colElimina = new TableColumn<>("Elimina");
        colElimina.setCellFactory(param -> new TableCell<Ristorante, Void>() {
        	private final EmojiTextFlow emojiTextFlow2 = new EmojiTextFlow();
            private final Button eliminaButton = new Button();
            {
            	emojiTextFlow2.parseAndAppend(":wastebasket:");
            	eliminaButton.setGraphic(emojiTextFlow2);
            	eliminaButton.getStyleClass().add("table-button-emoji");
                eliminaButton.setOnAction(event -> {
                    Ristorante ristorante = getTableView().getItems().get(getIndex());
                    confermaEliminazione(ristorante);
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

        // Aggiungi le colonne alla TableView
        tableView.getColumns().addAll(colNome, colVisualizzaMenu, colModifica, colElimina);
    }

    private void loadRistoranti() {
        try {
            ObservableList<Ristorante> ristoranti = FXCollections.observableArrayList(RistoranteDAO.getInstance().getRistorantiByEmail(this.email));
            tableView.setItems(ristoranti);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante il caricamento dei ristoranti.");
        }
    }

    private void switchToInserisciRistorante() {
        this.getScene().setRoot(new InserisciRistorante());
    }

    private void switchToModificaRistorante(String nomeRistorante) {
        this.getScene().setRoot(new ModificaRistorante(nomeRistorante));
    }

    private void switchToMenuTitolare(int idRistorante) {
        SessioneRistorante.setId(idRistorante); // Imposta l'id del ristorante nella sessione
        this.getScene().setRoot(new MenuTitolare());
    }

    private void switchToLoginScreen() {
        this.getScene().setRoot(new LoginScreen());
    }

    private void confermaEliminazione(Ristorante ristorante) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Stai per eliminare " + ristorante.getNome());
        alert.setContentText("Sei sicuro?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                eliminaRistorante(ristorante);
            }
        });
    }

    private void eliminaRistorante(Ristorante ristorante) {
        try {
            RistoranteDAO.getInstance().rimuoviRistorante(ristorante.getIdRistorante());
            Utilities.showAlert("Successo", "Ristorante eliminato con successo.");
            loadRistoranti(); // Ricarica la lista dei ristoranti dopo la cancellazione
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'eliminazione del ristorante.");
        }
    }
}