package de.gfi.felix.abschlussprojekt;

import de.gfi.felix.abschlussprojekt.gui.BetriebsurlaubController;
import de.gfi.felix.abschlussprojekt.gui.KalenderController;
import de.gfi.felix.abschlussprojekt.gui.KuechenController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MenueController {
    @FXML private Button btKalender;
    @FXML private Button btKueche;
    @FXML private Button btBetriebsurlaub;
    Stage stage;

    @FXML
    protected void onBtBetriebsurlaubClick() throws IOException, SQLException {
        BetriebsurlaubController.openWindow(stage, "Betriebsurlaub", "betriebsurlaub-view.fxml");
    }
    @FXML
    protected void onBtKuecheClick() throws SQLException {
        KuechenController.openWindow(stage, "Kuechenplanung", "kuechen-view.fxml");
    }
    @FXML
    protected void onBtKalenderClick() throws IOException {
        KalenderController.openWindow(stage, "Gruppenkalender", "kalender-view.fxml");
    }
    public void showDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menue-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(scene);
        stage.show();
    }
    public void initialize() {}
}