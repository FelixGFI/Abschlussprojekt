package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.speicherklassen.KalenderTag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class BetriebsurlaubController extends Controller {
    @FXML
    private Button btSpeichern;
    @FXML
    private Button btAbbrechen;
    @FXML
    private ComboBox cbMonat;
    @FXML
    private ComboBox cbJahr;
    @FXML
    private DatePicker dpVon;
    @FXML
    private DatePicker dpBis;
    @FXML
    private TableView tbTabelle;
    @FXML
    private TableColumn<KalenderTag, LocalDate> tcDatum;
    @FXML
    private TableColumn<KalenderTag, Integer> tcIstBetriebsurlaub;
    @FXML
    private Button btNext;
    @FXML
    private Button btPrevious;
    @FXML
    private Button btUrlaub;
    @FXML
    private Button btArbeit;

    public static void openWindow(Stage parentStage, String title, String fxmlResource) {
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource(fxmlResource));
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ex) {

            //TODO gennerate ERROR popup
            System.out.println("Error beim öffnen von Dialog. BetriebsurlaubController.openWindow()");
            return;
        }
        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.setScene(newScene);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    @FXML
    protected void onBtSpeichernClick() {
        System.out.println("BetriebsurlaubController.OnBtSpeichernClick()");
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("BetriebsurlaubController.OnBtAbbrechenClick()");
    }
    @FXML
    protected void onBtNextClick() {
        System.out.println("BetriebsurlaubController.OnBtNextClick()");
    }
    @FXML
    protected void onBtPreviousClick() {
        System.out.println("BetriebsurlaubController.OnBtFreiClick()");
    }
    @FXML protected void onBtUrlaubClick() {
        System.out.println("BetriebsurlaubController.OnBtUrlaubClick()");
    }
    @FXML protected void onBtArbeitClick() {
        System.out.println("BetriebsurlaubController.OnBtArbeitClick()");
    }
    public void initialize() {

    }
}