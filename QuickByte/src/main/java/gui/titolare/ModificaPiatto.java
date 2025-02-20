package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import sessione.SessioneMenu;
import sessione.SessionePiatto;
import database.DatabaseConnection;
import gui.main.*;
import java.io.File;
import java.sql.*;

public class ModificaPiatto extends VBox {
    private TextField prezzoField;
    private TextField allergeniField;
    private CheckBox disponibilitaCheckBox;
    private Button salvaButton;
    private Button tornaIndietroButton;
    private Button scegliFotoButton;
    private File fotoFile;
    
    private int idPiatto;
    private String nomeMenu;

    public ModificaPiatto() {
        super(10);
        this.setStyle("-fx-padding: 10;");
        this.idPiatto = SessionePiatto.getId();
        this.nomeMenu = SessioneMenu.getNome();
        
        Label titoloLabel = new Label("Modifica Piatto: " + idPiatto);	//TODO METTERE UN GETNOMEPIATTOBYID
        
        Label prezzoLabel = new Label("Prezzo:");
        prezzoField = new TextField();
        
        Label allergeniLabel = new Label("Allergeni:");
        allergeniField = new TextField();
        
        Label disponibilitaLabel = new Label("Disponibile:");
        disponibilitaCheckBox = new CheckBox();
        
        scegliFotoButton = new Button("Scegli Foto");
        scegliFotoButton.setOnAction(e -> scegliFoto());
        
        salvaButton = new Button("Salva Modifiche");
        salvaButton.setOnAction(e -> salvaModifiche());
        
        tornaIndietroButton = new Button("Torna ai Piatti");
        tornaIndietroButton.setOnAction(e -> tornaAiPiatti());
        
        loadPiattoData();
        
        this.getChildren().addAll(titoloLabel, prezzoLabel, prezzoField, allergeniLabel, allergeniField,
                disponibilitaLabel, disponibilitaCheckBox, scegliFotoButton, salvaButton, tornaIndietroButton);
    }

    private void loadPiattoData() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT prezzo, allergeni, disponibile FROM Piatto WHERE idPiatto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPiatto);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    prezzoField.setText(rs.getString("prezzo"));
                    allergeniField.setText(rs.getString("allergeni"));
                    disponibilitaCheckBox.setSelected(rs.getInt("disponibile") == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei dati del piatto.");
        }
    }

    private void salvaModifiche() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "UPDATE Piatto SET prezzo = ?, allergeni = ?, disponibile = ? WHERE idPiatto = ? AND nomeMenu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, prezzoField.getText());
                stmt.setString(2, allergeniField.getText());
                stmt.setInt(3, disponibilitaCheckBox.isSelected() ? 1 : 0);
                stmt.setInt(4, idPiatto);
                stmt.setString(5, nomeMenu);
                stmt.executeUpdate();
                
                showAlert("Successo", "Modifiche salvate correttamente.");
                tornaAiPiatti(); // Ritorna automaticamente alla lista piatti
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel salvataggio delle modifiche.");
        }
    }
    
    private void scegliFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        fotoFile = fileChooser.showOpenDialog(null);
        if (fotoFile != null) {
            showAlert("Foto Selezionata", "Foto selezionata: " + fotoFile.getName());
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
