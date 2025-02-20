package gui.cliente;

import database.DatabaseConnection;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sessione.SessioneUtente;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

            salvaIndirizzo(emailUtente, indirizzo, citta, cap, provincia);
        });

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(e -> tornaIndietro());

        getChildren().addAll(titolo, indirizzoField, cittaField, capField, provinciaField, salvaButton, annullaButton);
    }

    private void salvaIndirizzo(String email, String indirizzo, String citta, String cap, String provincia) {
        String sql = "INSERT INTO Indirizzo (indirizzo, citta, cap, provincia, emailUtente) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, indirizzo);
            pstmt.setString(2, citta);
            pstmt.setString(3, cap);
            pstmt.setString(4, provincia);
            pstmt.setString(5, email);
            pstmt.executeUpdate();

            showAlert("Successo", "Indirizzo salvato correttamente!");
            tornaIndietro(); // Torna alla schermata precedente

        } catch (SQLException e) {
            showAlert("Errore", "Errore nel salvataggio dell'indirizzo.");
            e.printStackTrace();
        }
    }

    private void tornaIndietro() {
        MainScreenCliente mainClienteScreen = new MainScreenCliente();  // Torna alla schermata principale del cliente
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
