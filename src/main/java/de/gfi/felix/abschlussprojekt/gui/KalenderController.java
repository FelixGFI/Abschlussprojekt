package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.helferklassen.PDFCreator;
import de.gfi.felix.abschlussprojekt.helferklassen.UsefullConstants;
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
import java.util.ArrayList;

public class KalenderController extends Controller {
    @FXML
    private Button btAnnehmen;
    @FXML
    private ComboBox<GruppeOderFamilie> cbGruppenauswahl;
    @FXML
    private ComboBox<Character> cbStatusAuswahl;
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
    @FXML
    private Button btBetriebsurlaubUebernehmen;

    ArrayList<Gruppe> gruppenListe;

    /**
     * diese Methode ist zur Öffnung eines Fensters dieser controllerKlasse aus einem anderen Fenster (Hauptmenü) da.
     * @param parentStage stage des Aufrufenden Fensters
     * @param title titel des zu Zeigenden Fensters als String
     * @param fxmlResource dateipfad der für den Controller verwendeten fxml Datei als String
     * @throws IOException
     */
    public static void openWindow(Stage parentStage, String title, String fxmlResource) throws IOException {
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
        System.out.println("KalenderControllerOn.BtSpeichernClick()");
        DatenbankCommunicator.dbSpeichernKalenderDaten(tbTabelle.getItems());
        datenWurdenBearbeitet = false;
    }

    /**
     * Schließt das Fenster. Wenn Änderungen des Nutzers durch verlassen des dialoges
     * Verworfen werden könnten holt die Methode vor dem Verlassen Nutzerbestätigung ein.
     */
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("KalenderControllerOn.BtAbbrechenClick()");
        if(datenWurdenBearbeitet) {
            System.out.println("Get Nutzerbestätigung");
            if(!getNutzerbestaetigung("Dialog beenden und Änderungen verwerfen?")) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }

