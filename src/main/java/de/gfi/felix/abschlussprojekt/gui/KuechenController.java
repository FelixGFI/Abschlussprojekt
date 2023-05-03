package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.application.Platform;
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

            //TODO gennerate ERROR popup
            System.out.println("Error beim öffnen von Dialog. KuechenController.openWindow()");
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
        System.out.println("KuechenController.OnBtSpeichernClick()");
        DatenbankCommunicator.dbSpeichernKuechenTage(tbTabelle.getItems());
        datenWurdenBearbeitet = false;
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("KuechenController.OnBtAbbrechenClick()");
        if(datenWurdenBearbeitet) {
            System.out.println("Get Nutzerbestätigung");
            if(!getNutzerbestätigung()) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }
    @FXML
    protected void onBtNextClick() {
        System.out.println("KuechenController.OnBtNextClick()");
    }
    @FXML
    protected void onBtPreviousClick() {
        System.out.println("KuechenController.OnBtPreviousClick()");
    }
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
    @FXML
    protected void onCbMonatAction() {
        System.out.println("KuechenController.onCbMonatAction()");
    }
    @FXML
    protected void onCbJahrAction() {
        System.out.println("KuechenController.onCbJahrAction()");
    }
    public void initialize() throws SQLException {
        DatenbankCommunicator.establishConnection();

        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configureLocalDateTableColum(tcDatum, "datum");
        configureIntegerColumn(tcKuechenStatus, "kuechenStatus");
        configureJahrCombobox(cbJahr);
        configureMonatCombobox(cbMonat);

        tbTabelle.getItems().setAll(DatenbankCommunicator.dbAbfrageKuechenTage(cbJahr.getSelectionModel().getSelectedItem()));

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