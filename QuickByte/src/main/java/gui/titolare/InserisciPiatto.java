package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import utilities.Utilities;
import sessione.SessioneRistorante;
import sessione.SessioneMenu;
import database.DatabaseConnection;
import dao.PiattoDAO;
import model.Piatto;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class InserisciPiatto extends VBox {
    private TextField nomeField, prezzoField, allergeniField, fotoField;
    private CheckBox disponibilitaCheckBox;
    private Button scegliFotoButton;
    private File fotoFile;
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

        Label fotoLabel = new Label("Foto:");
        fotoField = new TextField();
        fotoField.setPromptText("Nome della foto");
        fotoField.setEditable(false);

        scegliFotoButton = new Button("Scegli Foto");
        scegliFotoButton.setOnAction(e -> scegliFoto());

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

        HBox buttonContainer = new HBox(10, scegliFotoButton, inserisciButton, tornaIndietroButton);

        this.getChildren().addAll(titolo, nomeLabel, nomeField, prezzoLabel, prezzoField, allergeniLabel, allergeniField, disponibilitaCheckBox, fotoLabel, fotoField, buttonContainer);
    }

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

        fotoFile = fileChooser.showOpenDialog(null);
        if (fotoFile != null) {
            fotoField.setText(fotoFile.getName());
            Utilities.showAlert("Foto Selezionata", "Foto selezionata: " + fotoFile.getName());
        }
    }

    private void inserisciPiatto() {
        String nome = nomeField.getText();
        String prezzo = prezzoField.getText();
        String allergeni = allergeniField.getText();
        boolean disponibilita = disponibilitaCheckBox.isSelected();
        String foto = fotoField.getText();

        if (nome.isEmpty() || prezzo.isEmpty()) {
            Utilities.showAlert("Errore", "Nome e prezzo sono obbligatori");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            Piatto piatto = new Piatto(0, nome, disponibilita, prezzo, allergeni, foto, nomeMenu, idRistorante);
            PiattoDAO.getInstance().aggiungiPiatto(piatto);
            tornaAiPiatti();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'inserimento del piatto");
        }
    }

    private void tornaAiPiatti() throws SQLException {
        PiattiTitolare piattiScreen = new PiattiTitolare();
        this.getScene().setRoot(piattiScreen);
    }
}
