package gui.cliente;

import javafx.scene.control.*;
import gui.main.*;
import javafx.scene.layout.*;
import sessione.*;
import javafx.scene.Scene;
import dao.*;
import model.*;
import java.sql.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

public class CarrelloView extends VBox {

	private VBox container;
	private String emailUtente;

	public CarrelloView() {
		super(10);
		this.setStyle("-fx-padding: 10;");

		this.emailUtente = SessioneUtente.getEmail();

		// Aggiungi un titolo alla vista (solo una volta)
		Label titolo = new Label("Carrello del Cliente");
		titolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		this.getChildren().add(titolo); // Titolo aggiunto come primo elemento

		// Carica il contenuto del carrello (senza rimuovere il titolo)
		loadCarrello();       
	}

	private void loadCarrello() {
		// Rimuoviamo solo la tabella e i pulsanti precedenti, non il titolo
		this.getChildren().removeIf(node -> node instanceof TableView || node instanceof HBox);

		TableView<Carrello> table = new TableView<>();
		TableColumn<Carrello, String> colPiatto = new TableColumn<>("Piatto");
		TableColumn<Carrello, Void> colAzioni = new TableColumn<>("Azioni");

		colPiatto.setCellValueFactory(data -> {
			String nomePiatto = "";
			try {
				nomePiatto = CarrelloDAO.getInstance().getNomePiattoById(data.getValue().getIdPiatto());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return new SimpleStringProperty(nomePiatto + " (x" + data.getValue().getQuantitaPiatti() + ")");
		});

		colAzioni.setCellFactory(param -> new TableCell<Carrello, Void>() {
			private final Button addButton = new Button("+");
			private final Button minusButton = new Button("-");
			private final HBox buttonBox = new HBox(5, minusButton, addButton);

			{
				addButton.setOnAction(event -> modificaQuantita(getTableRow().getItem(), 1));
				minusButton.setOnAction(event -> modificaQuantita(getTableRow().getItem(), -1));
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || getTableRow().getItem() == null) {
					setGraphic(null);
				} else {
					setGraphic(buttonBox);
				}
			}
		});

		table.getColumns().addAll(colPiatto, colAzioni);

		try {
			List<Carrello> carrelli = CarrelloDAO.getInstance().getCarrelloByUtente(emailUtente);
			table.getItems().addAll(carrelli);
		} catch (SQLException e) {
			e.printStackTrace();
			Utilities.showAlert("Errore", "Errore nel caricamento del carrello.");
		}

		Button tornaAllaListaButton = new Button("⬅ HOME");
		tornaAllaListaButton.setOnAction(event -> tornaAllaHome());

		Button confermaOrdineButton = new Button("CONFERMA ORDINE");
		confermaOrdineButton.setOnAction(event -> {
			try {
				confermaOrdine();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		HBox buttonBox = new HBox(10, tornaAllaListaButton, confermaOrdineButton);

		// Aggiungi la tabella e i pulsanti alla VBox senza rimuovere il titolo
		this.getChildren().addAll(table, buttonBox);
	}
	
	private void modificaQuantita(Carrello item, int delta) {
		try {
			int nuovaQuantita = item.getQuantitaPiatti() + delta;
			if (nuovaQuantita > 0) {
				CarrelloDAO.getInstance().aggiornaQuantita(item.getIdCarrello(), nuovaQuantita);
			} else {
				rimuoviDalCarrello(item.getIdCarrello());
			}
			loadCarrello();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilities.showAlert("Errore", "Errore durante la modifica della quantità.");
		}
	}

	private void rimuoviDalCarrello(int idCarrello) {
		try {
			CarrelloDAO.getInstance().rimuoviDalCarrello(idCarrello);
			loadCarrello();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilities.showAlert("Errore", "Errore durante la rimozione dal carrello.");
		}
	}

	private void tornaAllaHome() {
        MainScreenCliente mainScreenCliente = new MainScreenCliente();
        this.getScene().setRoot(mainScreenCliente);
    }
	
	public String getDataOraCorrente() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return now.format(formatter);
	}

	private void confermaOrdine() throws SQLException {
		if (SessioneCarrello.getPieno()) {
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
		} else {
			Utilities.showAlert("Errore", "Carrello vuoto: impossibile effettuare l'ordine");
		}
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
			Utilities.showAlert("Ordine Confermato", "Il tuo ordine è stato pagato con successo!");
			loadCarrello();
		} else {
			Utilities.showAlert("Errore", "Errore durante la conferma dell'ordine.");
		}
	}
}
