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
    protected void onBtSpeichernClick() {
        System.out.println("KuechenController.OnBtSpeichernClick()");
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("KuechenController.OnBtAbbrechenClick()");
    }
    @FXML
    protected void onBtNextClick() {
        System.out.println("KuechenController.OnBtNextClick()");
    }
    @FXML
    protected void onBtPreviousClick() {
        System.out.println("KuechenController.OnBtPreviousClick()");
    }
    @FXML protected void onBtOffenClick() {
        System.out.println("KuechenController.OnBtOffenClick");
    }
    @FXML protected void onBtGeschlossenClick() {
        System.out.println("KuechenController.OnBtGeschlossenClick");
        ArrayList<KuechenTag> testListe = new ArrayList<>();
        testListe.add(new KuechenTag(LocalDate.now(), 0));
        testListe.add(new KuechenTag(LocalDate.now(), 1));
        testListe.add(new KuechenTag(LocalDate.now(), 2));
        tbTabelle.getItems().setAll(testListe);
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
    public void initialize() {
        DatenbankCommunicator.establishConnection();

        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configureLocalDateTableColum(tcDatum, "datum");
        configureIntegerColumn(tcKuechenStatus, "kuechenStatus");
        configureJahrCombobox(cbJahr);
        configureMonatCombobox(cbMonat);
    }

}