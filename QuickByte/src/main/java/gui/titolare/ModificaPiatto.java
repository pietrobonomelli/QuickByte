package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import sessione.SessionePiatto;
import utilities.Utilities;
import sessione.SessioneMenu;
import database.DatabaseConnection;
import dao.PiattoDAO;
import model.Piatto;
import gui.main.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

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

    public ModificaPiatto() throws SQLException {
        super(10);
        this.setStyle("-fx-padding: 10;");
        this.idPiatto = SessionePiatto.getId();
        this.nomeMenu = SessioneMenu.getNome();

        // Large Title
        Label titoloLabel = new Label("MODIFICA PIATTO");
        titoloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

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
        tornaIndietroButton.setOnAction(e -> {
			try {
				tornaAiPiatti();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
        
        loadPiattoData();
        
        // HBox for buttons to align them on the same row
        HBox buttonsBox = new HBox(10, scegliFotoButton, salvaButton, tornaIndietroButton);
        buttonsBox.setStyle("-fx-alignment: center;");

        this.getChildren().addAll(titoloLabel, prezzoLabel, prezzoField, allergeniLabel, allergeniField,
                disponibilitaLabel, disponibilitaCheckBox, buttonsBox);
    }

    private void loadPiattoData() throws SQLException {
            Piatto piatto = PiattoDAO.getInstance().getPiattoById(idPiatto);
            if (piatto != null) {
                prezzoField.setText(piatto.getPrezzo());
                allergeniField.setText(piatto.getAllergeni());
                disponibilitaCheckBox.setSelected(piatto.isDisponibile());
            }
    }

    private void salvaModifiche() {
        try (Connection conn = DatabaseConnection.connect()) {
            Piatto piatto = new Piatto(idPiatto, "", disponibilitaCheckBox.isSelected(), 
                                       prezzoField.getText(), allergeniField.getText(), 
                                       fotoFile != null ? fotoFile.getAbsolutePath() : null, nomeMenu, 0);
            PiattoDAO.getInstance().aggiornaPiatto(piatto);
            
            Utilities.showAlert("Successo", "Modifiche salvate correttamente.");
            tornaAiPiatti(); 
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel salvataggio delle modifiche.");
        }
    }
    
    private void scegliFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        fotoFile = fileChooser.showOpenDialog(null);
        if (fotoFile != null) {
        	Utilities.showAlert("Foto Selezionata", "Foto selezionata: " + fotoFile.getName());
        }
    }
    
    private void tornaAiPiatti() throws SQLException {
        PiattiTitolare piattiScreen = new PiattiTitolare();
        this.getScene().setRoot(piattiScreen);
    }
}
