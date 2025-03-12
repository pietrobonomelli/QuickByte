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
import utilities.Utilities;

public class IndirizzoForm extends VBox {

    private String emailUtente;

    public IndirizzoForm() {
        super(10);
        this.emailUtente = SessioneUtente.getEmail();
        setAlignment(Pos.CENTER);

        Label titolo = Utilities.createLabel("Aggiungi un indirizzo", "title");

        TextField indirizzoField = new TextField();
        indirizzoField.setPromptText("Indirizzo");

        TextField cittaField = new TextField();
        cittaField.setPromptText("Città");

        TextField capField = new TextField();
        capField.setPromptText("CAP");

        TextField provinciaField = new TextField();
        provinciaField.setPromptText("Provincia");

        Button salvaButton = Utilities.createButton("Salva indirizzo", () -> {
            String indirizzo = indirizzoField.getText();
            String citta = cittaField.getText();
            String cap = capField.getText();
            String provincia = provinciaField.getText();

            if (indirizzo.isEmpty() || citta.isEmpty() || cap.isEmpty() || provincia.isEmpty()) {
            	Utilities.showAlert("Errore", "Tutti i campi sono obbligatori.");
                return;
            }

            // Crea un oggetto Indirizzo e passa al DAO
            Indirizzo nuovoIndirizzo = new Indirizzo(0, indirizzo, citta, cap, provincia, emailUtente);
            salvaIndirizzo(nuovoIndirizzo);
        });

        Button annullaButton = Utilities.createButton("Annulla", this::tornaIndietro);

        getChildren().addAll(titolo, indirizzoField, cittaField, capField, provinciaField, salvaButton, annullaButton);
    }

    private void salvaIndirizzo(Indirizzo indirizzo) {
        try (Connection conn = DatabaseConnection.connect()) {
            IndirizzoDAO.getInstance().aggiungiIndirizzo(indirizzo); // Usa il DAO per salvare l'indirizzo
            Utilities.showAlert("Successo", "Indirizzo salvato correttamente!");
            tornaIndietro(); // Torna alla schermata principale del cliente
        } catch (SQLException e) {
        	Utilities.showAlert("Errore", "Errore nel salvataggio dell'indirizzo.");
            e.printStackTrace();
        }
    }

    private void tornaIndietro() {
        CarrelloView mainClienteScreen = new CarrelloView();  // Torna alla schermata precedente
        Scene currentScene = this.getScene();
        currentScene.setRoot(mainClienteScreen);
    }

}
