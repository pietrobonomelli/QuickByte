package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.*;
import javafx.scene.Scene;
import dao.*;
import model.*;
import java.sql.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CarrelloView extends VBox {
    
    private String emailUtente;

    public CarrelloView() {
        super(10);
        this.setStyle("-fx-padding: 10;");
        
        this.emailUtente = SessioneUtente.getEmail();
        loadCarrello();      	
    }
       
    private void loadCarrello() {
        this.getChildren().clear();
        try {
            List<Carrello> carrelli = CarrelloDAO.getInstance().getCarrelloByUtente(emailUtente);

            for (Carrello item : carrelli) {
                String nomePiatto = CarrelloDAO.getInstance().getNomePiattoById(item.getIdPiatto());

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
        confermaOrdineButton.setOnAction(event -> {
			try {
				confermaOrdine();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        this.getChildren().add(confermaOrdineButton);

        Button tornaAllaListaButton = new Button("Torna indetro");
        tornaAllaListaButton.setOnAction(event -> tornaAllaLista());
        this.getChildren().add(tornaAllaListaButton);
    }
    
    private void rimuoviDalCarrello(int idCarrello) {
        try {
        	CarrelloDAO.getInstance().rimuoviDalCarrello(idCarrello);
            loadCarrello();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante la rimozione dal carrello.");
        }
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
    

    public String getDataOraCorrente() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }  
    
    private void confermaOrdine() throws SQLException {
        MetodoDiPagamentoDAO pagamentoDAO = new MetodoDiPagamentoDAO();
        List<String> metodiPagamento = pagamentoDAO.getMetodiPagamento(emailUtente);

        ChoiceDialog<String> sceltaCarta = new ChoiceDialog<>("Aggiungi metodo di pagamento", metodiPagamento);
        sceltaCarta.setTitle("Metodo di Pagamento");
        sceltaCarta.setHeaderText("Seleziona un metodo di pagamento:");
        sceltaCarta.setContentText("Metodo di pagamento:");

        sceltaCarta.showAndWait().ifPresent(cartaSelezionata -> {
            if (cartaSelezionata.equals("Aggiungi metodo di pagamento")) {
                try {
                    this.getChildren().setAll(new MetodoDiPagamentoForm());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                selezionaIndirizzo();
            }
        });
    }

    private void selezionaIndirizzo() {
        List<String> indirizzi = IndirizzoDAO.getInstance().getIndirizzi(emailUtente);

        ChoiceDialog<String> sceltaIndirizzo = new ChoiceDialog<>("Aggiungi indirizzo", indirizzi);
        sceltaIndirizzo.setTitle("Indirizzo di Consegna");
        sceltaIndirizzo.setHeaderText("Seleziona un indirizzo di consegna:");
        sceltaIndirizzo.setContentText("Indirizzo:");

        sceltaIndirizzo.showAndWait().ifPresent(indirizzoSelezionato -> {
            if (indirizzoSelezionato.equals("Aggiungi indirizzo")) {
                this.getChildren().setAll(new IndirizzoForm());
            } else {
                registraOrdine(indirizzoSelezionato);
            }
        });
    }

    private void registraOrdine(String indirizzo) {
        boolean success = OrdineDAO.getInstance().registraOrdine(emailUtente, indirizzo);

        if (success) {
            showAlert("Ordine Confermato", "Il tuo ordine è stato pagato con successo!");
            loadCarrello();
        } else {
            showAlert("Errore", "Errore durante la conferma dell'ordine.");
        }
    }

    
    
    
    
    
    
    
    
    
    

}
