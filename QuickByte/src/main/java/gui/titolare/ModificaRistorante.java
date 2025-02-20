package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import database.DatabaseConnection;

import java.sql.*;

public class ModificaRistorante extends VBox {
    private TextField nomeField, telefonoField, indirizzoField;
    private String emailTitolare = SessioneUtente.getEmail();
    private String nomeRistorante;

    public ModificaRistorante(String nomeRistorante) {
        super(10);
        this.nomeRistorante = nomeRistorante;
        this.setStyle("-fx-padding: 10;");

        // Titolo
        Label titolo = new Label("Modifica Ristorante: " + nomeRistorante);

        // Campi di input
        nomeField = new TextField();
        telefonoField = new TextField();
        indirizzoField = new TextField();

        // Carica i dati attuali
        caricaDatiRistorante();

        // Pulsante per salvare le modifiche
        Button salvaButton = new Button("Salva Modifiche");
        salvaButton.setOnAction(e -> salvaModifiche());
        
        // Pulsante per tornare alla gestione ristoranti
        Button tornaButton = new Button("Torna alla Gestione Ristoranti");
        tornaButton.setOnAction(e -> getScene().setRoot(new MainScreenTitolare()));

        // Aggiunta elementi al layout
        this.getChildren().addAll(titolo, nomeField, telefonoField, indirizzoField, salvaButton, tornaButton);
    }

    private void caricaDatiRistorante() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome, telefono, indirizzo FROM Ristorante WHERE nome = ? AND emailTitolare = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeRistorante);
                stmt.setString(2, emailTitolare);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nomeField.setText(rs.getString("nome"));
                    telefonoField.setText(rs.getString("telefono"));
                    indirizzoField.setText(rs.getString("indirizzo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei dati del ristorante.");
        }
    }

    private void salvaModifiche() {
        String nuovoNome = nomeField.getText();
        String nuovoTelefono = telefonoField.getText();
        String nuovoIndirizzo = indirizzoField.getText();

        if (nuovoNome.isEmpty() || nuovoTelefono.isEmpty() || nuovoIndirizzo.isEmpty()) {
            showAlert("Errore", "Tutti i campi devono essere compilati.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "UPDATE Ristorante SET nome = ?, telefono = ?, indirizzo = ? WHERE nome = ? AND emailTitolare = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nuovoNome);
                stmt.setString(2, nuovoTelefono);
                stmt.setString(3, nuovoIndirizzo);
                stmt.setString(4, nomeRistorante);
                stmt.setString(5, emailTitolare);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Successo", "Ristorante modificato con successo!");
                    getScene().setRoot(new MainScreenTitolare());
                } else {
                    showAlert("Errore", "Modifica non riuscita.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante la modifica del ristorante.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
