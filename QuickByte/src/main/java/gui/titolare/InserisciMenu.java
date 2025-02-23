package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLException;

import dao.MenuDAO;
import model.Menu;
import database.DatabaseConnection;
import sessione.SessioneRistorante;

public class InserisciMenu extends VBox {
    private TextField nomeMenuField;
    private Label nomeRistoranteLabel;
    private int idRistorante;

    public InserisciMenu() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        this.idRistorante = SessioneRistorante.getId();
        
        // Usa il DAO per ottenere il nome del ristorante
        String nomeRistorante = getNomeRistorante(idRistorante);
        
        nomeRistoranteLabel = new Label("Ristorante: " + nomeRistorante);
        Label nomeMenuLabel = new Label("Nome Menu:");
        nomeMenuField = new TextField();
        Button inserisciButton = new Button("Inserisci Menu");
        inserisciButton.setOnAction(e -> inserisciMenu());

        Button tornaButton = new Button("Torna ai menu");
        tornaButton.setOnAction(e -> switchToMenuTitolare());

        VBox formContainer = new VBox(10, nomeRistoranteLabel, nomeMenuLabel, nomeMenuField, inserisciButton, tornaButton);
        this.getChildren().add(formContainer);
    }

    // Usa il DAO per ottenere il nome del ristorante
    private String getNomeRistorante(int idRistorante) {
        String nomeRistorante = "";
        try {
            MenuDAO menuDAO = new MenuDAO(DatabaseConnection.connect());
            nomeRistorante = menuDAO.getNomeRistorante(idRistorante);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomeRistorante;
    }

    // Usa il DAO per inserire il menu
    private void inserisciMenu() {
        String nomeMenu = nomeMenuField.getText().trim();
        if (nomeMenu.isEmpty()) {
            showAlert("Errore", "Il nome del menu non può essere vuoto.");
            return;
        }

        Menu menu = new Menu(nomeMenu, idRistorante);

        try {
            MenuDAO menuDAO = new MenuDAO(DatabaseConnection.connect());
            menuDAO.aggiungiMenu(menu);
            showAlert("Successo", "Menu inserito correttamente.");
            nomeMenuField.clear();
            switchToMenuTitolare(); // Torna alla schermata MenuTitolare dopo l'inserimento
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'inserimento del menu.");
        }
    }

    private void switchToMenuTitolare() {
        MenuTitolare menuTitolareScreen = new MenuTitolare(); // Passa l'ID del ristorante
        this.getScene().setRoot(menuTitolareScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
