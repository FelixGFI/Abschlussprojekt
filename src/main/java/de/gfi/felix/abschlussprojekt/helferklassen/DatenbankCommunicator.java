package de.gfi.felix.abschlussprojekt.helferklassen;

import de.gfi.felix.abschlussprojekt.speicherklassen.Gruppe;
import de.gfi.felix.abschlussprojekt.speicherklassen.GruppeOderFamilie;
import de.gfi.felix.abschlussprojekt.speicherklassen.GruppenFamilie;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

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
    public static ArrayList<GruppenFamilie> dbAbfrageGruppenUndFamilien() throws SQLException {
        ArrayList<GruppenFamilie> familienListe = new ArrayList<>();
        int akktuelleFamilienID = -1;
        GruppenFamilie akktuelleFamilie = null;
        try (Statement statement = conn.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery("select g.id, g.name, g.gruppenfamilie_id, g2.name as \"familienName\" from gruppe g, gruppenfamilie g2 where g.gruppenfamilie_id = g2.id order by g.gruppenfamilie_id;")) {
                while (resultSet.next()) {
                    akktuelleFamilienID = resultSet.getInt("gruppenfamilie_id");
                    if(akktuelleFamilie == null) {
                        akktuelleFamilie = new GruppenFamilie(resultSet.getInt("gruppenfamilie_id"), resultSet.getString("familienName"), new ArrayList<Gruppe>());
                    } else if(akktuelleFamilienID != akktuelleFamilie.getFamilienID()) {
                        familienListe.add(akktuelleFamilie);
                        akktuelleFamilie = akktuelleFamilie = new GruppenFamilie(resultSet.getInt("gruppenfamilie_id"), resultSet.getString("familienName"), new ArrayList<Gruppe>());
                    }
                    akktuelleFamilie.addToGruppenListe(new Gruppe(resultSet.getInt("id"), resultSet.getString("name")));
                }
                familienListe.add(akktuelleFamilie);
            }
        }
        return familienListe;
    }

    public static void dbAbfrageKalenderDaten(GruppeOderFamilie gruppeOderFamilie, Integer jahr) throws SQLException {
        LocalDate firstWerktagOfYear = getFirstWerktagOfYear(jahr);
        checkForDatensatzAndGenerateIfMissing(gruppeOderFamilie, jahr);
    }

    private static void checkForDatensatzAndGenerateIfMissing(GruppeOderFamilie gruppeOderFamilie, Integer jahr) throws SQLException {
        LocalDate firstWerktagOfYear = getFirstWerktagOfYear(jahr);
        ArrayList<Gruppe> ausgewaehlteGruppenListe = new ArrayList<>();
        if(gruppeOderFamilie.getClass() == GruppenFamilie.class) {
            ausgewaehlteGruppenListe.addAll(((GruppenFamilie) gruppeOderFamilie).getGruppenListe());
        } else {
            ausgewaehlteGruppenListe.add(((Gruppe) gruppeOderFamilie));
        }
        for (Gruppe g : ausgewaehlteGruppenListe) {
            try (Statement statement = conn.createStatement()) {
                try(ResultSet resultSet = statement.executeQuery("select exists (select * from gruppenkalender g where datum = \"" + firstWerktagOfYear.toString() + "\" and gruppe_id = " + g.getGruppenID() + ") as dayExists;")) {
                    while (resultSet.next()) {
                        if(!resultSet.getBoolean("dayExists")) {
                            System.out.println("Generate");
                            //TODO implement Generation
                        }
                    }
                }
            }
        }

    }

    private static LocalDate getFirstWerktagOfYear(Integer jahr) {
        Month monat = Month.JANUARY;
        LocalDate firstDayOfMonth = getFirstWerktagOfMonth(jahr, monat);
        return firstDayOfMonth;
    }

    private static LocalDate getFirstWerktagOfMonth(Integer jahr, Month monat) {
        LocalDate firstDayOfMonth = LocalDate.of(jahr, monat, 1);
        switch (firstDayOfMonth.getDayOfWeek()) {
            case SATURDAY -> firstDayOfMonth = firstDayOfMonth.plusDays(2);
            case SUNDAY -> firstDayOfMonth = firstDayOfMonth.plusDays(1);
            default -> {}
        }
        return firstDayOfMonth;
    }
}
