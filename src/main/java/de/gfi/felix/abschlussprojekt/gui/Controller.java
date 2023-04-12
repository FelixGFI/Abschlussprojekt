package de.gfi.felix.abschlussprojekt.gui;

import de.gfi.felix.abschlussprojekt.helferklassen.UsefullConstants;
import de.gfi.felix.abschlussprojekt.speicherklassen.Tag;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
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
    private static String getStatusStringForCharacter(Character statusCharacter) {
        String statusString;
        int statusIndex = UsefullConstants.getStatusListCharacterFormat().indexOf(statusCharacter);
        statusString = UsefullConstants.getStatusListStringFormat().get(statusIndex);
        return statusString;
    }
}