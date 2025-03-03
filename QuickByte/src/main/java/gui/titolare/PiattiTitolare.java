package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneMenu;
import sessione.SessioneRistorante;
import sessione.SessionePiatto;
import dao.PiattoDAO;
import model.Piatto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.List;

public class PiattiTitolare extends VBox {

    private VBox container;
    private String nomeMenu;
    private int idPiatto;

    public PiattiTitolare() throws SQLException {
        super(10);
        this.nomeMenu = SessioneMenu.getNome();
        this.idPiatto = SessionePiatto.getId();
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
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
            // Modifica: ottenere piatti dal database tramite DAO
            List<Piatto> piatti = PiattoDAO.getInstance().getPiattiByMenuAndIdRistorante(nomeMenu, SessioneRistorante.getId());
            
            for (Piatto piatto : piatti) {
                HBox piattoBox = new HBox(10);
                piattoBox.setStyle("-fx-padding: 10;");
                Label nomePiattoLabel = new Label(piatto.getNome());
                
                MenuButton menuButton = new MenuButton("⋮");
                MenuItem eliminaItem = new MenuItem("Elimina");
                eliminaItem.setOnAction(e -> eliminaPiatto(piatto.getIdPiatto()));
                
                MenuItem modificaItem = new MenuItem("Modifica");
                modificaItem.setOnAction(e -> {
					try {
						switchToModificaPiatto();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				});
                
                menuButton.getItems().addAll(modificaItem, eliminaItem);
                piattoBox.getChildren().addAll(nomePiattoLabel, menuButton);
                container.getChildren().add(piattoBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void eliminaPiatto(int idPiatto) {
        try {
            PiattoDAO.getInstance().rimuoviPiatto(idPiatto);
            container.getChildren().clear();
            loadPiatti();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del piatto.");
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
        MenuTitolare MenuScreen = new MenuTitolare(); // Da sostituire con la gestione corretta
        this.getScene().setRoot(MenuScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
