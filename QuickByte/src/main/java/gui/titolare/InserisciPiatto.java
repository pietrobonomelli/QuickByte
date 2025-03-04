package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneRistorante;
import sessione.SessioneMenu;
import database.DatabaseConnection;
import dao.PiattoDAO;
import model.Piatto;

import java.sql.Connection;
import java.sql.SQLException;

public class InserisciPiatto extends VBox {
    private TextField nomeField, prezzoField, allergeniField, fotoField;
    private CheckBox disponibilitaCheckBox;
    private String nomeMenu;
    private int idRistorante;

    public InserisciPiatto() {
        super(10);
        this.nomeMenu = SessioneMenu.getNome();
        this.idRistorante = SessioneRistorante.getId();
        this.setStyle("-fx-padding: 10;");
        
        Label titolo = new Label("Inserisci un nuovo piatto nel menu: " + nomeMenu);
        titolo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label nomeLabel = new Label("Nome del piatto:");
        nomeField = new TextField();
        nomeField.setPromptText("Nome del piatto");
        
        Label prezzoLabel = new Label("Prezzo:");
        prezzoField = new TextField();
        prezzoField.setPromptText("Prezzo");
        
        Label allergeniLabel = new Label("Allergeni:");
        allergeniField = new TextField();
        allergeniField.setPromptText("Allergeni");
        
        disponibilitaCheckBox = new CheckBox("Disponibile");
        
        fotoField = new TextField();
        fotoField.setPromptText("Foto");
        
        Button inserisciButton = new Button("Inserisci Piatto");
        inserisciButton.setOnAction(e -> inserisciPiatto());
        
        Button tornaIndietroButton = new Button("Torna ai Piatti");
        tornaIndietroButton.setOnAction(e -> {
            try {
                tornaAiPiatti();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        
        HBox buttonContainer = new HBox(10, inserisciButton, tornaIndietroButton);
        
        this.getChildren().addAll(titolo, nomeLabel, nomeField, prezzoLabel, prezzoField, allergeniLabel, allergeniField, disponibilitaCheckBox, fotoField, buttonContainer);
    }
    
    private void inserisciPiatto() {
        String nome = nomeField.getText();
        String prezzo = prezzoField.getText();
        String allergeni = allergeniField.getText();
        boolean disponibilita = disponibilitaCheckBox.isSelected();
        String foto = fotoField.getText();
        
        if (nome.isEmpty() || prezzo.isEmpty()) {
            showAlert("Errore", "Nome e prezzo sono obbligatori");
            return;
        }
        
        try (Connection conn = DatabaseConnection.connect()) {
            Piatto piatto = new Piatto(0, nome, disponibilita, prezzo, allergeni, foto, nomeMenu, idRistorante);
            PiattoDAO.getInstance().aggiungiPiatto(piatto);
            tornaAiPiatti();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'inserimento del piatto");
        }
    }
    
    private void tornaAiPiatti() throws SQLException {
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
