package gui.cliente;

import database.DatabaseConnection;
import gui.main.SessioneUtente;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MetodoDiPagamentoForm extends VBox {

    private String emailUtente;

    public MetodoDiPagamentoForm() {
        super(10);
        this.emailUtente = SessioneUtente.getEmail();
        setAlignment(Pos.CENTER);

        Label titolo = new Label("Aggiungi un metodo di pagamento");

        TextField nominativoField = new TextField();
        nominativoField.setPromptText("Nome sulla carta");

        TextField numeroCartaField = new TextField();
        numeroCartaField.setPromptText("Numero carta (16 cifre)");

        TextField scadenzaField = new TextField();
        scadenzaField.setPromptText("Scadenza (MM/YY)");

        Button salvaButton = new Button("Salva carta");
        salvaButton.setOnAction(e -> {
            String nominativo = nominativoField.getText();
            String numeroCarta = numeroCartaField.getText();
            String scadenza = scadenzaField.getText();

            if (nominativo.isEmpty() || numeroCarta.isEmpty() || scadenza.isEmpty()) {
                showAlert("Errore", "Tutti i campi sono obbligatori.");
                return;
            }

            if (numeroCarta.length() != 16 || !numeroCarta.matches("\\d+")) {
                showAlert("Errore", "Il numero della carta deve avere 16 cifre.");
                return;
            }

            salvaMetodoDiPagamento(emailUtente, nominativo, numeroCarta, scadenza);
        });

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(e -> tornaIndietro());

        getChildren().addAll(titolo, nominativoField, numeroCartaField, scadenzaField, salvaButton, annullaButton);
    }

    private void salvaMetodoDiPagamento(String email, String nominativo, String numeroCarta, String scadenza) {
        String sql = "INSERT INTO MetodoDiPagamento (nominativo, numeroCarta, scadenza, emailCliente) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nominativo);
            pstmt.setString(2, numeroCarta);
            pstmt.setString(3, scadenza);
            pstmt.setString(4, email);
            pstmt.executeUpdate();

            showAlert("Successo", "Metodo di pagamento salvato correttamente!");
            tornaIndietro(); // Torna alla schermata precedente

        } catch (SQLException e) {
            showAlert("Errore", "Errore nel salvataggio del metodo di pagamento.");
            e.printStackTrace();
        }
    }

    private void tornaIndietro() {
        Carrello mainClienteScreen = new Carrello();  // Torna alla schermata dei piatti
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
