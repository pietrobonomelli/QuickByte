package gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import java.sql.*;

public class GestioneRistoranti extends VBox {

    private VBox container; // Contenitore verticale per i ristoranti

    public GestioneRistoranti() {
        super(10); // Spazio tra gli elementi
        this.setStyle("-fx-padding: 10;");

        // Inizializza container PRIMA di chiamare loadRistoranti()
        container = new VBox(10);
        
        // Carica i ristoranti dal database
        loadRistoranti();

        // Aggiungi il container alla vista
        this.getChildren().add(container);

        // Crea una HBox per i pulsanti (Torna alla pagina principale e Inserisci nuovo ristorante)
        HBox buttonContainer = new HBox(10); // 10 è la distanza tra i pulsanti
        buttonContainer.setStyle("-fx-padding: 10;");

        // Pulsante "Torna alla pagina principale"
        Button tornaButton = new Button("Torna alla pagina principale");
        tornaButton.setOnAction(e -> switchToMainScreen()); // Torna alla pagina principale
        buttonContainer.getChildren().add(tornaButton);

        // Pulsante "Inserisci nuovo Ristorante"
        Button inserisciRistoranteButton = new Button("Inserisci nuovo Ristorante");
        inserisciRistoranteButton.setOnAction(e -> switchToInserisciRistorante());
        buttonContainer.getChildren().add(inserisciRistoranteButton);

        // Aggiungi la HBox con i pulsanti alla vista principale
        this.getChildren().add(buttonContainer);
    }

    private void loadRistoranti() {
        String emailTitolare = SessioneUtente.getEmail(); // Ottieni l'email dell'utente dalla sessione

        // Connessione al database e query per recuperare i ristoranti dell'utente
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT nome FROM Ristorante WHERE emailTitolare = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, emailTitolare); // Imposta l'email del titolare

                ResultSet rs = stmt.executeQuery();

                ObservableList<String> ristoranti = FXCollections.observableArrayList();
                while (rs.next()) {
                    String nomeRistorante = rs.getString("nome");
                    ristoranti.add(nomeRistorante); // Aggiungi il nome del ristorante alla lista
                }

                // Carica i ristoranti nell'interfaccia
                for (String ristorante : ristoranti) {
                    // Crea il box per ogni ristorante
                    HBox ristoranteBox = new HBox(10);
                    ristoranteBox.setStyle("-fx-padding: 10;");

                    // Etichetta con il nome del ristorante
                    Label nomeRistorante = new Label(ristorante);

                    // Pulsante per eliminare
                    Button eliminaButton = new Button("Elimina");
                    eliminaButton.setOnAction(e -> eliminaRistorante(ristorante));

                    // Pulsante per modificare il menù
                    Button modificaMenuButton = new Button("Modifica Menù");
                    modificaMenuButton.setOnAction(e -> modificaMenu(ristorante));

                    // Aggiungi gli elementi al box
                    ristoranteBox.getChildren().addAll(nomeRistorante, eliminaButton, modificaMenuButton);

                    // Aggiungi il box alla vista principale
                    container.getChildren().add(ristoranteBox);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Errore", "Errore di connessione al database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    private void eliminaRistorante(String ristorante) {
        // Logica per eliminare un ristorante dal database
        System.out.println("Eliminando ristorante: " + ristorante);
        // Codice per eliminare il ristorante dal database
    }

    private void modificaMenu(String ristorante) {
        // Logica per modificare il menù di un ristorante
        System.out.println("Modificando menù per: " + ristorante);
        // Codice per modificare il menù del ristorante
    }

    private void switchToInserisciRistorante() {
        InserisciRistorante inserisciRistoranteScreen = new InserisciRistorante();
        this.getScene().setRoot(inserisciRistoranteScreen); // Imposta direttamente la nuova schermata
    }

    private void switchToMainScreen() {
        // Logica per tornare alla pagina principale, ad esempio passando alla schermata principale
        MainScreenTitolare mainScreenTitolare = new MainScreenTitolare();
        this.getScene().setRoot(mainScreenTitolare);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getContainer() {
        return container; // Restituisce il VBox contenente la lista dei ristoranti
    }
}
