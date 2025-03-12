package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.SQLException;
import dao.MenuDAO;
import model.Menu;
import sessione.SessioneRistorante;
import utilities.Utilities;

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

        Label titoloLabel = new Label("INSERISCI NUOVO MENU");
        titoloLabel.getStyleClass().add("title");

        nomeRistoranteLabel = new Label("Ristorante: " + nomeRistorante);
        Label nomeMenuLabel = new Label("Nome Menu:");
        nomeMenuField = new TextField();

        Button inserisciButton = Utilities.createButton("Inserisci Menu", this::inserisciMenu);
        Button tornaButton = Utilities.createButton("Torna ai menu", this::switchToMenuTitolare);

        HBox buttonContainer = new HBox(10, inserisciButton, tornaButton);

        VBox formContainer = new VBox(10, titoloLabel, nomeRistoranteLabel, nomeMenuLabel, nomeMenuField, buttonContainer);
        this.getChildren().add(formContainer);
    }

    private String getNomeRistorante(int idRistorante) {
        String nomeRistorante = "";
        try {
            nomeRistorante = MenuDAO.getInstance().getNomeRistorante(idRistorante);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomeRistorante;
    }

    private void inserisciMenu() {
        String nomeMenu = nomeMenuField.getText().trim();
        if (nomeMenu.isEmpty()) {
            Utilities.showAlert("Errore", "Il nome del menu non può essere vuoto.");
            return;
        }

        Menu menu = new Menu(nomeMenu, idRistorante);

        try {
            MenuDAO.getInstance().aggiungiMenu(menu);
            Utilities.showAlert("Successo", "Menu inserito correttamente.");
            nomeMenuField.clear();
            switchToMenuTitolare();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'inserimento del menu.");
        }
    }

    private void switchToMenuTitolare() {
        MenuTitolare menuTitolareScreen = new MenuTitolare(); // Passa l'ID del ristorante
        this.getScene().setRoot(menuTitolareScreen);
    }
}
