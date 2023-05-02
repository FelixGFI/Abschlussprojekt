package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.collections.ObservableList;
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

    /**
     * diese Methode ist zur Öffnung eines Fensters dieser controllerKlasse aus einem anderen Fenster (Hauptmenü) da.
     * @param parentStage stage des Aufrufenden Fensters
     * @param title titel des zu Zeigenden Fensters als String
     * @param fxmlResource dateipfad der für den Controller verwendeten fxml Datei als String
     * @throws IOException
     */
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
    protected void onBtSpeichernClick() throws SQLException {
        System.out.println("BetriebsurlaubController.OnBtSpeichernClick()");
        DatenbankCommunicator.dbSpeichernBetriebsurlaubTage(tbTabelle.getItems());
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
        for (BetriebsurlaubsTag tag : (ObservableList<BetriebsurlaubsTag>) tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getIstBetriebsurlaub() != 2) {
                tag.setIstBetriebsurlaub(1);
            }
        }
        tbTabelle.refresh();
    }

    @FXML
    protected void onBtArbeitClick() {
        System.out.println("BetriebsurlaubController.OnBtArbeitClick()");
        for (BetriebsurlaubsTag tag : (ObservableList<BetriebsurlaubsTag>) tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getIstBetriebsurlaub() != 2) {
                tag.setIstBetriebsurlaub(0);
            }
        }
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
    public void initialize() throws SQLException {
        DatenbankCommunicator.establishConnection();

        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configureLocalDateTableColum(tcDatum, "datum");
        configureIntegerColumn(tcIstBetriebsurlaub, "istBetriebsurlaub");
        configureJahrCombobox(cbJahr);
        configureMonatCombobox(cbMonat);

        tbTabelle.getItems().setAll(DatenbankCommunicator.dbAbfrageBetriebsurlaubTage(cbJahr.getSelectionModel().getSelectedItem()));
    }
}