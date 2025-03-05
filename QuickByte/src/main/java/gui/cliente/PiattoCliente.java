package gui.cliente;

import javafx.scene.control.*;
import gui.main.*;
import javafx.scene.layout.*;
import sessione.SessionePiatto;
import javafx.scene.Scene;
import dao.PiattoDAO;
import model.Piatto;  // Importa il modello corretto
import javafx.scene.image.ImageView;
import java.sql.SQLException;

public class PiattoCliente extends VBox {
	
    public PiattoCliente() throws SQLException {
        super(10);
        int idPiatto = SessionePiatto.getId();  // Recupera l'idPiatto dalla sessione
        this.setStyle("-fx-padding: 10;");
        loadPiatto(idPiatto);
    }

    private void loadPiatto(int idPiatto) {
        try {
            // Recupera il piatto tramite il PiattoDAO
            Piatto piatto = PiattoDAO.getInstance().getPiattoById(idPiatto);  // Ottieni direttamente il piatto dal DAO

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
            	Utilities.showAlert("Errore", "Piatto non trovato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento delle informazioni del piatto.");
        }
    }

    private void tornaAllaListaPiatti() throws SQLException {
        PiattiCliente piattiClienteScreen = new PiattiCliente();  // Torna alla schermata dei piatti
        Scene currentScene = this.getScene();
        currentScene.setRoot(piattiClienteScreen);
    }
}
