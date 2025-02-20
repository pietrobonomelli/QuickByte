package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import sessione.SessioneMenu;
import sessione.SessionePiatto;
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
    private PiattoDAO piattoDAO;

    public ModificaPiatto() throws SQLException {
        super(10);
        this.setStyle("-fx-padding: 10;");
        this.idPiatto = SessionePiatto.getId();
        this.nomeMenu = SessioneMenu.getNome();

        try (Connection conn = DatabaseConnection.connect()) {
            piattoDAO = new PiattoDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile connettersi al database.");
        }

        Label titoloLabel = new Label("Modifica Piatto: " + idPiatto); 

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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        loadPiattoData();
        
        this.getChildren().addAll(titoloLabel, prezzoLabel, prezzoField, allergeniLabel, allergeniField,
                disponibilitaLabel, disponibilitaCheckBox, scegliFotoButton, salvaButton, tornaIndietroButton);
    }

    private void loadPiattoData() throws SQLException {
            piattoDAO = new PiattoDAO();
            Piatto piatto = piattoDAO.getPiattoById(idPiatto);
            if (piatto != null) {
                prezzoField.setText(piatto.getPrezzo());
                allergeniField.setText(piatto.getAllergeni());
                disponibilitaCheckBox.setSelected(piatto.isDisponibile());
            }
        
    }

    private void salvaModifiche() {
        try (Connection conn = DatabaseConnection.connect()) {
            piattoDAO = new PiattoDAO();
            Piatto piatto = new Piatto(idPiatto, "", disponibilitaCheckBox.isSelected(), 
                                       prezzoField.getText(), allergeniField.getText(), 
                                       fotoFile != null ? fotoFile.getAbsolutePath() : null, nomeMenu, 0);
            piattoDAO.aggiornaPiatto(piatto);
            
            showAlert("Successo", "Modifiche salvate correttamente.");
            tornaAiPiatti(); 
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
