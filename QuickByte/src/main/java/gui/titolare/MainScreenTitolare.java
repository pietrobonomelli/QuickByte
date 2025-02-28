package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.*;
import model.*;
import sessione.SessioneRistorante;
import sessione.SessioneUtente;
import java.sql.SQLException;
import java.util.List;
import gui.main.*;

public class MainScreenTitolare extends VBox {

    private String email;
    private VBox container;
    private RistoranteDAO ristoranteDAO;
    private OrdineDAO ordineDAO;

    public MainScreenTitolare() {
        super(10);
        this.email = SessioneUtente.getEmail();

        try {
            ristoranteDAO = new RistoranteDAO();
            ordineDAO = new OrdineDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante la connessione al database.");
        }

        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadRistoranti();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button inserisciRistoranteButton = new Button("Inserisci nuovo Ristorante");
        inserisciRistoranteButton.setOnAction(e -> switchToInserisciRistorante());
        buttonContainer.getChildren().add(inserisciRistoranteButton);

        this.getChildren().add(buttonContainer);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> switchToLoginScreen());
        logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        this.getChildren().add(logoutButton);
    }

    private void loadRistoranti() {
        try {
            ObservableList<Ristorante> ristoranti = FXCollections.observableArrayList(ristoranteDAO.getRistorantiByEmail(this.email));
            for (Ristorante ristorante : ristoranti) {
                HBox ristoranteBox = new HBox(10);
                ristoranteBox.setStyle("-fx-padding: 10;");

                Label nomeRistorante = new Label(ristorante.getNome());

                nomeRistorante.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        SessioneRistorante.setId(ristorante.getIdRistorante());
                        switchToMenuTitolare();
                    }
                });

                MenuButton menuButton = new MenuButton("...");
                MenuItem modificaItem = new MenuItem("Modifica");
                MenuItem eliminaItem = new MenuItem("Elimina");

                modificaItem.setOnAction(e -> switchToModificaRistorante(ristorante.getNome()));
                eliminaItem.setOnAction(e -> confermaEliminazione(ristorante));

                menuButton.getItems().addAll(modificaItem, eliminaItem);

                ristoranteBox.getChildren().addAll(nomeRistorante, menuButton);
                container.getChildren().add(ristoranteBox);

                loadOrdiniPerRistorante(ristorante);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante il caricamento dei ristoranti.");
        }
    }

    private void loadOrdiniPerRistorante(Ristorante ristorante) throws SQLException {
    	TitolareDAO titolareDao = new TitolareDAO();
        List<Ordine> ordini = ordineDAO.getOrdiniByIdRistoranti(titolareDao.getRistorantiByEmail());
        VBox ordiniContainer = new VBox(10);
        ordiniContainer.setStyle("-fx-padding: 10;");
        Label ordiniLabel = new Label("Ordini per " + ristorante.getNome());

        ordiniContainer.getChildren().add(ordiniLabel);

        for (Ordine ordine : ordini) {
            if (ordine.getIdRistorante() == ristorante.getIdRistorante()) {
                HBox ordineBox = new HBox(10);
                ordineBox.setStyle("-fx-padding: 10;");
                Label ordineInfo = new Label("Ordine ID: " + ordine.getIdOrdine() + " - Costo: " + ordine.getCosto() + "€");
                Button accettaButton = new Button("Accetta");

                accettaButton.setOnAction(e -> accettaOrdine(ordine));

                ordineBox.getChildren().addAll(ordineInfo, accettaButton);
                ordiniContainer.getChildren().add(ordineBox);
            }
        }

        container.getChildren().add(ordiniContainer);
    }

    private void accettaOrdine(Ordine ordine) {
        ordineDAO.aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.IN_CONSEGNA.name());
        showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
        container.getChildren().clear();
        loadRistoranti();
    }

    private void switchToInserisciRistorante() {
        InserisciRistorante inserisciRistoranteScreen = new InserisciRistorante();
        this.getScene().setRoot(inserisciRistoranteScreen);
    }

    private void switchToMenuTitolare() {
        MenuTitolare menuTitolareScreen = new MenuTitolare();
        this.getScene().setRoot(menuTitolareScreen);
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
            ristoranteDAO.rimuoviRistorante(ristorante.getIdRistorante());
            showAlert("Successo", "Ristorante eliminato con successo.");
            container.getChildren().clear();
            loadRistoranti();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del ristorante.");
        }
    }

    private void switchToModificaRistorante(String nomeRistorante) {
        ModificaRistorante modificaRistoranteScreen = new ModificaRistorante(nomeRistorante);
        this.getScene().setRoot(modificaRistoranteScreen);
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
