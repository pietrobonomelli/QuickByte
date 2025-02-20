package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.RistoranteDAO;
import model.Ristorante;
import sessione.SessioneRistorante;
import sessione.SessioneUtente;
import gui.main.*;
import java.sql.SQLException;

public class MainScreenTitolare extends VBox {

    private String email;
    private VBox container;
    private RistoranteDAO ristoranteDAO;

    public MainScreenTitolare() {
        super(10);
        this.email = SessioneUtente.getEmail();

        try {
            ristoranteDAO = new RistoranteDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante la connessione al database.");
        }

        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadRistoranti(); // Carica inizialmente i ristoranti
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button inserisciRistoranteButton = new Button("Inserisci nuovo Ristorante");
        inserisciRistoranteButton.setOnAction(e -> switchToInserisciRistorante());
        buttonContainer.getChildren().add(inserisciRistoranteButton);

        this.getChildren().add(buttonContainer);
    }

    private void loadRistoranti() {
        try {
            ObservableList<Ristorante> ristoranti = FXCollections.observableArrayList(ristoranteDAO.getRistorantiByEmail(this.email));
            System.out.println("Ristoranti caricati: " + ristoranti.size()); // Debug
            System.out.println("Email in sessione: " + this.email); // Debug
            for (Ristorante ristorante : ristoranti) {
                System.out.println("Ristorante: " + ristorante.getNome()); // Debug
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante il caricamento dei ristoranti.");
        }
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
            loadRistoranti(); // Ricarica la lista dei ristoranti
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del ristorante.");
        }
    }

    private void switchToModificaRistorante(String nomeRistorante) {
        ModificaRistorante modificaRistoranteScreen = new ModificaRistorante(nomeRistorante);
        this.getScene().setRoot(modificaRistoranteScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
