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

        Button inserisciRistoranteButton = Utilities.createButton("Inserisci nuovo Ristorante", this::switchToInserisciRistorante);
        Button logoutButton = Utilities.createButtonLogout("Logout", this::switchToLoginScreen);

        buttonContainer.getChildren().addAll(logoutButton, inserisciRistoranteButton);

        // Add elements in the correct order: Title -> Table -> Buttons
        this.getChildren().addAll(titolo, tableView, buttonContainer);

        loadRistoranti(); 
    }

    private void setupTable() {
        // Create column for restaurant name
        TableColumn<Ristorante, String> colNome = new TableColumn<>("Gestisci menu ed ordinazioni");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        // Create column for "Visualizza Menu" button
        TableColumn<Ristorante, Void> colVisualizzaMenu = new TableColumn<>("Gestisci menu ed ordinazioni");
        colVisualizzaMenu.setCellFactory(param -> new TableCell<Ristorante, Void>() {
            private final Button visualizzaMenuButton = Utilities.createButtonEmoji("", ":clipboard:",
                    () -> {
                        Ristorante ristorante = getTableView().getItems().get(getIndex());
                        switchToMenuTitolare(ristorante.getIdRistorante());
                    });

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

        // Create column for "Modifica" button
        TableColumn<Ristorante, Void> colModifica = new TableColumn<>("Modifica");
        colModifica.setCellFactory(param -> new TableCell<Ristorante, Void>() {
            private final Button modificaButton = Utilities.createButtonEmoji("", ":pencil:",
                    () -> {
                        Ristorante ristorante = getTableView().getItems().get(getIndex());
                        switchToModificaRistorante(ristorante.getNome());
                    });

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

        // Create column for "Elimina" button
        TableColumn<Ristorante, Void> colElimina = new TableColumn<>("Elimina");
        colElimina.setCellFactory(param -> new TableCell<Ristorante, Void>() {
            private final Button eliminaButton = Utilities.createButtonEmoji("", ":wastebasket:",
                    () -> {
                        Ristorante ristorante = getTableView().getItems().get(getIndex());
                        confermaEliminazione(ristorante);
                    });

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

        // Add columns to the TableView
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
        SessioneRistorante.setId(idRistorante); // Set the restaurant id in the session
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
            loadRistoranti(); // Reload the list of restaurants after deletion
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'eliminazione del ristorante.");
        }
    }
}
