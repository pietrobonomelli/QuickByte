package gui.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainScreenTitolare extends MainScreen {
    
    public MainScreenTitolare() {
        super();
        Label welcomeLabel = new Label("Benvenuto Titolare: " + SessioneUtente.getEmail());
        welcomeLabel.getStyleClass().add("title");

        // Crea il bottone "Gestisci Ristoranti"
        Button gestisciRistorantiButton = new Button("Gestisci Ristoranti");
        gestisciRistorantiButton.getStyleClass().add("button");
        
        // Azione per il bottone
        gestisciRistorantiButton.setOnAction(e -> switchToGestioneRistoranti());

        // Aggiungi elementi alla schermata
        this.getChildren().addAll(welcomeLabel, gestisciRistorantiButton);
    }

    private void switchToGestioneRistoranti() {
        GestioneRistoranti gestioneRistorantiScreen = new GestioneRistoranti();
        this.getScene().setRoot(gestioneRistorantiScreen); // Imposta direttamente il VBox
    }

}
