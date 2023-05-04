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
        newScene = new Scene(loader.load());
        /*try {

        } catch (IOException ex) {

            //TODO gennerate ERROR popup
            System.out.println("Error beim öffnen von Dialog. KalenderController.openWindow()");
            return;
        }*/

        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.setScene(newScene);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    @FXML
    protected void onBtSpeichernClick() throws SQLException {
        System.out.println("KalenderControllerOn.BtSpeichernClick()");
        DatenbankCommunicator.dbSpeichernKalenderDaten(tbTabelle.getItems());
        datenWurdenBearbeitet = false;
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("KalenderControllerOn.BtAbbrechenClick()");
        if(datenWurdenBearbeitet) {
            System.out.println("Get Nutzerbestätigung");
            if(!getNutzerbestätigung()) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }

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
    @FXML
    protected void onBtNextClick() {
        System.out.println("KalenderController.OnBtNextClick()");
        handleBtNextClick(cbMonat, cbJahr, tbTabelle);
    }

    @FXML
    protected void onBtPreviousClick() {
        System.out.println("KalenderController.OnBtPreviousClick()");

        handleBtPriviousClick(cbMonat, cbJahr, tbTabelle);
    }
    @FXML
    protected void onBtPDFErstellenClick() {
        System.out.println("KalenderController.OnBtPDFErstellenClick()");
        PDFCreator.writeKalenderPDF((ObservableList<KalenderTag>) tbTabelle.getItems(), (Stage) this.btSpeichern.getScene().getWindow(), gruppenListe);
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
            if(!getNutzerbestätigung()) {
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
    @FXML
    protected void onCbMonatAction() {
        System.out.println("KlanderController.onCbMonatAction()");
        handleCbMonatAction(cbMonat, tbTabelle);
    }
    //TODO Add Documentation
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
            if(!getNutzerbestätigung()) {
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
                    if(!getNutzerbestätigung()) e.consume();
                }
            });
        }));
    }
}