package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.helferklassen.UsefullConstants;
import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {

    Boolean datenWurdenBearbeitet = false;
    Boolean ingnorEventCbJahr = false;
    Boolean ingnorEventCbMonat = false;

    /**
     * Konfiguriert das Datum in einer übergebenen TableColumn in dem Vormat, Wochentagskürzel, DD.MM.JJJJ
     * z. B. Fr, 28.04.2023
     * Dies geschieht mithilfe einer CellValueFactory (zum festsetzen des Anzuzeigenden Attributes) und einer
     * CellFactory (zum festlegen der Formatierung jeder Zelle)
     * @param tableColumnLocalDate zu Formatierende TableColumn
     * @param attributName attributname des Datums in der für die zugehörige TableView enthaltene Klasse
     */
    public void configureLocalDateTableColum(TableColumn tableColumnLocalDate, String attributName) {
        tableColumnLocalDate.setCellValueFactory(new PropertyValueFactory<>(attributName));
        tableColumnLocalDate.setCellFactory(column -> {
            TableCell<Tag, LocalDate> cell = new TableCell<>();
            cell.itemProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText;
                    switch(newValue.getDayOfWeek()) {
                        case MONDAY -> cellText = "Mo, ";
                        case TUESDAY -> cellText = "Di, ";
                        case WEDNESDAY -> cellText = "Mi, ";
                        case THURSDAY -> cellText = "Do, ";
                        case FRIDAY -> cellText = "Fr, ";
                        default -> cellText = "";
                    }

                    cellText += newValue.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    cell.setText(cellText);
                }
            }));
            return cell;
        });

    }
    /**
     * Formatiert eine tableColumn welche Boolsche werte enthalten soll und sorgt dafür das für den booleschen
     * Wert true "Ja" und für den wert false "Nein" angezeigt wird.
     * Dies geschieht mithilfe einer CellValueFactory (zum festsetzen des Anzuzeigenden Attributes) und einer
     * CellFactory (zum festlegen der Formatierung jeder Zelle)
     * @param tableColumnBoolean zu Formatierende TableColumn
     * @param attributName attributname des Datums in der für die zugehörige TableView enthaltene Klasse
     */
    public void configureBooleanTableColumn(TableColumn tableColumnBoolean, String attributName) {
        tableColumnBoolean.setCellValueFactory(new PropertyValueFactory<>(attributName));
        tableColumnBoolean.setCellFactory(column -> {
            TableCell<Tag, Boolean> cell = new TableCell<>();
            cell.itemProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText = (newValue ? "Ja" : "Nein");
                    cell.setText(cellText);
                }
            }));
            return cell;
        });
    }
    /**
     * Formatiert eine TableColumn welche den Status einer Gruppe als Character enthält (z. b. B für Berufschule)
     * so das anstatt des Characters der Entsprechende String angezeigt wird. Der anzuzeigende String für jeden Chracter,
     * sowie die Verfügbaren Characters werden aus Arrays in der Klasse UsefullConstants gelesen.
     * Dies geschieht mithilfe einer CellValueFactory (zum festsetzen des Anzuzeigenden Attributes) und einer
     * CellFactory (zum festlegen der Formatierung jeder Zelle)
     * @param tableColumnStatus zu Formatierende TableColumn
     * @param attributName attributname des Datums in der für die zugehörige TableView enthaltene Klasse
     */
    public void configureGruppenStatusTableColumn(TableColumn tableColumnStatus, String attributName) {
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>(attributName));
        tableColumnStatus.setCellFactory(column -> {
            TableCell<Tag, Character> cell = new TableCell<>();
            cell.itemProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText;
                    cellText = UsefullConstants.getStatusStringForCharacter(newValue);
                    cell.setText(cellText);
                }
            }));
            return cell;
        });
    }


    /**
     * Diese Methode ist noch nicht Vollständig implementiert. In Zukunft soll sie für eine Bestimmte GruppenID
     * in der Angezeigten Zelle die Richtige Formatierung festlegen.
     * Dies geschieht mithilfe einer CellValueFactory (zum festsetzen des Anzuzeigenden Attributes) und einer
     * CellFactory (zum festlegen der Formatierung jeder Zelle)
     * @param tableColumnGruppenBezeichnung zu Formatierende TableColumn
     * @param attributName attributname des Datums in der für die zugehörige TableView enthaltene Klasse
     */
    public void configureGruppenBezeichnungtableColumn(TableColumn tableColumnGruppenBezeichnung, String attributName, ArrayList<Gruppe> gruppenListe) {
        tableColumnGruppenBezeichnung.setCellValueFactory(new PropertyValueFactory<>(attributName));
        tableColumnGruppenBezeichnung.setCellFactory(column -> {
            TableCell<Tag, Integer> cell = new TableCell<>();
            cell.itemProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText = "";
                    for (Gruppe g : gruppenListe) {
                        if(g.getGruppenID() == newValue) {
                            cellText = g.getBezeichnung();
                        }
                    }
                    cell.setText(cellText);
                }
            }));
            return cell;
        });
    }

    /**
     * Diese Methode formatiert eien TableColumn welche die Integers mit den Werten 0, 1  und 2 beinhalten kann
     * indem sie je nach enthaltenem Wert einen Bestimmten String ausgibt. (0 -> "Nein", 1 -> "Ja", 2 ->
     * "gesetzlicher Feiertag")
     * Dies geschieht mithilfe einer CellValueFactory (zum festsetzen des Anzuzeigenden Attributes) und einer
     * CellFactory (zum festlegen der Formatierung jeder Zelle)
     * @param tableColumnInteger zu Formatierende TableColumn
     * @param attributName attributname des Datums in der für die zugehörige TableView enthaltene Klasse
     */
    public void configureIntegerColumn(TableColumn tableColumnInteger, String attributName) {
        tableColumnInteger.setCellValueFactory(new PropertyValueFactory<>(attributName));
        tableColumnInteger.setCellFactory(column -> {
            TableCell<Tag, Integer> cell = new TableCell<>();
            cell.itemProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText;
                    switch (newValue) {
                        case 0 -> cellText = "Nein";
                        case 1 -> cellText = "Ja";
                        case 2 -> cellText = "gesetzlicher Feiertag";
                        default -> cellText = "";
                    }
                    cell.setText(cellText);
                }
            }));
            return cell;
        });
    }

    /**
     * befüllt eien Combobox welche Jahreszahlen zur Auswahl enthalten soll mit werten aus siner ArrayList enthalten
     * in der Klasse UsefullConstants
     * @param cbJahr zu Befüllende Combobox
     */
    protected void configureJahrCombobox(ComboBox<Integer> cbJahr) {
        cbJahr.getItems().addAll(UsefullConstants.getJahreListe());
        cbJahr.getSelectionModel().select(LocalDate.now().getYear() - UsefullConstants.getJahreListe().get(0));
    }

    /**
     * Befüllt eine Combobox mit objekten 12 Month objekten welche für die Zwölf Monat des Jahres stehen
     * und sorgt danach dafür das für jedes Month Objekt, sowohl in der Dropdown liste als auch wenn ausgewählt
     * der entsprechende Deutsche Monatsname als String angezeigt wird
     * Hierzu wird eine CellFactory verwendet (für die Anzeige in der Liste) und ein StringConverter
     * (für die Anzeige des Ausgewählten wertes)
     * @param cbMonat zu Befüllende und Formatierende Combobox
     */
    protected void configureMonatCombobox(ComboBox<Month> cbMonat) {
        cbMonat.getItems().addAll(Month.values());
        cbMonat.getSelectionModel().select(LocalDate.now().getMonth());
        cbMonat.setCellFactory(colum -> {
            ListCell<Month> cell = new ListCell<>();
            cell.itemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText;
                    cellText = getMonthStringFromMonth(newValue);
                    cell.setText(cellText);
                }
            });
            return cell;
        });
        cbMonat.setConverter(new StringConverter<Month>() {
            @Override
            public String toString(Month object) {
                return getMonthStringFromMonth(object);
            }

            @Override
            public Month fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Formatiert eine Combobox welche den Status einer Gruppe als Character enthält (z. b. B für Berufschule)
     * so das anstatt des Characters der Entsprechende String angezeigt wird. Der anzuzeigende String für jeden Chracter,
     * sowie die Verfügbaren Characters werden aus Arrays in der Klasse UsefullConstants gelesen. Die Formatierung
     * wird sowohl für die Dropdown Liste als auch für die anzeige des Ausgewählten Wertes angepasst
     * Hierzu wird eine CellFactory verwendet (für die Anzeige in der Liste) und ein StringConverter
     * (für die Anzeige des Ausgewählten wertes)
     * @param cbStatusAuswahl zu Befüllende und Formatierende Combobox
     */
    protected void configureStatusCombobox(ComboBox<Character> cbStatusAuswahl) {
        cbStatusAuswahl.getItems().addAll(UsefullConstants.getStatusListCharacterFormatOhneFeiertag());
        cbStatusAuswahl.setCellFactory(colum -> {
            ListCell<Character> cell = new ListCell<>();
            cell.itemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText;
                    cellText = getStatusStringForCharacterOhneFeiertag(newValue);
                    cell.setText(cellText);
                }
            });
            return cell;
        });
        cbStatusAuswahl.setConverter(new StringConverter<Character>() {
            @Override
            public String toString(Character object) {
                return getStatusStringForCharacterOhneFeiertag(object);
            }

            @Override
            public Character fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Diese Methode formatiert eine Combobox welche obejekte enthält welche vo nder Klasse GruppeOderFamilie
     * abgeleitet sind. Die Combobox kann entweder Objekte der Klasse Gruppe oder der Klasse GruppenFamilie enthalten
     * welche beide von GruppeOderFamilie abgeleitet sind. Hierzu wird eine ArrayListe aller in der Datenbank enthaltenen
     * Gruppenfamilien verwendet welche vom DatenbankCommunicator bereitgestellt wird. Jede GruppenFamilie enthält
     * eine ArrayList aller zu ihr gehörenden Gruppen (ein Gruppe kann nur zu einer Familie gehören). Die GruppenFamilien
     * werden in der Dropdown Liste Fett gedrukt dargestellt, die zu ihnen Gehördenden Gruppen werden unter der Jweiligen
     * Familie ohne Fettdruck aufgelistet. Auf diese weise ist erkennbar was eine Gruppenfamilie ist und welche Gruppen
     * zu ihr gehören. In der Liste sollen Gruppenfamilien als ganzes oder nur einzelne Gruppen ausgwählt werden können.
     * Die Formatierung wird sowohl für die Dropdown Liste als auch für die anzeige des Ausgewählten
     * Wertes angepasst
     * Hierzu wird eine CellFactory verwendet (für die Anzeige in der Liste) und ein StringConverter
     * (für die Anzeige des Ausgewählten wertes)
     * @param cbGruppenauswahl zu Befüllende und Formatierende Combobox
     */
    protected void configureGruppenAuswahlCombobox(ComboBox<GruppeOderFamilie> cbGruppenauswahl) throws SQLException {
        ArrayList<GruppenFamilie> familenListe = DatenbankCommunicator.dbAbfrageGruppenUndFamilien();
        for (GruppenFamilie familie : familenListe) {
            cbGruppenauswahl.getItems().add(familie);
            for (Gruppe gruppe: familie.getGruppenListe()) {
                cbGruppenauswahl.getItems().add(gruppe);
            }
        }
        cbGruppenauswahl.setCellFactory(colum -> {
            ListCell<GruppeOderFamilie> cell = new ListCell<>();
            cell.itemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null) {

                    if(newValue.getClass() == GruppenFamilie.class) {
                        cell.setText(((GruppenFamilie) newValue).getBezeichnung());
                        cell.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
                    } else {
                        cell.setText(((Gruppe) newValue).getBezeichnung());
                        cell.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, Font.getDefault().getSize()));
                    }

                }
            });
            return cell;
        });
        cbGruppenauswahl.setConverter(new StringConverter<GruppeOderFamilie>
                () {
            @Override
            public String toString(GruppeOderFamilie object) {
                if(object.getClass() == GruppenFamilie.class) {
                    return ((GruppenFamilie) object).getBezeichnung();
                } else {
                    return ((Gruppe) object).getBezeichnung();
                }
            }

            @Override
            public GruppeOderFamilie fromString(String string) {
                return null;
            }
        });

    }
    /**
     * gibt einen für einen übergebenen Status Character den entsprecehnden Status als String zurück.
     * berücksichtigt dabei alerdings keine Feirtage (status Character F)
     * @param statusCharacter Character zu dem der Status strign ermittelt werden soll
     * @return Status String wenn gefunden, wenn nicht gefunden null.
     */
    private static String getStatusStringForCharacterOhneFeiertag(Character statusCharacter) {
        String statusString;
        int statusIndex = UsefullConstants.getStatusListCharacterFormatOhneFeiertag().indexOf(statusCharacter);
        statusString = UsefullConstants.getStatusListStringFormatOhneFeiertag().get(statusIndex);
        return statusString;
    }

    /**
     * ermittelt für ein gegebenes Month objekt die entsprechende Deutschsprachige bezeichnugn als string
     * @param newValue Month für die bezeichung als String ermittelt werden soll.
     * @return Deutschsprachige Bezeichnung als String wenn gefunden. Ansonsten einen Leerstring
     */
    private static String getMonthStringFromMonth(Month newValue) {
        String cellText;
        switch (newValue) {
            case JANUARY -> cellText = "Januar";
            case FEBRUARY -> cellText = "Februar";
            case MARCH -> cellText = "März";
            case APRIL -> cellText = "April";
            case MAY -> cellText = "Mai";
            case JUNE -> cellText = "Juni";
            case JULY -> cellText = "Juli";
            case AUGUST -> cellText = "August";
            case SEPTEMBER -> cellText = "September";
            case OCTOBER -> cellText = "Oktober";
            case NOVEMBER -> cellText = "November";
            case DECEMBER -> cellText = "Dezember";
            default -> cellText = "";
        }
        return cellText;
    }

    /**
     * Erzeugt einen Alert des AlertType.CONFIRMATION welcher den User Fragt ob er Fortfahren möchte und das Ungespeicherte
     * Daten verloren gehen. Gibt die Antwort des Users zurück. True wenn der User mit "OK" bestätigt. False wenn der
     * User mit "Abbrechen" die Zustimmung verweigert
     * @return boolean true wenn Nutzerbestätigung erteilt wurde. fasle wenn nicht
     */
    protected boolean getNutzerbestätigung() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Bestätigung");
        alert.setHeaderText("Sind sie sich sicher?");
        alert.setContentText("Alle nicht gespeicherten Daten gehen verloren.");
        DialogPane pane = alert.getDialogPane();
        for(ButtonType buttonType : alert.getButtonTypes()) {
            Button button = (Button) pane.lookupButton(buttonType);
            button.setDefaultButton(buttonType == ButtonType.CANCEL);
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    /**
     * handelt die OnAction Methode des Datepickers in den Einzelnen Controllern in dem das Datum angegeben werden kann
     * von dem Aus Tage in der Tabelle markiert werden sollen (dpVon). Überprüft ob in dpVon und im dpBis gültige Werte vorhanden sind
     * und führt je nach gültigkeit der Werte unterschiedliceh Aktionen aus. Sind keine gültigen Werte vorhanden oder
     * ist nur der dpBis wert gültig beendet sich die Methode. Ist nur der dpVon Wert gültig oder der dpBis wert vor oder
     * gleich dem dpVon wert wird nur der dpVon Wert markeirt. sind dpVon und dpBis gültig und dp Bis nach dpVon werden
     * alel Tage mit den beiden werten, sowie alle Tage dazwischen markeirt. Das Programm prüft außerdem ob die daten sich
     * auf dds Momentan ausgewählte Jahr beziehen. Wenn nicht beendet sich die Methode.
     * @param dpVon DatePicker welcher das Datum enthalten kann von dem Aus in der Tabelle ausgewäht werden soll
     *              dpVon löst außerdem das ActionEvent aus welches dies Methode bearbeiten soll.
     * @param dpBis DatePicker welcher das Datum enthalten kann bis zu Welchem in der Tabelle ausgewäht werden soll
     * @param tbTabelle TableView in welcher zeilen ausgewählt werden sollen
     */
    protected void handleDpVon(DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        if(dpVon.getValue() == null) {
            return;
        }
        if(((ObservableList<Tag>) tbTabelle.getItems()).isEmpty()) {
            return;
        }

        Integer jahr = ((ObservableList<Tag>) tbTabelle.getItems()).get(0).getDatum().getYear();

        if(dpVon.getValue().getYear() != jahr) {
            return;
        }

        tbTabelle.getSelectionModel().clearSelection();

        if(dpBis.getValue() == null || dpBis.getValue().getYear() != jahr) {
            LocalDate vonDat = dpVon.getValue();
            for(Tag tag : (ObservableList<Tag>) tbTabelle.getItems()) {
                LocalDate datum = tag.getDatum();
                if(datum.isEqual(vonDat)) {
                    tbTabelle.getSelectionModel().select(tag);
                }
            }

        } else {
            LocalDate vonDat = dpVon.getValue();
            LocalDate bisDat = dpBis.getValue();
            for(Tag tag : (ObservableList<Tag>) tbTabelle.getItems()) {
                LocalDate datum = tag.getDatum();
                if((datum.isBefore(bisDat) && datum.isAfter(vonDat)) || datum.isEqual(bisDat) || datum.isEqual(vonDat)) {
                    tbTabelle.getSelectionModel().select(tag);
                }
            }
        }
    }

    /**
     * handelt die OnAction Methode des Datepickers in den Einzelnen Controllern in dem das Datum angegeben werden kann
     * bis zu dem Tage in der Tabelle markiert werden sollen (dpBis). Überprüft ob in dpVon und im dpBis gültige Werte vorhanden sind
     * und führt je nach gültigkeit der Werte unterschiedliceh Aktionen aus. Sind keine gültigen Werte vorhanden,
     * ist nur der dpBis wert gültig oder der dpBisWert vor oder gliech beendet sich die Methode. sind dpVon und dpBis gültig und
     * dpBis nach dpVon werden alle Tage mit den beiden werten, sowie alle Tage dazwischen markeirt.
     * Das Programm prüft außerdem ob die daten sich auf dds Momentan ausgewählte Jahr beziehen. Wenn nicht beendet
     * sich die Methode.
     * @param dpVon DatePicker welcher das Datum enthalten kann von dem Aus in der Tabelle ausgewäht werden soll
     * @param dpBis DatePicker welcher das Datum enthalten kann bis zu Welchem in der Tabelle ausgewäht werden soll
     *              dpBis löst außerdem das ActionEvent aus welches dies Methode bearbeiten soll.
     * @param tbTabelle TableView in welcher zeilen ausgewählt werden sollen
     */
    protected void handleDpBis(DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        if(dpVon.getValue() == null || dpBis.getValue() == null) {
            return;
        }
        if(!dpVon.getValue().isBefore(dpBis.getValue())) {
            return;
        }
        if(((ObservableList<Tag>) tbTabelle.getItems()).isEmpty()) {
            return;
        }

        Integer jahr = ((ObservableList<Tag>) tbTabelle.getItems()).get(0).getDatum().getYear();

        tbTabelle.getSelectionModel().clearSelection();

        if(dpVon.getValue().getYear() != jahr || dpBis.getValue().getYear() != jahr) {
            return;
        }

        LocalDate vonDat = dpVon.getValue();
        LocalDate bisDat = dpBis.getValue();
        for(Tag tag : (ObservableList<Tag>) tbTabelle.getItems()) {
            LocalDate datum = tag.getDatum();
            if((datum.isBefore(bisDat) && datum.isAfter(vonDat)) || datum.isEqual(bisDat) || datum.isEqual(vonDat)) {
                tbTabelle.getSelectionModel().select(tag);
            }
        }
    }

    /**
     * fragt für das übergebene Jahr und den Übergebenen Monat den ersten Werktag ab und scrollt zum Ersten gefundenen
     * Tag mit diesem Datum in der übergebenen TableView
     * @param jahr Integer jahr des Werktags zu dem gescrollt werden soll.
     * @param monat Month des Werktags zu dem gescrollt werden soll
     * @param tbTabelle TableView in der gescrollt werden soll.
     */
    protected void scrollToFirstOfMonthAndYear(Integer jahr, Month monat, TableView tbTabelle) {
        LocalDate datum = DatenbankCommunicator.getFirstWerktagOfMonth(jahr, monat);

        scrollToDatum(datum, tbTabelle);
    }

    /**
     * scrollt zu einem Tag mit einem bestimmten übergebenen Datum in der übergebenen TableView.
     * Wichtig!: die Methode prüft NICHT ob ein Tag mit diseem Datum in der Tabellevorhanden ist. Deswegen sollte
     * beim Aufrufen sichergestelltw werden das ein Tag mit dem Entsprechenden Datum auch wirklich in der TableView
     * vorhanden ist. (z. B. es sollte Überpfüfte werrden das das gegeben Datum ein Werktag ist.)
     * alternativ kann die Funktion scrollToFirstOfMonthAndYear verwendet werden welche einen Werktag ermittelt
     * zu dem sie scrollt.
     * @param datum datum zu welchem ein tag in der TabelView gesucht und zu diesem gescrollt werden soll.
     * @param tbTabelle TableView in der gescroltl werden soll
     */
    protected static void scrollToDatum(LocalDate datum, TableView tbTabelle) {
        for(Tag tag : (ObservableList<Tag>) tbTabelle.getItems()) {
            if(tag.getDatum().isEqual(datum)) {
                tbTabelle.scrollTo(tag);
                break;
            }
        }
    }

    /**
     * Soll vom Event handler der ComboBoxen der einzelenen Controller augerufen werden in denen der Nutzer
     * den ausgewählten Monat auswählen kann. Ist ingnorEventCbMonat true, d. h. wird dise Methode durch eine AUswahländerung
     * via programcode in einer Anderen Methode aufgerufen und soll deswegen nciht weiter ausgeführt werden beenet sich
     * die Methode. Ansonsten Überprüft sie ob ein Monat ausgewählt ist (sollte in der Regel eigentlcih immer der Fall sein)
     * und ob in der Tabelle gerade Daten angezeigt werden. Ist letzteres der Fall so wird zum ausgewählten Monat
     * in der tabelle gescrollt
     * @param cbMonat Combobox<Month> für welche diese Methode aufgerufen wird.
     * @param tbTabelle TableView in der gescrollt werden soll
     */
    protected void handleCbMonatAction(ComboBox<Month> cbMonat, TableView tbTabelle) {

        System.out.println("KuechenController.onCbMonatAction()");

        if(ingnorEventCbMonat) {
            ingnorEventCbMonat = false;
            return;
        }
        if(cbMonat.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if(tbTabelle.getItems().isEmpty()) {
            return;
        }

        Integer jahr = ((ObservableList<Tag>) tbTabelle.getItems()).get(0).getDatum().getYear();
        Month monat = cbMonat.getSelectionModel().getSelectedItem();

        scrollToFirstOfMonthAndYear(jahr, monat, tbTabelle);
    }

    /**
     * soll vom EventHandler der btNext in den einzelnen Controllern controller ausgewähtl werden welcher vom Nutzer
     * verwendet werden können um zum nächsten Monat umzuschalten. wenn durch das springen zum nächsten Monat
     * auch das Jahr geweschelt werden würde (z. B. von Dezember 2023 nach Januar 2024) holt die Methode
     * dafür Nutzerbstätigung ein, wenn möglicherweise Änderungen an den Angezeigten Daten vorgenommen wurden.
     * In jedem Fall, es sei den sie wurde vorher beeendet, scroltl die Methode zum neuen Ausgewählten Monat
     * Die Methode akktualisiert außerdem die Comboboxen cbJahr und cbMonat entsprechend des Neuen
     * Ausgewählten jahres und Monats.
     * @param cbMonat Combobox<Month>cbMonat welche durch die Methode aktualiseirt wird.
     * @param cbJahr Combobox<Integer> cbJahr welche durch die Methode aktualiseirt wird.
     * @param tbTabelle tbTabelle auf in welcher zum neuen Ausgewählten Monat gescrollt werden soll
     */
    protected void handleBtNextClick(ComboBox<Month> cbMonat, ComboBox<Integer> cbJahr ,TableView tbTabelle) {
        Month monat = cbMonat.getSelectionModel().getSelectedItem();
        Integer jahr = cbJahr.getSelectionModel().getSelectedItem();
        if(monat == Month.DECEMBER) {
            Integer neuesJahr = jahr + 1;
            if(!cbJahr.getItems().contains(neuesJahr)) {
                return;
            }
            //triggers the Action Listener for cbJahr who does everything else necccsary
            cbJahr.getSelectionModel().select(neuesJahr);
            if(cbJahr.getSelectionModel().getSelectedItem() == jahr) {
                return;
            }
            //triggers the Action Listener for cbMonat who does everything else necccsary
            cbMonat.getSelectionModel().select(Month.JANUARY);
        } else {
            //triggers the Action Listener for cbMonat who does everything else necccsary
            cbMonat.getSelectionModel().selectNext();
        }
    }
    /**
     * soll vom EventHandler der btPrevious in den einzelnen Controllern controller ausgewähtl werden welcher vom Nutzer
     * verwendet werden können um zum vorherigen Monat umzuschalten. wenn durch das springen zum vorherigen Monat
     * auch das Jahr geweschelt werden würde (z. B. von Januar 2023 nach Dezember 2022) holt die Methode
     * dafür Nutzerbstätigung ein, wenn möglicherweise Änderungen an den Angezeigten Daten vorgenommen wurden.
     * In jedem Fall, es sei den sie wurde vorher beeendet, scroltl die Methode zum neuen Ausgewählten Monat.
     * Anmerkung: Beim Wechsel zu Manchen Jahren funktioniert das Scrollen nicht. Bei Manchen schon.
     * Dies scheint mit der funktinsweise der scroll funktionen von JavaFx zutun zu haben und ich konnte keine
     * Lösung dafür finden. In manchen jahren funktioniert es.
     * Die Methode akktualisiert außerdem die Comboboxen cbJahr und cbMonat entsprechend des Neuen
     * Ausgewählten jahres und Monats.
     * @param cbMonat Combobox<Month>cbMonat welche durch die Methode aktualiseirt wird.
     * @param cbJahr Combobox<Integer> cbJahr welche durch die Methode aktualiseirt wird.
     * @param tbTabelle tbTabelle auf in welcher zum neuen Ausgewählten Monat gescrollt werden soll
     */
    protected void handleBtPriviousClick(ComboBox<Month> cbMonat, ComboBox<Integer> cbJahr, TableView tbTabelle) {
        Month monat = cbMonat.getSelectionModel().getSelectedItem();
        Integer jahr = cbJahr.getSelectionModel().getSelectedItem();
        if(monat == Month.JANUARY) {
            Integer neuesJahr = jahr - 1;
            if(!cbJahr.getItems().contains(neuesJahr)) {
                return;
            }
            //triggers the Action Listener for cbJahr who does everything else necccsary
            cbJahr.getSelectionModel().select(neuesJahr);
            if(cbJahr.getSelectionModel().getSelectedItem() == jahr) {
                return;
            }
            //triggers the Action Listener for cbMonat who does everything else necccsary
            cbMonat.getSelectionModel().select(Month.DECEMBER);
        } else {
            //triggers the Action Listener for cbMonat who does everything else necccsary
            cbMonat.getSelectionModel().selectPrevious();
        }
    }

    /**
     * wird von eventFiltern der Einzelnen Controller aufgerufen welche ScrollEvents abfangen und mittels dieser
     * methode bearbeiten sollen. Die Methode überprüft ob in der Tabelle einträge vorhanden sind und wenn ja
     * passt sie die combobox in welcher der Ausgewähtle Monat angezeigt ist entsprechend der Position an zu welcher der User
     * gescrollt ist. (z. b. Der user scrollt in der Tabelle von einträgen aus dem Monat märz, zu einträgen aus dem Monat
     * Apri. Die methode soll den Ausgewählten Monat auf März ändern). Um sicher zustellen das der event Handler
     * von cbMonat nicht dazwischen funkt wird der Boolean ingnorEventCbMonat verwendet, welccher wenn true
     * die ausführung der eventHandler Methode unterbricht.
     * @param event
     * @param cbMonat
     * @param tbTabelle
     */
    protected void handleScrollEvent(ScrollEvent event, ComboBox<Month> cbMonat, TableView tbTabelle) {
        if(tbTabelle.getItems().isEmpty()) {
            return;
        }
        try {
            System.out.println(event.getTarget());
            TableCell<Tag, LocalDate> cell = (TableCell) event.getTarget();
            Tag tag = (Tag) cell.getTableRow().getItem();
            System.out.println(tag.getDatum());
            if(cbMonat.getSelectionModel().getSelectedItem() != tag.getDatum().getMonth()) {
                ingnorEventCbMonat = true;
                cbMonat.getSelectionModel().select(tag.getDatum().getMonth());
            }
        } catch (ClassCastException ce) {

        }
    }

    /**
     * erstellt einen Alert vom Typ AlertType.Error mit dem Übergebenen Titel, der Übergebenen Überschrift und dem
     * Übergebenen Inhalt und zeigt diesen an.
     * @param titel titel des Alert
     * @param ueberschrift Überschrift für den Alert
     * @param inhalt Inhalt des Alerts
     */
    public static void createAndShowErrorAlert(String titel, String ueberschrift, String inhalt) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(titel);
        alert.setHeaderText(ueberschrift);
        alert.setContentText(inhalt);
        alert.showAndWait();
    }
}