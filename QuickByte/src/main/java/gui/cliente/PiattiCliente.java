package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneCarrello;
import sessione.SessioneMenu;
import sessione.SessionePiatto;
import sessione.SessioneRistorante;
import sessione.SessioneUtente;
import gui.main.*;
import dao.PiattoDAO;
import model.Piatto;
import java.sql.*;
import java.util.List;

public class PiattiCliente extends VBox {

    private String emailCliente;
    private String nomeMenu;
    private int idRistorante;

    public PiattiCliente() throws SQLException {
        super(10);

        this.idRistorante = SessioneRistorante.getId();
        this.nomeMenu = SessioneMenu.getNome();  // Recupera il nome del menu selezionato dalla sessione
        this.emailCliente = SessioneUtente.getEmail();

        this.setStyle("-fx-padding: 10;");
        loadPiatti();
    }

    private void loadPiatti() {
        try {
            // Ottieni i piatti dal DAO
            List<Piatto> piatti = PiattoDAO.getInstance().getPiattiByMenuAndIdRistorante(nomeMenu, idRistorante);

            for (Piatto piatto : piatti) {
                // Crea un box per ogni piatto
                HBox piattoBox = new HBox(10);
                piattoBox.setStyle("-fx-padding: 10;");

                // Crea l'etichetta con il nome del piatto
                Label nomeLabel = new Label(piatto.getNome());

                // Crea il pulsante per aggiungere al carrello
                Button aggiungiCarrelloButton = new Button("Aggiungi al carrello");

                // Quando clicchi sul nome del piatto, salva l'idPiatto nella sessione e vai alla pagina PiattoCliente
                nomeLabel.setOnMouseClicked(event -> {
                    SessionePiatto.setId(piatto.getIdPiatto());  // Salviamo l'ID del piatto nella sessione
                    try {
						switchToPiattoCliente();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}          // Vai alla pagina PiattoCliente
                });

                // Quando clicchi sul pulsante "Aggiungi al carrello", passiamo l'idPiatto al metodo
                aggiungiCarrelloButton.setOnAction(event -> {
					try {
						aggiungiAlCarrello(piatto.getIdPiatto());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

                // Aggiungi gli elementi al box
                piattoBox.getChildren().addAll(nomeLabel, aggiungiCarrelloButton);
                this.getChildren().add(piattoBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei piatti.");
        }

        // Aggiungi il pulsante "Torna alla lista dei menu"
        Button tornaAllaListaMenuButton = new Button("Torna alla lista dei menu");
        tornaAllaListaMenuButton.setOnAction(event -> tornaAllaListaMenu());

        // Aggiungi il pulsante alla schermata
        this.getChildren().add(tornaAllaListaMenuButton);
    }

    private void aggiungiAlCarrello(int idPiatto) throws SQLException {
        boolean pieno = SessioneCarrello.getPieno();
        int idRistoranteCarrello = SessioneCarrello.getIdRistorante();

        if (!pieno) {
            // Se il carrello è vuoto, imposto l'idRistorante in sessione e aggiungo il piatto
            SessioneCarrello.setIdRistorante(idRistorante);
            SessioneCarrello.setPieno(true);
            PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
        } else {
            if (idRistoranteCarrello == idRistorante) {
                // Se il ristorante è lo stesso, aggiungo il piatto
                PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
            } else {
                // Se il ristorante è diverso, mostro il popup di conferma con Alert
                mostraPopupConferma(idPiatto);
            }
        }
    }

    private void mostraPopupConferma(int idPiatto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attenzione");
        alert.setHeaderText("Hai già piatti di un altro ristorante nel carrello.");
        alert.setContentText("Vuoi svuotare il carrello e procedere con il nuovo ristorante?");

        ButtonType btnProcedi = new ButtonType("Procedi");
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnProcedi, btnAnnulla);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnProcedi) {
                svuotaCarrello(emailCliente);
                SessioneCarrello.setIdRistorante(idRistorante);
                try {
					PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

    private void svuotaCarrello(String emailUtente) {
        try {
            PiattoDAO.getInstance().svuotaCarrello(emailUtente);
            SessioneCarrello.setPieno(false);
            System.out.println("Carrello svuotato.");
        } catch (SQLException e) {
            System.err.println("Errore SQL nello svuotamento del carrello: " + e.getMessage());
        }
    }

    private void switchToPiattoCliente() throws SQLException {
        PiattoCliente piattoClienteScreen = new PiattoCliente(); // La schermata PiattoCliente prenderà l'idPiatto dalla sessione
        this.getScene().setRoot(piattoClienteScreen);
    }

    private void tornaAllaListaMenu() {
        // Supponiamo che tu voglia caricare una schermata di lista menu
        MenuCliente listaMenuScreen = new MenuCliente();  // ListaMenu è la schermata con i menu
        this.getScene().setRoot(listaMenuScreen);    // Cambia la scena per mostrare la lista dei menu
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
