package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class KuechenController extends Controller {
    @FXML
    private Button btSpeichern;
    @FXML
    private Button btAbbrechen;
    @FXML
    private ComboBox cbMonat;
    @FXML
    private ComboBox cbJahr;
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

    public static void openWindow(Stage parentStage, String title, String fxmlResource) {
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
    }
    public void initialize() {

    }

}