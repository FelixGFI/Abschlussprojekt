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

public class KuechenController extends Controller {
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
    private TableColumn<KuechenTag, LocalDate> tcDatum;
    @FXML
    private TableColumn<KuechenTag, Integer> tcKuechenStatus;
    @FXML
    private Button btNext;
    @FXML
    private Button btPrevious;
    @FXML
    private Button btOffen;
    @FXML
    private Button btGeschlossen;

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
        System.out.println("KuechenController.OnBtSpeichernClick()");
        DatenbankCommunicator.dbSpeichernKuechenTage(tbTabelle.getItems());
        datenWurdenBearbeitet = false;
    }

    /**
     * Schließt das Fenster. Wenn Änderungen des Nutzers durch verlassen des dialoges
     * Verworfen werden könnten holt die Methode vor dem Verlassen Nutzerbestätigung ein.
     */
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("KuechenController.OnBtAbbrechenClick()");
        if(datenWurdenBearbeitet) {
            System.out.println("Get Nutzerbestätigung");
            if(!getNutzerbestaetigung("Dialog beenden und Änderungen verwerfen?")) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }

    /**
     * wechselt zum Nächsten Monat
     */
    @FXML
    protected void onBtNextClick() {
        System.out.println("KuechenController.OnBtNextClick()");
        handleBtNextClick(cbMonat, cbJahr, tbTabelle);
    }

    /**
     * wechselt zum Vormonat
     */
    @FXML
    protected void onBtPreviousClick() {
        handleBtPriviousClick(cbMonat, cbJahr, tbTabelle);
        System.out.println("KuechenController.OnBtPreviousClick()");
    }

    /**
     * Setzt für alle in der Tabelel ausgewählten Tage den kuechenStatus auf auf 1, stellt also ein das
     * Die Küche am besagten Tag geöffnet ist. Ausnahme sind gesetztliche Feiertage welche vom
     * Nuter nicht abgeendert werden können (istBetriebsurlaub = 2 : gesetzlicher Feiertag).
     */
    @FXML
    protected void onBtOffenClick() {
        System.out.println("KuechenController.OnBtOffenClick");
        for (KuechenTag tag : (ObservableList<KuechenTag>) tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getKuechenStatus() != 2) {
                tag.setKuechenStatus(1);
                datenWurdenBearbeitet = true;
            }
        }
        tbTabelle.refresh();
    }

    /**
     * Setzt für alle in der Tabelel ausgewählten Tage den kuechenStatus auf auf 0, stellt also ein das
     * Die Küche am besagten Tag NICHT geöffnet ist. Ausnahme sind gesetztliche Feiertage welche vom
     * Nuter nicht abgeendert werden können (istBetriebsurlaub = 2 : gesetzlicher Feiertag).
     */
    @FXML
    protected void onBtGeschlossenClick() {
        System.out.println("KuechenController.OnBtGeschlossenClick");
        for (KuechenTag tag : (ObservableList<KuechenTag>) tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getKuechenStatus() != 2) {
                tag.setKuechenStatus(0);
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
        System.out.println("KuechenController.onCbJahrAction()");
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

        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
        tbTabelle.refresh();
        datenWurdenBearbeitet = false;
        tbTabelle.getItems().setAll(DatenbankCommunicator.dbAbfrageKuechenTage(cbJahr.getSelectionModel().getSelectedItem()));
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
        configureIntegerColumn(tcKuechenStatus, "kuechenStatus");
        configureJahrCombobox(cbJahr);
        configureMonatCombobox(cbMonat);

        tbTabelle.getItems().setAll(DatenbankCommunicator.dbAbfrageKuechenTage(cbJahr.getSelectionModel().getSelectedItem()));
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