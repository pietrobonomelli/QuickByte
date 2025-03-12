package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.SQLException;
import dao.MenuDAO;
import model.Menu;
import sessione.SessioneRistorante;
import utilities.Utilities;

public class InserisciMenu extends VBox {
    private TextField campoNomeMenu;
    private Label etichettaNomeRistorante;
    private int idRistorante;

    public InserisciMenu() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        this.idRistorante = SessioneRistorante.getId();

        // Ottieni il nome del ristorante utilizzando il DAO
        String nomeRistorante = ottieniNomeRistorante(idRistorante);

        Label etichettaTitolo = new Label("INSERISCI NUOVO MENU");
        etichettaTitolo.getStyleClass().add("title");

        etichettaNomeRistorante = new Label("Ristorante: " + nomeRistorante);
        Label etichettaNomeMenu = new Label("Nome Menu:");
        campoNomeMenu = new TextField();

        Button bottoneInserisci = Utilities.createButton("Inserisci Menu", this::inserisciMenu);
        Button bottoneTorna = Utilities.createButton("Torna ai menu", this::switchToMenuTitolare);

        HBox contenitoreBottoni = new HBox(10, bottoneInserisci, bottoneTorna);

        VBox contenitoreForm = new VBox(10, etichettaTitolo, etichettaNomeRistorante, etichettaNomeMenu, campoNomeMenu, contenitoreBottoni);
        this.getChildren().add(contenitoreForm);
    }

    /**
     * Ottiene il nome del ristorante dall'ID.
     *
     * @param idRistorante L'ID del ristorante.
     * @return Il nome del ristorante.
     */
    private String ottieniNomeRistorante(int idRistorante) {
        String nomeRistorante = "";
        try {
            nomeRistorante = MenuDAO.getInstance().getNomeRistorante(idRistorante);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomeRistorante;
    }

    /**
     * Inserisce un nuovo menu nel database.
     */
    private void inserisciMenu() {
        String nomeMenu = campoNomeMenu.getText().trim();
        if (nomeMenu.isEmpty()) {
            Utilities.showAlert("Errore", "Il nome del menu non può essere vuoto.");
            return;
        }

        Menu menu = new Menu(nomeMenu, idRistorante);

        try {
            MenuDAO.getInstance().aggiungiMenu(menu);
            Utilities.showAlert("Successo", "Menu inserito correttamente.");
            campoNomeMenu.clear();
            switchToMenuTitolare();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'inserimento del menu.");
        }
    }

    /**
     * Passa alla schermata del menu del titolare.
     */
    private void switchToMenuTitolare() {
        MenuTitolare schermataMenuTitolare = new MenuTitolare(); // Passa l'ID del ristorante
        this.getScene().setRoot(schermataMenuTitolare);
    }
}
