package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.speicherklassen.KalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class KalenderController extends Controller {
    @FXML
    private Button btAnnehmen;
    @FXML
    private ComboBox cbGruppenauswahl;
    @FXML
    private ComboBox cbStatusAuswahl;
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
    private TableColumn<KalenderTag, LocalDate> tcDatum;
    @FXML
    private TableColumn<KalenderTag, Boolean> tcEssenVerfuegbar;
    @FXML
    private TableColumn<KalenderTag, Character> tcGruppenStatus;
    @FXML
    private TableColumn<KalenderTag, Integer> tcGruppenBezeichnung;

    public void initialize() {

    }
}