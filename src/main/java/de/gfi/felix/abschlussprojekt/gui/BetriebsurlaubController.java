package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class BetriebsurlaubController extends Controller {
    @FXML
    private Button btSpeichern;
    @FXML
    private Button btAbbrechen;
    @FXML
    private ComboBox<Month> cbMonat;
    @FXML
    private ComboBox<Integer> cbJahr;
    @FXML
    private DatePicker dpVon;
    @FXML
    private DatePicker dpBis;
    @FXML
    private TableView tbTabelle;
    @FXML
    private TableColumn<BetriebsurlaubsTag, LocalDate> tcDatum;
    @FXML
    private TableColumn<BetriebsurlaubsTag, Integer> tcIstBetriebsurlaub;
    @FXML
    private Button btNext;
    @FXML
    private Button btPrevious;
    @FXML
    private Button btUrlaub;
    @FXML
    private Button btArbeit;

    public static void openWindow(Stage parentStage, String title, String fxmlResource) throws SQLException {
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
    @FXML
    protected void onBtUrlaubClick() {
        System.out.println("BetriebsurlaubController.OnBtUrlaubClick()");
    }
    @FXML
    protected void onBtArbeitClick() {
        System.out.println("BetriebsurlaubController.OnBtArbeitClick()");
        ArrayList<BetriebsurlaubsTag> testListe = new ArrayList();
        testListe.add(new BetriebsurlaubsTag(LocalDate.now(), 0));
        testListe.add(new BetriebsurlaubsTag(LocalDate.now(), 1));
        testListe.add(new BetriebsurlaubsTag(LocalDate.now(), 2));
        tbTabelle.getItems().setAll(testListe);
        tbTabelle.refresh();
    }
    @FXML
    protected void onCbMonatAction() {
        System.out.println("BetriebsurlaubController.onCbMonatAction()");
    }
    @FXML
    protected void onCbJahrAction() {
        System.out.println("BetriebsurlaubController.onCbJahrAction()");
    }
    public void initialize() {
        DatenbankCommunicator.establishConnection();

        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configureLocalDateTableColum(tcDatum, "datum");
        configureIntegerColumn(tcIstBetriebsurlaub, "istBetriebsurlaub");
        configureJahrCombobox(cbJahr);
        configureMonatCombobox(cbMonat);
    }
}