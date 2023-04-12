package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.DatenbankCommunicator;
import de.gfi.felix.abschlussprojekt.helferklassen.UsefullConstants;
import de.gfi.felix.abschlussprojekt.speicherklassen.Gruppe;
import de.gfi.felix.abschlussprojekt.speicherklassen.GruppeOderFamilie;
import de.gfi.felix.abschlussprojekt.speicherklassen.GruppenFamilie;
import de.gfi.felix.abschlussprojekt.speicherklassen.Tag;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Controller {

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

                    cellText += newValue.format(DateTimeFormatter.ofPattern("dd.MM.yyy"));
                    cell.setText(cellText);
                }
            }));
            return cell;
        });

    }

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



    public void configureGruppenBezeichnungtableColumn(TableColumn tableColumnGruppenBezeichnung, String attributName) {
        tableColumnGruppenBezeichnung.setCellValueFactory(new PropertyValueFactory<>(attributName));
    }
    public void configureIntegerColumn(TableColumn tableColumnInteger, String attributName) {
        tableColumnInteger.setCellValueFactory(new PropertyValueFactory<>(attributName));
        tableColumnInteger.setCellFactory(column -> {
            TableCell<Tag, Integer> cell = new TableCell<>();
            cell.itemProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    String cellText;
                    switch (newValue) {
                        case 0 -> cellText = "Ja";
                        case 1 -> cellText = "Nein";
                        case 2 -> cellText = "gesetzlicher Feiertag";
                        default -> cellText = "";
                    }
                    cell.setText(cellText);
                }
            }));
            return cell;
        });
    }

    protected void configureJahrCombobox(ComboBox<Integer> cbJahr) {
        cbJahr.getItems().addAll(UsefullConstants.getJahreListe());
        cbJahr.getSelectionModel().select(LocalDate.now().getYear() - UsefullConstants.getJahreListe().get(0));
    }
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

    protected void configureGruppenAuswahlCombobox(ComboBox<GruppeOderFamilie> cbGruppenauswahl) throws SQLException {
        ArrayList<GruppenFamilie> familenListe = DatenbankCommunicator.dbAbfrageGruppenUndFamilien();
        for (GruppenFamilie familie : familenListe) {
            System.out.println(familie.getBezeichnung());
            for (Gruppe gruppe: familie.getGruppenListe()) {
                System.out.println("\t" + gruppe.getBezeichnung());
            }
        }
    }
    private static String getStatusStringForCharacter(Character statusCharacter) {
        String statusString;
        int statusIndex = UsefullConstants.getStatusListCharacterFormat().indexOf(statusCharacter);
        statusString = UsefullConstants.getStatusListStringFormat().get(statusIndex);
        return statusString;
    }
    private static String getStatusStringForCharacterOhneFeiertag(Character statusCharacter) {
        String statusString;
        int statusIndex = UsefullConstants.getStatusListCharacterFormatOhneFeiertag().indexOf(statusCharacter);
        statusString = UsefullConstants.getStatusListStringFormatOhneFeiertag().get(statusIndex);
        return statusString;
    }
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


}