package gui.corriere;

import javafx.scene.control.*;
import gui.main.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.OrdineDAO;
import model.Ordine;
import model.StatoOrdine;
import sessione.SessioneUtente;
import java.sql.SQLException;

public class MainScreenCorriere extends VBox {

    private String email;
    private VBox container;
    private OrdineDAO ordineDAO;

    public MainScreenCorriere() {
        super(10);
        this.email = SessioneUtente.getEmail();

        ordineDAO = new OrdineDAO();

        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadOrdini();
        this.getChildren().add(container);
        
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> switchToLoginScreen());
        logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        this.getChildren().add(logoutButton);
    }

    private void loadOrdini() {
        ObservableList<Ordine> ordini = FXCollections.observableArrayList(
		        ordineDAO.getOrdiniByStato(StatoOrdine.ACCETTATO.name())
		);
		
		for (Ordine ordine : ordini) {
		    HBox ordineBox = new HBox(10);
		    ordineBox.setStyle("-fx-padding: 10;");
		    Label ordineInfo = new Label("Ordine ID: " + ordine.getIdOrdine() + " - Costo: " + ordine.getCosto() + "€");
		    Button accettaButton = new Button("Accetta");
		    
		    accettaButton.setOnAction(e -> accettaOrdine(ordine));
		    
		    ordineBox.getChildren().addAll(ordineInfo, accettaButton);
		    container.getChildren().add(ordineBox);
		}
    }

    private void accettaOrdine(Ordine ordine) {
        ordineDAO.aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.IN_CONSEGNA.name());
		showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
		container.getChildren().clear();
		loadOrdini(); // Ricarica gli ordini disponibili
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