package gui.main;

import javafx.scene.control.Label;
import sessione.SessioneUtente;

public class MainScreenCorriere extends MainScreen {
    
    public MainScreenCorriere() {
        super();
        Label welcomeLabel = new Label("Benvenuto Corriere: " + SessioneUtente.getEmail());
        welcomeLabel.getStyleClass().add("title");

        // Aggiungi qui elementi specifici per il corriere, es. lista ordini disponibili

        this.getChildren().add(welcomeLabel);
    }
}
