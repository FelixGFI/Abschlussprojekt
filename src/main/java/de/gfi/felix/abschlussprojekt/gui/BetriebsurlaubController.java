package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.speicherklassen.KalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class BetriebsurlaubController extends Controller {
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
    private TableColumn<KalenderTag, LocalDate> tcDatum;
    @FXML
    private TableColumn<KalenderTag, Integer> tcIstBetriebsurlaub;
}