package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.helferklassen.UsefullConstants;
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
        ArrayList<KalenderTag> testListe = new ArrayList<>();
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(0), 0, true, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(1), 1, true, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(2), 2, true, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(3), 3, true, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(4), 4, true, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(5), 5, true, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(6), 6, true, true));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(0), 7, false, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(1), 8, false, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(6), 9, true, false));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(1), 10, true, true));
        tbTabelle.getItems().setAll(testListe);
        tbTabelle.refresh();
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
    @FXML
    protected void onCbGruppenauswahlAction() throws SQLException {
        if(cbGruppenauswahl.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        System.out.println("KalenderController.onCbGruppenauswahLAction()");
        ArrayList<KalenderTag> kalenderTagsListe = DatenbankCommunicator.dbAbfrageKalenderDaten(cbGruppenauswahl.getSelectionModel().getSelectedItem(), cbJahr.getSelectionModel().getSelectedItem());
        for (KalenderTag tag : kalenderTagsListe) {
            System.out.println(tag.getDatum() + ", " + tag.getStatus() + ", " + tag.getGruppenID());
        }

        //TODO abfrage ob Nutzer sicher ist wenn Änderungen vorgenommen wurden

        tbTabelle.getItems().setAll(kalenderTagsListe);
        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
    }
    @FXML
    protected void onCbMonatAction() {
        System.out.println("KlanderController.onCbMonatAction()");
    }
    @FXML
    protected void onCbJahrAction() {
        System.out.println("KlanderController.onCbJahrAction()");
    }
    public void initialize() throws SQLException {
        DatenbankCommunicator.establishConnection();

        Label lblPlacholder = new Label("Momentan sind keine Daten ausgewählt.\nBitte wählen sie eine Gruppe oder Gruppenfamilie aus");
        tbTabelle.setPlaceholder(lblPlacholder);
        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configureLocalDateTableColum(tcDatum, "datum");
        configureGruppenBezeichnungtableColumn(tcGruppenBezeichnung, "gruppenID");
        configureGruppenStatusTableColumn(tcGruppenStatus, "status");
        configureBooleanTableColumn(tcEssenVerfuegbar, "essenVerfuegbar");

        configureMonatCombobox(cbMonat);
        configureJahrCombobox(cbJahr);
        configureStatusCombobox(cbStatusAuswahl);
        configureGruppenAuswahlCombobox(cbGruppenauswahl);
    }
}