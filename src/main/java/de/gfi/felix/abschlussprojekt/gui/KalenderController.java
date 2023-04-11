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

public class KalenderController extends Controller {
    @FXML
    private Button btAnnehmen;
    @FXML
    private ComboBox cbGruppenauswahl;
    @FXML
    private ComboBox cbStatusAuswahl;
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
    private TableColumn<KalenderTag, Boolean> tcEssenVerfuegbar;
    @FXML
    private TableColumn<KalenderTag, Character> tcGruppenStatus;
    @FXML
    private TableColumn<KalenderTag, Integer> tcGruppenBezeichnung;
    @FXML
    private Button btNext;
    @FXML
    private Button btPrevious;
    @FXML
    private Button btPDFErstellen;

    public static void openWindow(Stage parentStage, String title, String fxmlResource) {
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource(fxmlResource));
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ex) {

            //TODO gennerate ERROR popup
            System.out.println("Error beim öffnen von Dialog. KalenderController.openWindow()");
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
        System.out.println("KalenderControllerOn.BtSpeichernClick()");
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("KalenderControllerOn.BtAbbrechenClick()");
    }
    @FXML
    protected void onBtAnnehmenClick() {
        System.out.println("KalenderController.OnBtAnnehmenClick()");
    }
    @FXML
    protected void onBtNextClick() {
        System.out.println("KalenderController.OnBtNextClick()");
    }
    @FXML
    protected void onBtPreviousClick() {
        System.out.println("KalenderController.OnBtPreviousClick()");
    }
    @FXML
    protected void onBtPDFErstellenClick() {
        System.out.println("KalenderController.OnBtPDFErstellenClick()");
    }
    public void initialize() {

    }
}