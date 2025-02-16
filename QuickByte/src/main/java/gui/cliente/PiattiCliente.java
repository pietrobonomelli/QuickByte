package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import database.DatabaseConnection;
import gui.main.*;
import java.sql.*;

public class PiattiCliente extends VBox {

	private String emailCliente;
    private String nomeMenu;
    private int idRistorante;

    public PiattiCliente() {
        super(10);

        this.idRistorante = SessioneRistorante.getId();
        this.nomeMenu = SessioneMenu.getNome();  // Recupera il nome del menu selezionato dalla sessione
        this.emailCliente = SessioneUtente.getEmail();

        this.setStyle("-fx-padding: 10;");
        loadPiatti(idRistorante, nomeMenu);
    }

    private void loadPiatti(int idRistorante, String nomeMenu) {
        try (Connection conn = DatabaseConnection.connect()) {
            // Query per recuperare i piatti associati all'idRistorante e al nomeMenu
            String query = "SELECT idPiatto, nome FROM Piatto WHERE idRistorante = ? AND nomeMenu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idRistorante);
                stmt.setString(2, nomeMenu);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int idPiatto = rs.getInt("idPiatto");
                    String nomePiatto = rs.getString("nome");

                    // Crea un box per ogni piatto
                    HBox piattoBox = new HBox(10);
                    piattoBox.setStyle("-fx-padding: 10;");

                    // Crea l'etichetta con il nome del piatto
                    Label nomeLabel = new Label(nomePiatto);

                    // Crea il pulsante per aggiungere al carrello
                    Button aggiungiCarrelloButton = new Button("Aggiungi al carrello");

                    // Quando clicchi sul nome del piatto, salva l'idPiatto nella sessione e vai alla pagina PiattoCliente
                    nomeLabel.setOnMouseClicked(event -> {
                        SessionePiatto.setId(idPiatto);  // Salviamo l'ID del piatto nella sessione
                        switchToPiattoCliente();          // Vai alla pagina PiattoCliente
                    });

                    // Quando clicchi sul pulsante "Aggiungi al carrello", passiamo l'idPiatto al metodo
                    aggiungiCarrelloButton.setOnAction(event -> aggiungiAlCarrello(idPiatto));

                    // Aggiungi gli elementi al box
                    piattoBox.getChildren().addAll(nomeLabel, aggiungiCarrelloButton);
                    this.getChildren().add(piattoBox);
                }
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

 
    
    private void aggiungiAlCarrello(int idPiatto) {
        boolean pieno = SessioneCarrello.getPieno();
        int idRistoranteCarrello = SessioneCarrello.getIdRistorante();

        if (!pieno) {
            // Se il carrello è vuoto, imposto l'idRistorante in sessione e aggiungo il piatto
            SessioneCarrello.setIdRistorante(idRistorante);
            SessioneCarrello.setPieno(true);
            aggiungiPiattoAlCarrello(idPiatto);
        } else {
            if (idRistoranteCarrello == idRistorante) {
                // Se il ristorante è lo stesso, aggiungo il piatto
                aggiungiPiattoAlCarrello(idPiatto);
            } else {
                // Se il ristorante è diverso, mostro il popup di conferma con Alert
                mostraPopupConferma(idPiatto);
            }
        }
    }

    private void aggiungiPiattoAlCarrello(int idPiatto) {
        String sql = "INSERT INTO Carrello (quantitaPiatti, idPiatto, ordine, emailUtente) " +
                     "VALUES (1, ?, NULL, ?) " +
                     "ON CONFLICT (idPiatto, emailUtente) DO UPDATE SET quantitaPiatti = quantitaPiatti + 1";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPiatto);
            pstmt.setString(2, this.emailCliente);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Piatto aggiunto al carrello.");
            } else {
                System.out.println("Errore nell'aggiunta del piatto al carrello.");
            }

        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
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
                svuotaCarrello(this.emailCliente);
                SessioneCarrello.setIdRistorante(idRistorante);
                aggiungiPiattoAlCarrello(idPiatto);
            }
        });
    }

    private void svuotaCarrello(String emailUtente) {
        String sql = "DELETE FROM Carrello WHERE emailUtente = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emailUtente);
            pstmt.executeUpdate();

            SessioneCarrello.setPieno(false);
            System.out.println("Carrello svuotato.");
        } catch (SQLException e) {
            System.err.println("Errore SQL nello svuotamento del carrello: " + e.getMessage());
        }
    }






    private void switchToPiattoCliente() {
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
