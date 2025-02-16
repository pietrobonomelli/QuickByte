package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import database.DatabaseConnection;
import gui.main.*;
import java.sql.*;

public class PiattiCliente extends VBox {

    private String nomeMenu;
    private int idRistorante;

    public PiattiCliente() {
        super(10);

        this.idRistorante = SessioneRistorante.getId();
        this.nomeMenu = SessioneMenu.getNome();  // Recupera il nome del menu selezionato dalla sessione

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
        // TODO Qui puoi implementare la logica per aggiungere il piatto al carrello
        // Ad esempio, potresti avere un carrello in sessione o un'altra struttura dati
        // Questo è solo un esempio di base:
        System.out.println("Piatto " + idPiatto + " aggiunto al carrello.");
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
