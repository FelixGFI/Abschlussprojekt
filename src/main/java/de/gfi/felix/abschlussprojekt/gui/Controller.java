package de.gfi.felix.abschlussprojekt.gui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {

    public void configureLocalDateTableColum(TableColumn tableColumnLocalDate, String attributName) {
        tableColumnLocalDate.setCellValueFactory(new PropertyValueFactory<>(attributName));
    }

    public void configureBooleanTableColumn(TableColumn tableColumnBoolean, String attributName) {
        tableColumnBoolean.setCellValueFactory(new PropertyValueFactory<>(attributName));
    }

    public void configureGruppenStatusTableColumn(TableColumn tableColumnStatus, String attributName) {
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>(attributName));
    }

    public void configureGruppenBezeichnungtableColumn(TableColumn tableColumnGruppenBezeichnung, String attributName) {
        tableColumnGruppenBezeichnung.setCellValueFactory(new PropertyValueFactory<>(attributName));
    }
    public void configureIntegerColumn(TableColumn tableColumnInteger, String attributName) {
        tableColumnInteger.setCellValueFactory(new PropertyValueFactory<>(attributName));
    }
}