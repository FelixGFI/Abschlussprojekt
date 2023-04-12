module de.gfi.felix.abschlussprojekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens de.gfi.felix.abschlussprojekt to javafx.fxml;
    exports de.gfi.felix.abschlussprojekt.gui;
    exports de.gfi.felix.abschlussprojekt;
    opens de.gfi.felix.abschlussprojekt.gui to javafx.fxml;
    opens de.gfi.felix.abschlussprojekt.speicherklassen to javafx.fxml;
    opens de.gfi.felix.abschlussprojekt.helferklassen to javafx.fxml;
    exports de.gfi.felix.abschlussprojekt.helferklassen;
    exports de.gfi.felix.abschlussprojekt.speicherklassen;
} 