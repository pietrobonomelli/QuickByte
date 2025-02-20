package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessionePiatto;
import javafx.scene.Scene;
import dao.PiattoDAO;
import model.Piatto;  // Importa il modello corretto
import javafx.scene.image.ImageView;
import java.sql.SQLException;

public class PiattoCliente extends VBox {
	
	private PiattoDAO piattoDao;

    public PiattoCliente() throws SQLException {
        super(10);
        this.piattoDao = new PiattoDAO();
        int idPiatto = SessionePiatto.getId();  // Recupera l'idPiatto dalla sessione
        this.setStyle("-fx-padding: 10;");
        loadPiatto(idPiatto);
    }

    private void loadPiatto(int idPiatto) {
        try {
            // Recupera il piatto tramite il PiattoDAO
            Piatto piatto = this.piattoDao.getPiattoById(idPiatto);  // Ottieni direttamente il piatto dal DAO

            if (piatto != null) {
                // Aggiungi un titolo per il piatto
                Label nomeLabel = new Label(piatto.getNome());
                nomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
                this.getChildren().add(nomeLabel);

                // Aggiungi disponibilità
                Label disponibilitaLabel = new Label(piatto.isDisponibile() ? "Disponibile" : "Non disponibile");
                this.getChildren().add(disponibilitaLabel);

                // Aggiungi prezzo
                Label prezzoLabel = new Label("Prezzo: €" + piatto.getPrezzo());
                this.getChildren().add(prezzoLabel);

                // Aggiungi allergeni, se presenti
                if (piatto.getAllergeni() != null && !piatto.getAllergeni().isEmpty()) {
                    Label allergeniLabel = new Label("Allergeni: " + piatto.getAllergeni());
                    this.getChildren().add(allergeniLabel);
                }

                // Aggiungi immagine del piatto (se presente)
                if (piatto.getFoto() != null && !piatto.getFoto().isEmpty()) {
                    ImageView immagineView = new ImageView(piatto.getFoto());  // Puoi caricare l'immagine tramite URL o path
                    immagineView.setFitWidth(200);
                    immagineView.setFitHeight(200);
                    this.getChildren().add(immagineView);
                }

                // Aggiungi un pulsante per aggiungere al carrello
                Button aggiungiCarrelloButton = new Button("Aggiungi al carrello");
                aggiungiCarrelloButton.setOnAction(event -> aggiungiAlCarrello(piatto.getIdPiatto()));
                this.getChildren().add(aggiungiCarrelloButton);

                // Aggiungi un pulsante per tornare alla lista di piatti
                Button tornaAllaListaButton = new Button("Torna alla lista di piatti");
                tornaAllaListaButton.setOnAction(event -> {
                    try {
                        tornaAllaListaPiatti();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                this.getChildren().add(tornaAllaListaButton);
            } else {
                showAlert("Errore", "Piatto non trovato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento delle informazioni del piatto.");
        }
    }

    private void aggiungiAlCarrello(int idPiatto) {
        // Logica per aggiungere il piatto al carrello
        System.out.println("Piatto " + idPiatto + " aggiunto al carrello.");
    }

    private void tornaAllaListaPiatti() throws SQLException {
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
