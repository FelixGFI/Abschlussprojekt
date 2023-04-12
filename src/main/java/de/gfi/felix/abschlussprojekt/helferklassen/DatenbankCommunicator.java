package de.gfi.felix.abschlussprojekt.helferklassen;

import de.gfi.felix.abschlussprojekt.speicherklassen.GruppeOderFamilie;

import java.sql.*;

public class DatenbankCommunicator {

    private static Connection conn;
    private static final String url = "jdbc:mariadb://localhost/verpflegungsgeld";
    private static final String user = "root";
    private static final String password = "";

    /**
     * Stellt die Verbindung mit der Datenbank her deren daten in globalen Variable definiert sind. Verursacht ein Popup
     * sollte die verbindung nicht möglich sein
     */
    public static void establishConnection() {
        //create connection for a server installed in localhost, with a user "root" with no password
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            //TODO make error alert
            System.out.println( "Datenbankverbindung Fehlgeschlagen. Bitte Stellen Sie sicher das das Programm zugriff auf die Datenbank hat. [" + url + "]");
        }
    }
}
