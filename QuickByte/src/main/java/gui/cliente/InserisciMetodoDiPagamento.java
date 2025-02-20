package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import database.DatabaseConnection;
import gui.main.*;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InserisciMetodoDiPagamento extends VBox {
    
    private TextField nominativoField;
    private TextField numeroCartaField;
    private TextField scadenzaField;
    private String emailUtente;
    
    public InserisciMetodoDiPagamento() {
        super(10);
        this.setStyle("-fx-padding: 10;");
        
        this.emailUtente = SessioneUtente.getEmail();
        if (emailUtente == null) {
            showAlert("Errore", "Devi effettuare il login per aggiungere un metodo di pagamento.");
            return;
        }
        
        Label titolo = new Label("Aggiungi Metodo di Pagamento");
        nominativoField = new TextField();
        nominativoField.setPromptText("Nome e Cognome sulla carta");
        
        numeroCartaField = new TextField();
        numeroCartaField.setPromptText("Numero della Carta");
        
        scadenzaField = new TextField();
        scadenzaField.setPromptText("Scadenza (MM/YY)");
        
        Button confermaButton = new Button("Aggiungi Metodo");
        confermaButton.setOnAction(event -> aggiungiMetodoDiPagamento());
        
     // Aggiungi un pulsante per tornare alla lista di piatti
        Button tornaAllaListaButton = new Button("Torna indetro");
        tornaAllaListaButton.setOnAction(event -> tornaAllaLista());
        this.getChildren().add(tornaAllaListaButton);
        
        this.getChildren().addAll(titolo, nominativoField, numeroCartaField, scadenzaField, confermaButton, tornaAllaListaButton);
    }
    
    private void tornaAllaLista() {
        MainScreenCliente mainClienteScreen = new MainScreenCliente();  // Torna alla schermata dei piatti
        Scene currentScene = this.getScene();
        currentScene.setRoot(mainClienteScreen);
    }
    
    private void aggiungiMetodoDiPagamento() {
        String nominativo = nominativoField.getText().trim();
        String numeroCarta = numeroCartaField.getText().trim();
        String scadenza = scadenzaField.getText().trim();
        
        if (nominativo.isEmpty() || numeroCarta.isEmpty() || scadenza.isEmpty()) {
            showAlert("Errore", "Tutti i campi sono obbligatori.");
            return;
        }
        
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "INSERT INTO MetodoDiPagamento (nominativo, numeroCarta, scadenza, emailCliente) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nominativo);
                stmt.setString(2, numeroCarta);
                stmt.setString(3, scadenza);
                stmt.setString(4, emailUtente);
                stmt.executeUpdate();
                showAlert("Successo", "Metodo di pagamento aggiunto con successo.");
            }
        } catch (SQLException e) {
            showAlert("Errore", "Errore durante l'inserimento del metodo di pagamento.");
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
