package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.helferklassen.UsefullConstants;
import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
                    String cellText;
                    if (newValue) {
                        cellText = "Ja";
                    } else {
                        cellText = "Nein";
                    }
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
                    cellText = getStatusStringForCharacter(newValue);
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
     * @param statusCharacter Character zu dem der Status strign ermittelt werden soll
     * @return Status String wenn gefunden, wenn nicht gefunden null.
     */
    private static String getStatusStringForCharacter(Character statusCharacter) {
        String statusString;
        int statusIndex = UsefullConstants.getStatusListCharacterFormat().indexOf(statusCharacter);
        statusString = UsefullConstants.getStatusListStringFormat().get(statusIndex);
        return statusString;
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

    //TODO Add Documentation
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
    //TODO Add Documentation
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

    //TODO Add Documentation
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
    //TODO Add Documentation
    protected void scrollToFirstOfMonthAndYear(Integer jahr, Month monat, TableView tbTabelle) {
        LocalDate datum = DatenbankCommunicator.getFirstWerktagOfMonth(jahr, monat);

        scrollToDatum(datum, tbTabelle);
    }
    //TODO Add Documentation
    protected static void scrollToDatum(LocalDate datum, TableView tbTabelle) {
        for(Tag tag : (ObservableList<Tag>) tbTabelle.getItems()) {
            if(tag.getDatum().isEqual(datum)) {
                tbTabelle.scrollTo(tag);
                break;
            }
        }
    }
    protected void handleCbMonatAction(ComboBox<Month> cbMonat, TableView tbTabelle) {
        System.out.println("KuechenController.onCbMonatAction()");
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
}