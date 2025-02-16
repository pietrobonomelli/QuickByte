package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import database.DatabaseConnection;
import gui.main.*;
import java.sql.*;
import javafx.scene.image.ImageView;


public class PiattoCliente extends VBox {

    public PiattoCliente() {
        super(10);

        int idPiatto = SessionePiatto.getId();  // Recupera l'idPiatto dalla sessione
        this.setStyle("-fx-padding: 10;");
        loadPiatto(idPiatto);
    }

    private void loadPiatto(int idPiatto) {
        try (Connection conn = DatabaseConnection.connect()) {
            // Query per recuperare le informazioni del piatto con idPiatto
            String query = "SELECT nome, disponibile, prezzo, allergeni, foto, nomeMenu FROM Piatto WHERE idPiatto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPiatto);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String nomePiatto = rs.getString("nome");
                    boolean disponibile = rs.getInt("disponibile") == 1;  // Converte 1 in true e 0 in false
                    String prezzoPiatto = rs.getString("prezzo");  // Modifica per trattare il prezzo come stringa
                    String allergeni = rs.getString("allergeni");
                    String fotoPiatto = rs.getString("foto");  // Se disponibile, per esempio un URL o path dell'immagine

                    // Aggiungi un titolo per il piatto
                    Label nomeLabel = new Label(nomePiatto);
                    nomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
                    this.getChildren().add(nomeLabel);

                    // Aggiungi disponibilità
                    Label disponibilitaLabel = new Label(disponibile ? "Disponibile" : "Non disponibile");
                    this.getChildren().add(disponibilitaLabel);

                    // Aggiungi prezzo
                    Label prezzoLabel = new Label("Prezzo: €" + prezzoPiatto);
                    this.getChildren().add(prezzoLabel);

                    // Aggiungi allergeni, se presenti
                    if (allergeni != null && !allergeni.isEmpty()) {
                        Label allergeniLabel = new Label("Allergeni: " + allergeni);
                        this.getChildren().add(allergeniLabel);
                    }

                    // Aggiungi immagine del piatto (se presente)
                    if (fotoPiatto != null && !fotoPiatto.isEmpty()) {
                        ImageView immagineView = new ImageView(fotoPiatto);  // Puoi caricare l'immagine tramite URL o path
                        immagineView.setFitWidth(200);
                        immagineView.setFitHeight(200);
                        this.getChildren().add(immagineView);
                    }

                    // Aggiungi un pulsante per aggiungere al carrello
                    Button aggiungiCarrelloButton = new Button("Aggiungi al carrello");
                    aggiungiCarrelloButton.setOnAction(event -> aggiungiAlCarrello(idPiatto));
                    this.getChildren().add(aggiungiCarrelloButton);

                    // Aggiungi un pulsante per tornare alla lista di piatti
                    Button tornaAllaListaButton = new Button("Torna alla lista di piatti");
                    tornaAllaListaButton.setOnAction(event -> tornaAllaListaPiatti());
                    this.getChildren().add(tornaAllaListaButton);
                } else {
                    showAlert("Errore", "Piatto non trovato.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento delle informazioni del piatto.");
        }
    }


    private void aggiungiAlCarrello(int idPiatto) {
        // Logica per aggiungere il piatto al carrello
        System.out.println("Piatto " + idPiatto + " aggiunto al carrello.");
        // Puoi implementare una logica per salvare il piatto in una struttura dati come un carrello
    }

    private void tornaAllaListaPiatti() {
        PiattiCliente piattiClienteScreen = new PiattiCliente();  // Torna alla schermata dei piatti
        Scene currentScene = this.getScene();
        currentScene.setRoot(piattiClienteScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
