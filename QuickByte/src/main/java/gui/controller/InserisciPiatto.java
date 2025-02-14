package gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import database.DatabaseConnection;
import java.sql.*;

public class InserisciPiatto extends VBox {
    public InserisciPiatto(String nomeMenu) {
        super(10);
        this.setStyle("-fx-padding: 10;");

        Label nomeLabel = new Label("Nome:");
        TextField nomeField = new TextField();

        Label disponibilitaLabel = new Label("Disponibilità:");
        CheckBox disponibilitaCheck = new CheckBox();

        Label prezzoLabel = new Label("Prezzo:");
        TextField prezzoField = new TextField();

        Label allergeniLabel = new Label("Allergeni:");
        TextField allergeniField = new TextField();

        Label fotoLabel = new Label("Foto:");
        TextField fotoField = new TextField();

        Label menuLabel = new Label("Nome Menu: " + nomeMenu);

        Button confermaButton = new Button("Conferma");
        confermaButton.setOnAction(e -> inserisciPiatto(nomeField.getText(), disponibilitaCheck.isSelected(), prezzoField.getText(), allergeniField.getText(), fotoField.getText(), nomeMenu));

        this.getChildren().addAll(nomeLabel, nomeField, disponibilitaLabel, disponibilitaCheck, prezzoLabel, prezzoField, allergeniLabel, allergeniField, fotoLabel, fotoField, menuLabel, confermaButton);
    }

    private void inserisciPiatto(String nome, boolean disponibilita, String prezzo, String allergeni, String foto, String nomeMenu) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO Piatto (nome, disponibilita, prezzo, allergeni, foto, nomeMenu) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nome);
                stmt.setBoolean(2, disponibilita);
                stmt.setString(3, prezzo);
                stmt.setString(4, allergeni);
                stmt.setString(5, foto);
                stmt.setString(6, nomeMenu);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}