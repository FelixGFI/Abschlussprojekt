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
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(0), 0));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(1), 1));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(2), 2));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(3), 3));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(4), 4));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(5), 5));
        testListe.add(new KalenderTag(LocalDate.now(), UsefullConstants.getStatusListCharacterFormat().get(6), 6));

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
        DatenbankCommunicator.dbAbfrageKalenderDaten(cbGruppenauswahl.getSelectionModel().getSelectedItem(), cbJahr.getSelectionModel().getSelectedItem());
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
        configureGruppenBezeichnungtableColumn(tcGruppenBezeichnung, "gruppenBezeichnung");
        configureGruppenStatusTableColumn(tcGruppenStatus, "status");
        configureBooleanTableColumn(tcEssenVerfuegbar, "essenVerfuegbar");

        configureMonatCombobox(cbMonat);
        configureJahrCombobox(cbJahr);
        configureStatusCombobox(cbStatusAuswahl);
        configureGruppenAuswahlCombobox(cbGruppenauswahl);
    }
}