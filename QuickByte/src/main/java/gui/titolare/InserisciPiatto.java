package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import database.DatabaseConnection;
import gui.main.SessioneMenu;
import gui.main.SessioneRistorante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InserisciPiatto extends VBox {
    private TextField nomeField, prezzoField, allergeniField, fotoField;
    private CheckBox disponibilitaCheckBox;
    private Label nomeMenuLabel;
    private String nomeMenu;

    public InserisciPiatto() {
        super(10);
        this.nomeMenu = SessioneMenu.getNome();
        this.setStyle("-fx-padding: 10;");
        
        Label titolo = new Label("Inserisci nuovo piatto");
        nomeField = new TextField();
        nomeField.setPromptText("Nome del piatto");
        
        prezzoField = new TextField();
        prezzoField.setPromptText("Prezzo");
        
        allergeniField = new TextField();
        allergeniField.setPromptText("Allergeni");
        
        disponibilitaCheckBox = new CheckBox("Disponibile");
        
        fotoField = new TextField();
        fotoField.setPromptText("Foto");
        
        nomeMenuLabel = new Label("Menu: " + nomeMenu);
        
        Button inserisciButton = new Button("Inserisci Piatto");
        inserisciButton.setOnAction(e -> inserisciPiatto());
        
        Button tornaIndietroButton = new Button("Torna ai Piatti");
        tornaIndietroButton.setOnAction(e -> tornaAiPiatti());
        
        this.getChildren().addAll(titolo, nomeField, prezzoField, allergeniField, disponibilitaCheckBox, fotoField, nomeMenuLabel, inserisciButton, tornaIndietroButton);
    }
    
    private void inserisciPiatto() {
        String nome = nomeField.getText();
        String prezzo = prezzoField.getText();
        String allergeni = allergeniField.getText();
        int disponibilita = disponibilitaCheckBox.isSelected() ? 1 : 0;
        String foto = fotoField.getText();
        
        if (nome.isEmpty() || prezzo.isEmpty()) {
            showAlert("Errore", "Nome e prezzo sono obbligatori");
            return;
        }
        
        try (Connection conn = DatabaseConnection.connect()) {
            // Esegui l'inserimento senza il controllo duplicato
            String query = "INSERT INTO Piatto (nome, disponibile, prezzo, allergeni, foto, nomeMenu) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nome);
                stmt.setInt(2, disponibilita);
                stmt.setString(3, prezzo);
                stmt.setString(4, allergeni);
                stmt.setString(5, foto);
                stmt.setString(6, nomeMenu);
                stmt.executeUpdate();
                tornaAiPiatti(); // Torna automaticamente ai piatti dopo l'inserimento
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'inserimento del piatto");
        }
    }
    
    private void tornaAiPiatti() {
        PiattiTitolare piattiScreen = new PiattiTitolare();
        this.getScene().setRoot(piattiScreen);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
