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

    /**
     * speichert alle Änderungen die der Nutzer für die Angeziegten Daten vorgenommen hat in der Datenbank und setzt
     * den Boolen der anzeigt ob ungespeicherte Änderungen vorhanden sein könnten auf False
     * @throws SQLException
     */
    @FXML
    protected void onBtSpeichernClick() throws SQLException {
        System.out.println("BetriebsurlaubController.OnBtSpeichernClick()");
        DatenbankCommunicator.dbSpeichernBetriebsurlaubTage(tbTabelle.getItems());
        datenWurdenBearbeitet = false;
    }

    /**
     * Schließt das Fenster. Wenn Änderungen des Nutzers durch verlassen des dialoges
     * Verworfen werden könnten holt die Methode vor dem Verlassen Nutzerbestätigung ein.
     */
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("BetriebsurlaubController.OnBtAbbrechenClick()");
        if(datenWurdenBearbeitet) {
            System.out.println("Get Nutzerbestätigung");
            if(!getNutzerbestaetigung("Dialog beenden und Änderungen verwerfen?")) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }

    /**
     * wechselt zum nächsten Monat
     */
    @FXML
    protected void onBtNextClick() {
        System.out.println("BetriebsurlaubController.OnBtNextClick()");
        handleBtNextClick(cbMonat, cbJahr, tbTabelle);
    }

    /**
     * wechselt zum Vormoant
     */
    @FXML
    protected void onBtPreviousClick() {
        System.out.println("BetriebsurlaubController.OnBtFreiClick()");
        handleBtPriviousClick(cbMonat, cbJahr, tbTabelle);
    }

    /**
     * Setzt für alle in der Tabelel ausgewählten Tage den Betriebsurlaubsstatus (istBetriebsurlaub) auf 1,
     * stellt also ein das die ausgewählten Tage Betriebsurlaubstage sind. Ausnahme sind gesetztliche
     * Feiertage welche vom Nuter nicht abgeendert werden können (istBetriebsurlaub = 2 : gesetzlicher Feiertag).
     */
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

    /**
     * Setzt für alle in der Tabelel ausgewählten Tage den Betriebsurlaubsstatus (istBetriebsurlaub) auf 0,
     * stellt also ein das die ausgewählten Tage KEINE Betriebsurlaubstage sind. Ausnahme sind gesetztliche
     * Feiertage welche vom Nuter nicht abgeendert werden können (istBetriebsurlaub = 2 : gesetzlicher Feiertag).
     */
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

    /**
     * führt das Eventhandlung bei Änderung des Monats in der Combobox<Month> cbMonat entsprechend der
     * aufgerufenen Methode aus.
     */
    @FXML
    protected void onCbMonatAction() {
        System.out.println("BetriebsurlaubController.onCbMonatAction()");
        handleCbMonatAction(cbMonat, tbTabelle);
    }

    /**
     * wird ausgelöst wenn ein anderes Jahr in der cbJahr ausgewählt wird. Überprüft ob
     * daten editiert worden sein könnten und fragt, wenn ja Nuterbestätigung ab. Wird diese
     * nicht gewehrt so wird in der cbJahr das momentan in der Tabelle angezeigte Jahr
     * ausgewählt und die Methode dan beendet. Damit durch das auswählen des in der Tabelle
     * angeziegten jahres onCBJahrAction nicht erneut ausgeführt wird wird der
     * Boolean ingnorEventCbJahr verwendet. Wenn diese Methode nicht vorher beendet wird
     * fragte sie die Daten für das vom Nutzer neu ausgewählte jahr ab und befüllt
     * die Datenbank damit.
     * @throws SQLException
     */
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
            if(!getNutzerbestaetigung("Daten für anderes Jahr laden und Änderungen verwerfen?")) {
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

    /**
     * führt das Eventhandlung bei Eingabe in den Datepicker dpVon entsprechend der aufgerufenen Methode aus.
     */
    @FXML
    protected void onDpVonAction() {
        handleDpVon(dpVon, dpBis, tbTabelle);
    }

    /**
     * führt das Eventhandlung bei Eingabe in den Datepicker dpBis entsprechend der aufgerufenen Methode aus.
     */
    @FXML
    protected void onDpBisAction() {
        handleDpBis(dpVon, dpBis, tbTabelle);
    }

    /**
     * diese Initialize Methode setzt alle gui Elemente auf die Aufgesetzt werden müsssen. Sie stellt die Verbindung
     * zur Datenbank her und legt fest was getan werden soll wenn der User in der Tabelle scrollt und
     * was passieren soll wenn der user durch clicken auf das Kreuz in der rechten Oberen ecke den Dialog verlässt.
     * befüllt die TableView mit den Daten des Akktuellen Jahres
     * @throws SQLException
     */
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
                    if(!getNutzerbestaetigung("Dialog beenden und Änderungen verwerfen?")) e.consume();
                }
            });
        }));
    }
}