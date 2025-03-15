package gui.main;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import utilities.Utilities;

import org.mindrot.jbcrypt.BCrypt;

import dao.UtenteDAO;
import gui.cliente.MainScreenCliente;
import gui.corriere.MainScreenCorriere;
import gui.titolare.MainScreenTitolare;
import model.Utente;

public class ModificaProfilo extends VBox {
	private TextField passwordField, nomeField, telefonoField;	//campi che l'utente può modificare
    private String emailUtente = SessioneUtente.getEmail();

    public ModificaProfilo(){
        super(10);
        this.setStyle("-fx-padding: 10;");

        // Titolo grande
        Label titolo = Utilities.createLabel("Modifica il profilo di: " + emailUtente, "title");

        // Crea un HBox per il titolo
        HBox header = new HBox(10);
        header.getChildren().addAll(titolo);
        header.setStyle("-fx-padding: 10;");
        
        
        // Campi di input con i rispettivi Label

        passwordField = new TextField();
        VBox passwordBox = Utilities.createFieldBox("Password", "Password", passwordField);

        nomeField = new TextField();
        VBox nomeBox = Utilities.createFieldBox("Nome", "Nome", nomeField);

        telefonoField = new TextField();
        VBox telefonoBox = Utilities.createFieldBox("Numero di Telefono", "Numero di telefono", telefonoField);


        // Carica i dati attuali
        caricaDatiUtente();

        //Pulsante per eliminare il profilo
        Button pulsanteElimina = Utilities.createButtonLogout("Elimina Profilo", this::eliminaUtente);
        
        // Pulsante per salvare le modifiche
        Button salvaButton = Utilities.createButton("Salva Modifiche", this::salvaModifiche);

        // Pulsante per tornare indietro
        Button tornaButton = Utilities.createButton("Torna indietro", () -> {
            tornaIndietro();
        });


        // Aggiunta degli elementi al layout
        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");
        buttonContainer.getChildren().addAll(salvaButton, tornaButton);

        this.getChildren().addAll(header, passwordBox, nomeBox, telefonoBox, buttonContainer);
    }

    private void caricaDatiUtente() {
        Utente utente = UtenteDAO.getInstance().getUtenteByEmail(emailUtente);
		if (utente != null) {
			passwordField.setText("********");
		    nomeField.setText(utente.getNome());
		    telefonoField.setText(utente.getTelefono());
		    
		} else {
		    Utilities.showAlert("Errore", "Utente non trovato.");
		}
    }

    /*
     * Salva le modifiche del ristorante.
     */
    private void salvaModifiche() {
        String nuovoNome = nomeField.getText();
        String nuovoTelefono = telefonoField.getText();
        String nuovaPassword = passwordField.getText();
        
        //hasha la password
    	String nuovaHashedPassword = BCrypt.hashpw(nuovaPassword, BCrypt.gensalt(12));

        if (nuovoNome.isEmpty() || nuovoTelefono.isEmpty() || nuovaPassword.isEmpty()) {
            Utilities.showAlert("Errore", "Tutti i campi devono essere compilati.");
            return;
        }

        boolean success = UtenteDAO.getInstance().updateUtente(emailUtente, nuovaHashedPassword, nuovoNome, nuovoTelefono);

		if (success) {
		    Utilities.showAlert("Successo", "Utente modificato con successo!");
		    tornaIndietro();
		} else {
		    Utilities.showAlert("Errore", "Modifica non riuscita.");
		}
    }
    
    
    /*
     * Torna alla main screen giusta in base al tipo di utente che è
     */
    private void tornaIndietro() {
        String tipoUtente = UtenteDAO.getInstance().getTipoUtenteByEmail(emailUtente);

        switch (tipoUtente) {
            case "Cliente":
                MainScreenCliente mainScreenCliente = new MainScreenCliente();
                mainScreenCliente.getStylesheets().add("style/style.css");
                this.getScene().setRoot(mainScreenCliente);
                break;

            case "Titolare":
                MainScreenTitolare mainScreenTitolare = new MainScreenTitolare();
                mainScreenTitolare.getStylesheets().add("style/style.css");
                this.getScene().setRoot(mainScreenTitolare);
                break;

            case "Corriere":
                MainScreenCorriere mainScreenCorriere = new MainScreenCorriere();
                mainScreenCorriere.getStylesheets().add("style/style.css");
                this.getScene().setRoot(mainScreenCorriere);
                break;

            default:
                System.out.println("Tipo di utente non riconosciuto.");
                break;
        }
    }
    
    /*
     * Elimina l'utente
     */
    private void eliminaUtente() {
        if(UtenteDAO.getInstance().deleteUtente(emailUtente)) {
        	Utilities.showAlert("Successo", "Utente eliminato con successo!");
		    //vado al login screen
        	Scene currentScene = getScene();
            LoginScreen loginScreen = new LoginScreen();
            currentScene.setRoot(loginScreen);
        }else {
        	Utilities.showAlert("Errore", "Modifica non riuscita.");
        }
        
    }


}