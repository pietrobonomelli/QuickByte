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
    private TextField campoNome, campoPrezzo, campoAllergeni, campoFoto;
    private CheckBox checkBoxDisponibilita;
    private Button bottoneScegliFoto;
    private File fileFoto;
    private String nomeMenu;
    private int idRistorante;

    public InserisciPiatto() {
        super(10);
        this.nomeMenu = SessioneMenu.getNome();
        this.idRistorante = SessioneRistorante.getId();
        this.setStyle("-fx-padding: 10;");

        // Creazione delle etichette utilizzando il metodo di utilità
        Label titolo = Utilities.createLabel("Inserisci un nuovo piatto nel menu: " + nomeMenu, "title");
        titolo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label etichettaNome = Utilities.createLabel("Nome del piatto:", "label");
        campoNome = new TextField();
        campoNome.setPromptText("Nome del piatto");

        Label etichettaPrezzo = Utilities.createLabel("Prezzo:", "label");
        campoPrezzo = new TextField();
        campoPrezzo.setPromptText("Prezzo");

        Label etichettaAllergeni = Utilities.createLabel("Allergeni:", "label");
        campoAllergeni = new TextField();
        campoAllergeni.setPromptText("Allergeni");

        checkBoxDisponibilita = new CheckBox("Disponibile");

        Label etichettaFoto = Utilities.createLabel("Foto:", "label");
        campoFoto = new TextField();
        campoFoto.setPromptText("Nome della foto");
        campoFoto.setEditable(false);

        bottoneScegliFoto = Utilities.createButton("Scegli Foto", this::selezionaFoto);

        Button bottoneInserisci = Utilities.createButton("Inserisci Piatto", this::inserisciPiatto);
        Button bottoneTornaIndietro = Utilities.createButton("Torna ai Piatti", this::tornaAPiatti);

        HBox contenitoreBottoni = new HBox(10, bottoneScegliFoto, bottoneInserisci, bottoneTornaIndietro);

        this.getChildren().addAll(titolo, etichettaNome, campoNome, etichettaPrezzo, campoPrezzo, etichettaAllergeni, campoAllergeni, checkBoxDisponibilita, etichettaFoto, campoFoto, contenitoreBottoni);
    }

    /**
     * Apre una finestra di dialogo per selezionare una foto.
     */
    private void selezionaFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        // Imposta la directory iniziale del FileChooser alla directory delle immagini
        String directoryCorrente = System.getProperty("user.dir");
        File directoryIniziale = new File(directoryCorrente, "src/main/resources/images");

        // Controlla se la directory esiste, altrimenti utilizza la directory corrente
        if (directoryIniziale.exists() && directoryIniziale.isDirectory()) {
            fileChooser.setInitialDirectory(directoryIniziale);
        } else {
            fileChooser.setInitialDirectory(new File(directoryCorrente));
        }

        fileFoto = fileChooser.showOpenDialog(null);
        if (fileFoto != null) {
            campoFoto.setText(fileFoto.getName());
            Utilities.showAlert("Foto Selezionata", "Foto selezionata: " + fileFoto.getName());
        }
    }

    /**
     * Inserisce un nuovo piatto nel database.
     */
    private void inserisciPiatto() {
        String nome = campoNome.getText();
        String prezzo = campoPrezzo.getText();
        String allergeni = campoAllergeni.getText();
        boolean disponibilita = checkBoxDisponibilita.isSelected();
        String foto = campoFoto.getText();

        if (nome.isEmpty() || prezzo.isEmpty()) {
            Utilities.showAlert("Errore", "Nome e prezzo sono obbligatori");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            Piatto piatto = new Piatto(0, nome, disponibilita, prezzo, allergeni, foto, nomeMenu, idRistorante);
            PiattoDAO.getInstance().aggiungiPiatto(piatto);
            tornaAPiatti();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'inserimento del piatto");
        }
    }

    /**
     * Torna alla schermata dei piatti.
     */
    private void tornaAPiatti() {
        try {
            PiattiTitolare schermataPiatti = new PiattiTitolare();
            this.getScene().setRoot(schermataPiatti);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante il caricamento della schermata dei piatti");
        }
    }
}
