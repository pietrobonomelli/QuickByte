package gui.cliente;

import dao.IndirizzoDAO;
import database.DatabaseConnection;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import model.Indirizzo;
import java.sql.Connection;
import java.sql.SQLException;
import sessione.*;

public class IndirizzoForm extends VBox {

    private String emailUtente;

    public IndirizzoForm() {
        super(10);
        this.emailUtente = SessioneUtente.getEmail();
        setAlignment(Pos.CENTER);

        Label titolo = new Label("Aggiungi un indirizzo");

        TextField indirizzoField = new TextField();
        indirizzoField.setPromptText("Indirizzo");

        TextField cittaField = new TextField();
        cittaField.setPromptText("Città");

        TextField capField = new TextField();
        capField.setPromptText("CAP");

        TextField provinciaField = new TextField();
        provinciaField.setPromptText("Provincia");

        Button salvaButton = new Button("Salva indirizzo");
        salvaButton.setOnAction(e -> {
            String indirizzo = indirizzoField.getText();
            String citta = cittaField.getText();
            String cap = capField.getText();
            String provincia = provinciaField.getText();

            if (indirizzo.isEmpty() || citta.isEmpty() || cap.isEmpty() || provincia.isEmpty()) {
                showAlert("Errore", "Tutti i campi sono obbligatori.");
                return;
            }

            // Crea un oggetto Indirizzo e passa al DAO
            Indirizzo nuovoIndirizzo = new Indirizzo(0, indirizzo, citta, cap, provincia, emailUtente);
            salvaIndirizzo(nuovoIndirizzo);
        });

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(e -> tornaIndietro());

        getChildren().addAll(titolo, indirizzoField, cittaField, capField, provinciaField, salvaButton, annullaButton);
    }

    private void salvaIndirizzo(Indirizzo indirizzo) {
        try (Connection conn = DatabaseConnection.connect()) {
            IndirizzoDAO indirizzoDAO = new IndirizzoDAO();
            indirizzoDAO.aggiungiIndirizzo(indirizzo); // Usa il DAO per salvare l'indirizzo
            showAlert("Successo", "Indirizzo salvato correttamente!");
            tornaIndietro(); // Torna alla schermata principale del cliente
        } catch (SQLException e) {
            showAlert("Errore", "Errore nel salvataggio dell'indirizzo.");
            e.printStackTrace();
        }
    }

    private void tornaIndietro() {
        CarrelloView mainClienteScreen = new CarrelloView();  // Torna alla schermata precedente
        Scene currentScene = this.getScene();
        currentScene.setRoot(mainClienteScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
