package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

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
     * Zum Aufrufen dieses Controllers sollte außschließlich diese Methode verwendet werden. Ansonsten kann es zu Fehlern kommen.
     * @param parentStage stage des Aufrufenden Fensters
     * @param title titel des zu Zeigenden Fensters als String
     * @param fxmlResource dateipfad der für den Controller verwendeten fxml Datei als String
     * @throws IOException
     */
    public static void openWindow(Stage parentStage, String title, String fxmlResource) {
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource(fxmlResource));
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ex) {
            createAndShowErrorAlert("Fehler", "FXML Ladefehler", "Beim laden des Dialogs ist ein Fehler aufgetreten.");
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
        datenWurdenBearbeitet = false;
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("BetriebsurlaubController.OnBtAbbrechenClick()");
        if(datenWurdenBearbeitet) {
            System.out.println("Get Nutzerbestätigung");
            if(!getNutzerbestätigung()) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }
    @FXML
    protected void onBtNextClick() {
        System.out.println("BetriebsurlaubController.OnBtNextClick()");
        handleBtNextClick(cbMonat, cbJahr, tbTabelle);
    }
    @FXML
    protected void onBtPreviousClick() {
        System.out.println("BetriebsurlaubController.OnBtFreiClick()");
        handleBtPriviousClick(cbMonat, cbJahr, tbTabelle);
    }
    @FXML
    protected void onBtUrlaubClick() {
        System.out.println("BetriebsurlaubController.OnBtUrlaubClick()");
        for (BetriebsurlaubsTag tag : (ObservableList<BetriebsurlaubsTag>) tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getIstBetriebsurlaub() != 2) {
                tag.setIstBetriebsurlaub(1);
                datenWurdenBearbeitet = true;
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
                datenWurdenBearbeitet = true;
            }
        }
        tbTabelle.refresh();
    }
    @FXML
    protected void onCbMonatAction() {
        System.out.println("BetriebsurlaubController.onCbMonatAction()");
        handleCbMonatAction(cbMonat, tbTabelle);
    }
    //TODO Add Documentation
    @FXML
    protected void onCbJahrAction() throws SQLException {
        System.out.println("BetriebsurlaubController.onCbJahrAction()");
        if(ingnorEventCbJahr) {
            ingnorEventCbJahr = false;
            return;
        }

        if(tbTabelle.getItems().isEmpty()) {
            return;
        }
        if(datenWurdenBearbeitet) {
            if(!getNutzerbestätigung()) {
                ingnorEventCbJahr = true;
                Integer altesJahr = ((ObservableList<Tag>) tbTabelle.getItems()).get(0).getDatum().getYear();
                cbJahr.getSelectionModel().select(altesJahr);
                return;
            }
        }
        tbTabelle.getItems().setAll(DatenbankCommunicator.dbAbfrageBetriebsurlaubTage(cbJahr.getSelectionModel().getSelectedItem()));
        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
        tbTabelle.refresh();
        datenWurdenBearbeitet = false;

        scrollToFirstOfMonthAndYear(cbJahr.getSelectionModel().getSelectedItem(), cbMonat.getSelectionModel().getSelectedItem(), tbTabelle);
    }
    @FXML
    protected void onDpVonAction() {
        handleDpVon(dpVon, dpBis, tbTabelle);
    }
    @FXML
    protected void onDpBisAction() {
        handleDpBis(dpVon, dpBis, tbTabelle);
    }
    public void initialize() throws SQLException {
        DatenbankCommunicator.establishConnection();

        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configureLocalDateTableColum(tcDatum, "datum");
        configureIntegerColumn(tcIstBetriebsurlaub, "istBetriebsurlaub");
        configureJahrCombobox(cbJahr);
        configureMonatCombobox(cbMonat);

        tbTabelle.getItems().setAll(DatenbankCommunicator.dbAbfrageBetriebsurlaubTage(cbJahr.getSelectionModel().getSelectedItem()));
        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
        tbTabelle.refresh();

        tbTabelle.addEventFilter(ScrollEvent.SCROLL, event ->
                handleScrollEvent(event, cbMonat, tbTabelle));

        tbTabelle.sceneProperty().addListener((obs, oldScene, newScene) -> Platform.runLater(() -> {
            Stage stage = (Stage) newScene.getWindow();
            stage.setOnCloseRequest(e -> {
                if(datenWurdenBearbeitet) {
                    if(!getNutzerbestätigung()) e.consume();
                }
            });
        }));
    }
}