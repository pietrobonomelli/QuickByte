package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import utilities.Utilities;
import sessione.SessionePiatto;
import sessione.SessioneMenu;
import dao.PiattoDAO;
import model.Piatto;
import java.io.File;
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
        Label titoloLabel = Utilities.createLabel("MODIFICA PIATTO", "title-label");
        titoloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        prezzoField = new TextField();
        VBox prezzoBox = Utilities.createFieldBox("Prezzo:", "", prezzoField);

        allergeniField = new TextField();
        VBox allergeniBox = Utilities.createFieldBox("Allergeni:", "", allergeniField);

        disponibilitaCheckBox = new CheckBox("Disponibile:");

        tornaIndietroButton = Utilities.createButton("⬅ INDIETRO", () -> {
			try {
				tornaAiPiatti();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
        scegliFotoButton = Utilities.createButton("Scegli Foto", this::scegliFoto);
        salvaButton = Utilities.createButton("Salva Modifiche", this::salvaModifiche);

        loadPiattoData();

        // HBox for buttons to align them on the same row
        HBox buttonsBox = new HBox(10, tornaIndietroButton, scegliFotoButton, salvaButton);
        buttonsBox.setStyle("-fx-alignment: center;");

        this.getChildren().addAll(titoloLabel, prezzoBox, allergeniBox, disponibilitaCheckBox, buttonsBox);
    }

    /*
     * Carica i dati del piatto.
     */
    private void loadPiattoData() throws SQLException {
        Piatto piatto = PiattoDAO.getInstance().getPiattoById(idPiatto);
        if (piatto != null) {
            prezzoField.setText(piatto.getPrezzo());
            allergeniField.setText(piatto.getAllergeni());
            disponibilitaCheckBox.setSelected(piatto.isDisponibile());
        }
    }

    /*
     * Salvataggio delle modifiche del piatto.
     */
    private void salvaModifiche() {
        try {
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

    /*
     * Apre file explorer per la scelta di una foto per il piatto.
     */
    private void scegliFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        // Imposta la directory iniziale del FileChooser alla directory delle immagini
        String currentDirectory = System.getProperty("user.dir");
        File initialDirectory = new File(currentDirectory, "src/main/resources/images");

        // Controlla se la directory esiste, altrimenti utilizza la directory corrente
        if (initialDirectory.exists() && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        } else {
            fileChooser.setInitialDirectory(new File(currentDirectory));
        }

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Utilities.showAlert("Foto Selezionata", "Foto selezionata: " + selectedFile.getName());
            // Aggiorna la foto nel database
            try {
                PiattoDAO.getInstance().aggiornaFotoPiatto(idPiatto, selectedFile.getName());
                Utilities.showAlert("Successo", "Foto aggiornata nel database!");
            } catch (SQLException e) {
                e.printStackTrace();
                Utilities.showAlert("Errore", "Errore durante l'aggiornamento della foto nel database: " + e.getMessage());
            }
        }
    }

    private void tornaAiPiatti() throws SQLException {
        PiattiTitolare piattiScreen = new PiattiTitolare();
        this.getScene().setRoot(piattiScreen);
    }
}
