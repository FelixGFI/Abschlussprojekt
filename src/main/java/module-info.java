module de.gfi.felix.abschlussprojekt {
    requires javafx.controls;
    requires javafx.fxml;

    opens de.gfi.felix.abschlussprojekt to javafx.fxml;
    exports de.gfi.felix.abschlussprojekt.gui;
    exports de.gfi.felix.abschlussprojekt;
    opens de.gfi.felix.abschlussprojekt.gui to javafx.fxml;
} 