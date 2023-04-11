package de.gfi.felix.abschlussprojekt.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class KuechenController extends Controller {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}