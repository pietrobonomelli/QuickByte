package gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import database.DatabaseConnection;
import java.sql.*;

public class ModificaPiatto extends VBox {
    private TextField nomeField, prezzoField, allergeniField, fotoField;
    private CheckBox disponibilitaCheck;
    private String nomePiatto;
    private String nomeMenu;
    private int idRistorante;
    
    public ModificaPiatto(String nomePiatto, String nomeMenu, int idRistorante) {
        super(10);
        this.nomePiatto = nomePiatto;
        this.nomeMenu = nomeMenu;
        this.idRistorante = idRistorante;
        
        this.setStyle("-fx-padding: 10;");
        
        // Titolo della schermata
        Label titolo = new Label("Modifica Piatto: " + nomePiatto);
        
        // Campi di input per il piatto
        Label nomeLabel = new Label("Nome:");
        nomeField = new TextField();
        
        Label disponibilitaLabel = new Label("Disponibilità:");
        disponibilitaCheck = new CheckBox();
        
        Label prezzoLabel = new Label("Prezzo:");
        prezzoField = new TextField();
        
        Label allergeniLabel = new Label("Allergeni:");
        allergeniField = new TextField();
        
        Label fotoLabel = new Label("Foto:");
        fotoField = new TextField();
        
        // Carica i dati attuali del piatto dal database
        caricaDatiPiatto();
        
        // Bottone per salvare le modifiche
        Button salvaButton = new Button("Salva Modifiche");
        salvaButton.setOnAction(e -> salvaModifiche());
        
        // Bottone per tornare alla gestione dei piatti
        Button tornaButton = new Button("Torna alla Gestione Piatti");
        tornaButton.setOnAction(e -> getScene().setRoot(new GestisciMenu(idRistorante)));
        
        // Aggiunta degli elementi al layout
        this.getChildren().addAll(titolo, 
                                  nomeLabel, nomeField, 
                                  disponibilitaLabel, disponibilitaCheck, 
                                  prezzoLabel, prezzoField, 
                                  allergeniLabel, allergeniField, 
                                  fotoLabel, fotoField, 
                                  salvaButton, tornaButton);
    }
    
    private void caricaDatiPiatto() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome, disponibilita, prezzo, allergeni, foto FROM Piatto WHERE nome = ? AND nomeMenu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomePiatto);
                stmt.setString(2, nomeMenu);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nomeField.setText(rs.getString("nome"));
                    disponibilitaCheck.setSelected(rs.getBoolean("disponibilita"));
                    prezzoField.setText(rs.getString("prezzo"));
                    allergeniField.setText(rs.getString("allergeni"));
                    fotoField.setText(rs.getString("foto"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei dati del piatto.");
        }
    }
    
    private void salvaModifiche() {
        String nuovoNome = nomeField.getText();
        boolean nuovaDisponibilita = disponibilitaCheck.isSelected();
        String nuovoPrezzo = prezzoField.getText();
        String nuoviAllergeni = allergeniField.getText();
        String nuovaFoto = fotoField.getText();
        
        if (nuovoNome.isEmpty() || nuovoPrezzo.isEmpty()) {
            showAlert("Errore", "I campi Nome e Prezzo sono obbligatori.");
            return;
        }
        
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "UPDATE Piatto SET nome = ?, disponibilita = ?, prezzo = ?, allergeni = ?, foto = ? WHERE nome = ? AND nomeMenu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nuovoNome);
                stmt.setBoolean(2, nuovaDisponibilita);
                stmt.setString(3, nuovoPrezzo);
                stmt.setString(4, nuoviAllergeni);
                stmt.setString(5, nuovaFoto);
                stmt.setString(6, nomePiatto);
                stmt.setString(7, nomeMenu);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Successo", "Piatto modificato con successo!");
                    // Al termine della modifica si torna alla schermata di gestione dei piatti
                    getScene().setRoot(new GestisciMenu(idRistorante));
                } else {
                    showAlert("Errore", "Modifica non riuscita.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante la modifica del piatto.");
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