    /**
     * Wir beim clicken des btAnnehmen ausgelöst und ändert die ausgewählten Einträge in der
     * Tableview entsprechend des in der cbStatusauswahl ausgewählten Status. Ausgenommen ist
     * der Status "gesetzlicher Feiertag" welcher vom Programm anhand daten der Datenbank
     * automatisch vergeben wird und vom Nutzer nicht geändert werden kann.
     */
    @FXML
    protected void onBtAnnehmenClick() {
        System.out.println("KalenderController.OnBtAnnehmenClick()");
        Character ausgewaehlt = cbStatusAuswahl.getSelectionModel().getSelectedItem();
        if(ausgewaehlt == null) {
            return;
        }
        for (KalenderTag tag : (ObservableList<KalenderTag>) tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getStatus() != UsefullConstants.getFeiertagStatus()) {
                tag.setStatus(ausgewaehlt);
                datenWurdenBearbeitet = true;
            }
        }
        tbTabelle.refresh();
    }

    /**
     * wechselt zum nächsten Monat
     */
    @FXML
    protected void onBtNextClick() {
        System.out.println("KalenderController.OnBtNextClick()");
        handleBtNextClick(cbMonat, cbJahr, tbTabelle);
    }

    /**
     * wechselt zum Vormonat
     */
    @FXML
    protected void onBtPreviousClick() {
        System.out.println("KalenderController.OnBtPreviousClick()");

        handleBtPriviousClick(cbMonat, cbJahr, tbTabelle);
    }

    /**
     * beginnt den Prozess der PDF erstellung aus den Daten in der Tabelel entsprechend der Aufgerufenen Methode
     */
    @FXML
    protected void onBtPDFErstellenClick() {
        System.out.println("KalenderController.OnBtPDFErstellenClick()");
        PDFCreator.writeKalenderPDF((ObservableList<KalenderTag>) tbTabelle.getItems(), (Stage) this.btSpeichern.getScene().getWindow(), gruppenListe);
    }

    /**
     * Übernimmt den Betriebsurlaub aus der Datenbank für die in der Tabelle Augewählten Datensätze.
     * fragt eine ArrayListe von LocalDates ab an welchen alle Daten für das gegeben Jahr enthält für die
     * Betriebsurlaub angesetzt ist und übernimmt für alle Tage in der Tabelle mit dem entsprechenden Datum
     * (außer gesetzliche Feiertage) dan Status character 'U' für "Urlaub"
     * @throws SQLException
     */
    @FXML
    protected void onBtBetriebsurlaubUebernehmenClick() throws SQLException {
        System.out.println("KalenderController.OnBtBetriebsurlaubUebernehmenClick()");
        if(tbTabelle.getItems().isEmpty()) {
            return;
        }
        ObservableList<KalenderTag> tagesListe = (ObservableList<KalenderTag>) tbTabelle.getItems();
        ArrayList<LocalDate> betriebsurlaubsDatumListe = DatenbankCommunicator.dbAbfrageBetriebsurlaubUebernehmen(tagesListe.get(0).getDatum().getYear());
        for (KalenderTag tag : tagesListe) {
            if(betriebsurlaubsDatumListe.contains(tag.getDatum())) {
                if(!tag.getFeiertag()) {
                    tag.setStatus(UsefullConstants.getUrlaubStatusCharacter());
                }
            }
        }
        tbTabelle.refresh();
    }
    /**
     * Holt wenn eine Gruppe oder Gruppenfamilie ausgewählt wird die Entsprechenden Daten entweder für die gewählte
     * Gruppe oder für alle Gruppen der Familie alle relevanten Daten aus der Datenbank und zeigt diese in der Tabelle an.
     * Änderungen werden stand jetzt nicht gespeichert, die in der Tabelle breits vorhandenen Daten werden einfach überschrieben
     * @throws SQLException
     */
    @FXML
    protected void onCbGruppenauswahlAction() throws SQLException {
        if(cbGruppenauswahl.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        System.out.println("KalenderController.onCbGruppenauswahLAction()");

        if(datenWurdenBearbeitet) {
            if(!getNutzerbestaetigung("Andere Gruppe auswählen und Änderungen verwerfen?")) {
                return;
            }
        }

        ArrayList<KalenderTag> kalenderTagsListe = DatenbankCommunicator.dbAbfrageKalenderDaten(cbGruppenauswahl.getSelectionModel().getSelectedItem(), cbJahr.getSelectionModel().getSelectedItem());

        tbTabelle.getItems().setAll(kalenderTagsListe);
        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
        tbTabelle.refresh();

        scrollToFirstOfMonthAndYear(cbJahr.getSelectionModel().getSelectedItem(), cbMonat.getSelectionModel().getSelectedItem(), tbTabelle);

        datenWurdenBearbeitet = false;
    }

    /**
     * führt das Eventhandlung bei Änderung des Monats in der Combobox<Month> cbMonat entsprechend der
     * aufgerufenen Methode aus.
     */
    @FXML
    protected void onCbMonatAction() {
        System.out.println("KlanderController.onCbMonatAction()");
        handleCbMonatAction(cbMonat, tbTabelle);
    }

    /**
     * wird ausgelöst wenn ein anderes Jahr in der cbJahr ausgewählt wird. Überprüft ob
     * daten editiert worden sein könnten und fragt, wenn ja Nuterbestätigung ab. Wird diese
     * nicht gewehrt so wird in der cbJahr das momentan in der Tabelle angezeigte Jahr
     * ausgewählt und die Methode dan beendet. Damit durch das auswählen des in der Tabelle
     * angeziegten jahres onCBJahrAction nicht erneut ausgeführt wird wird der
     * Boolean ingnorEventCbJahr verwendet. Wenn diese Methode nicht vorher beendet wird
     * fragte sie die Daten für das vom Nutzer neu ausgewählte jahr und die ausgewählte Gruppe/
     * Gruppenfamilie ab und befüllt die Datenbank damit.
     * @throws SQLException
     */
    @FXML
    protected void onCbJahrAction() throws SQLException {
        if(ingnorEventCbJahr) {
            ingnorEventCbJahr = false;
            return;
        }

        System.out.println("KlanderController.onCbJahrAction()");
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
        ArrayList<KalenderTag> kalenderTagsListe = DatenbankCommunicator.dbAbfrageKalenderDaten(cbGruppenauswahl.getSelectionModel().getSelectedItem(), cbJahr.getSelectionModel().getSelectedItem());

        tbTabelle.getItems().setAll(kalenderTagsListe);
        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
        tbTabelle.refresh();

        scrollToFirstOfMonthAndYear(cbJahr.getSelectionModel().getSelectedItem(), cbMonat.getSelectionModel().getSelectedItem(), tbTabelle);

        datenWurdenBearbeitet = false;
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
     * Fragt außerdem alle Vorhanden Gruppen und Gruppenfamilien aus der Datenbank ab und Speichert alle Gruppen
     * in einer Globalen Variable
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        DatenbankCommunicator.establishConnection();

        Label lblPlacholder = new Label("Momentan sind keine Daten ausgewählt.\nBitte wählen sie eine Gruppe oder Gruppenfamilie aus");

        ArrayList<GruppenFamilie> familienListe = DatenbankCommunicator.dbAbfrageGruppenUndFamilien();
        gruppenListe = new ArrayList<>();
        for (GruppenFamilie f : familienListe) {
            gruppenListe.addAll(f.getGruppenListe());
        }

        tbTabelle.setPlaceholder(lblPlacholder);
        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configureLocalDateTableColum(tcDatum, "datum");
        configureGruppenBezeichnungtableColumn(tcGruppenBezeichnung, "gruppenID", gruppenListe);
        configureGruppenStatusTableColumn(tcGruppenStatus, "status");
        configureBooleanTableColumn(tcEssenVerfuegbar, "essenVerfuegbar");

        configureMonatCombobox(cbMonat);
        configureJahrCombobox(cbJahr);
        configureStatusCombobox(cbStatusAuswahl);
        configureGruppenAuswahlCombobox(cbGruppenauswahl);

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