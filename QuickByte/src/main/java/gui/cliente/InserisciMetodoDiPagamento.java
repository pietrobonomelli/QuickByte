package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import javafx.scene.Scene;
import java.sql.SQLException;
import dao.MetodoDiPagamentoDAO;
import model.MetodoDiPagamento;

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
        
        Button tornaAllaListaButton = new Button("Torna indetro");
        tornaAllaListaButton.setOnAction(event -> tornaAllaLista());
        
        this.getChildren().addAll(titolo, nominativoField, numeroCartaField, scadenzaField, confermaButton, tornaAllaListaButton);
    }
    
    private void tornaAllaLista() {
        MainScreenCliente mainClienteScreen = new MainScreenCliente();  // Torna alla schermata principale
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

        // Crea un oggetto MetodoDiPagamento con i dati inseriti
        MetodoDiPagamento metodo = new MetodoDiPagamento(nominativo, numeroCarta, scadenza, emailUtente);
        
        try {
            MetodoDiPagamentoDAO metodoDAO = new MetodoDiPagamentoDAO();
            metodoDAO.aggiungiMetodo(metodo);  // Passa l'oggetto MetodoDiPagamento
            showAlert("Successo", "Metodo di pagamento aggiunto con successo.");
        } catch (SQLException e) {
            showAlert("Errore", "Errore di connessione al database.");
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
