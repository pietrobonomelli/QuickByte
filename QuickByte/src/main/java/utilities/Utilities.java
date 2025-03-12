package utilities;

import com.pavlobu.emojitextflow.EmojiTextFlow;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Classe di utilità per la creazione di elementi dell'interfaccia utente.
 */
public class Utilities {

    /**
     * Mostra un alert informativo con un titolo e un messaggio specificati.
     *
     * @param title   Il titolo dell'alert.
     * @param message Il messaggio da visualizzare nell'alert.
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Crea un VBox contenente un'etichetta e un campo di testo.
     *
     * @param labelText  Il testo dell'etichetta.
     * @param promptText Il testo del prompt del campo di testo.
     * @param field      Il campo di testo da aggiungere al VBox.
     * @return Il VBox creato.
     */
    public static VBox createFieldBox(String labelText, String promptText, TextField field) {
        VBox fieldBox = new VBox();
        Text label = new Text(labelText);
        label.getStyleClass().add("label");
        field.setPromptText(promptText);
        field.getStyleClass().add("text-field");
        field.setMaxWidth(280);
        fieldBox.getChildren().addAll(label, field);
        fieldBox.getStyleClass().add("field-box");
        return fieldBox;
    }

    /**
     * Crea un'etichetta con un testo e una classe di stile specificati.
     *
     * @param text       Il testo dell'etichetta.
     * @param styleClass La classe di stile da applicare all'etichetta.
     * @return L'etichetta creata.
     */
    public static Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Crea un pulsante con un'emoji e un'azione specificata.
     *
     * @param text   Il testo del pulsante.
     * @param emoji  L'emoji da visualizzare sul pulsante.
     * @param action L'azione da eseguire quando il pulsante viene premuto.
     * @return Il pulsante creato.
     */
    public static Button createButtonEmoji(String text, String emoji, Runnable action) {
        Button button = new Button(text);
        EmojiTextFlow emojiTextFlow = new EmojiTextFlow();
        emojiTextFlow.parseAndAppend(emoji);
        button.setGraphic(emojiTextFlow);
        button.setOnAction(event -> action.run());
        button.getStyleClass().add("table-button-emoji");
        return button;
    }

    /**
     * Crea un pulsante con un testo e un'azione specificata.
     *
     * @param text   Il testo del pulsante.
     * @param action L'azione da eseguire quando il pulsante viene premuto.
     * @return Il pulsante creato.
     */
    public static Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.getStyleClass().add("button");
        return button;
    }

    /**
     * Crea un pulsante di logout con un testo e un'azione specificata.
     *
     * @param text   Il testo del pulsante.
     * @param action L'azione da eseguire quando il pulsante viene premuto.
     * @return Il pulsante creato.
     */
    public static Button createButtonLogout(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.getStyleClass().add("button-logout");
        return button;
    }

    /**
     * Crea un campo di testo con un prompt specificato.
     *
     * @param prompt Il testo del prompt.
     * @return Il campo di testo creato.
     */
    public static TextField creaCampoTesto(String prompt) {
        TextField campoTesto = new TextField();
        campoTesto.setPromptText(prompt);
        return campoTesto;
    }
}
