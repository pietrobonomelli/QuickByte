package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import javafx.scene.Scene;
import dao.CarrelloDAO;
import model.Carrello;
import java.sql.*;
import java.util.List;

public class CarrelloView extends VBox {
    
    private String emailUtente;
    private CarrelloDAO carrelloDAO;

    public CarrelloView() {
        super(10);
        this.setStyle("-fx-padding: 10;");
        
        this.emailUtente = SessioneUtente.getEmail();
        
        try {
            carrelloDAO = new CarrelloDAO();
            loadCarrello();      
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nella connessione al database.");
        }
    }
       
    private void loadCarrello() {
        this.getChildren().clear();
        try {
            List<Carrello> carrelli = carrelloDAO.getCarrelloByUtente(emailUtente);

            for (Carrello item : carrelli) {
                String nomePiatto = carrelloDAO.getNomePiattoById(item.getIdPiatto());

                HBox carrelloItem = new HBox(10);
                carrelloItem.setStyle("-fx-padding: 10;");
                
                Label nomeLabel = new Label(nomePiatto + " (x" + item.getQuantitaPiatti() + ")");
                Button rimuoviButton = new Button("Rimuovi");
                
                rimuoviButton.setOnAction(event -> rimuoviDalCarrello(item.getIdCarrello()));
                
                carrelloItem.getChildren().addAll(nomeLabel, rimuoviButton);
                this.getChildren().add(carrelloItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento del carrello.");
        }

        Button confermaOrdineButton = new Button("Conferma Ordine");
        confermaOrdineButton.setOnAction(event -> confermaOrdine());
        this.getChildren().add(confermaOrdineButton);

        Button tornaAllaListaButton = new Button("Torna indetro");
        tornaAllaListaButton.setOnAction(event -> tornaAllaLista());
        this.getChildren().add(tornaAllaListaButton);
    }
    
    private void rimuoviDalCarrello(int idCarrello) {
        try {
            carrelloDAO.rimuoviDalCarrello(idCarrello);
            loadCarrello();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante la rimozione dal carrello.");
        }
    }

    private void confermaOrdine() {
        // Logica di conferma ordine (introdurre il DAO anche per le altre operazioni correlate)
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void tornaAllaLista() {
        MenuCliente mainClienteScreen = new MenuCliente();
        Scene currentScene = this.getScene();
        currentScene.setRoot(mainClienteScreen);
    }
}
