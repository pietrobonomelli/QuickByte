package gui.controller;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public abstract class MainScreen extends VBox {
    
    public MainScreen() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
    }
}
