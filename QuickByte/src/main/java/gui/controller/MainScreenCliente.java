package gui.controller;

import javafx.scene.control.Label;

public class MainScreenCliente extends MainScreen {
    
    public MainScreenCliente() {
        super();
        Label welcomeLabel = new Label("Benvenuto Cliente: " + SessioneUtente.getEmail());
        welcomeLabel.getStyleClass().add("title");

        // Aggiungi qui elementi specifici per il cliente, es. elenco ristoranti

        this.getChildren().add(welcomeLabel);
    }
}
