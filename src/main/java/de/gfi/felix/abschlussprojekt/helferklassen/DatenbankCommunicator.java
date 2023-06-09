package de.gfi.felix.abschlussprojekt.helferklassen;

import de.gfi.felix.abschlussprojekt.gui.Controller;
import de.gfi.felix.abschlussprojekt.speicherklassen.*;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;

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
            Controller.createAndShowErrorAlert("Fehler", "Datenbankfehler",
                    "Verbindung mit der Datenbank fehlgeschlagen. Bitte stellen Sie sicher das die Datenbank angeschaltet und verfügbar ist.");
            System.out.println( "Datenbankverbindung Fehlgeschlagen. Bitte Stellen Sie sicher das das Programm zugriff auf die Datenbank hat. [" + url + "]");
        }
    }

    /**
     * fragt aus der Datenbank alle vorhandenen Gruppenfamilien und die zu ihnen Gehörenden Gruppen ab.
     * Erzeugt in jeder Gruppenfamilie eine Vollständige Arraylist aller zugehörigen Gruppen (eine Gruppe gehört
     * immer nur zu einer Familie) und gibt eine ArrayList aller Gruppenfamilien zurück (die enthaltenen Gruppen
     * können von der entsprechenden GruppenFamilie erhalten werden)
     * @return eine ArrayList aller GruppenFamilien aus der Datenbank welche jeweils alle zu ihnen gehörenden
     * Gruppen enthalten
     * @throws SQLException
     */
    public static ArrayList<GruppenFamilie> dbAbfrageGruppenUndFamilien() throws SQLException {
        ArrayList<GruppenFamilie> familienListe = new ArrayList<>();
        int akktuelleFamilienID = -1;
        GruppenFamilie akktuelleFamilie = null;
        try (Statement statement = conn.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery("select g.id, g.name, g.gruppenfamilie_id, g2.name as \"familienName\" " +
                    "from gruppe g, gruppenfamilie g2 where g.gruppenfamilie_id = g2.id order by g.gruppenfamilie_id;")) {
                while (resultSet.next()) {
                    akktuelleFamilienID = resultSet.getInt("gruppenfamilie_id");
                    if(akktuelleFamilie == null) {
                        akktuelleFamilie = new GruppenFamilie(resultSet.getInt("gruppenfamilie_id"), resultSet.getString("familienName"), new ArrayList<Gruppe>());
                    } else if(akktuelleFamilienID != akktuelleFamilie.getFamilienID()) {
                        familienListe.add(akktuelleFamilie);
                        akktuelleFamilie = new GruppenFamilie(resultSet.getInt("gruppenfamilie_id"), resultSet.getString("familienName"), new ArrayList<Gruppe>());
                    }
                    akktuelleFamilie.addToGruppenListe(new Gruppe(resultSet.getInt("id"), resultSet.getString("name")));
                }
                familienListe.add(akktuelleFamilie);
            }
        }
        return familienListe;
    }

    /**
     * leist für das gegebene Jahr und die gegebene Gruppe oder Gruppenfamilie alle Kalenderdatensätze aus der Datenbank aus
     * und gibt diese als ArrayList<KalenderTage> zurück. Wird eine Gruppe übergeben bearbeitet die Methode diese, wird eien
     * GruppenFamilie übergeben erkentn die Methode dies und bearbeitet alle Gruppen dieser Familie. (Alle zugehörigen gruppen
     * sind in der Gruppenfamilie als ArrayList enthalten). Überprüft vor dder Datenbankabfrage ob Kalenderdatensätze für
     * jeder Gruppe und das entsprechende Jahr vorhanden sind. Generiert diese fals noch nicht vorhanden
     * @param gruppeOderFamilie Grupep oder Gruppenfamilie für die Kalenderdatensätze ausgegelsesn werden sollen
     * @param jahr jahr für welche die Auslesung durchgeführt werden soll
     * @return ArrayListy<KalenderTag> welche für alle Werktage des übergebenen Jahres einen Datensatz für jede Gruppe enthält
     * @throws SQLException
     */
    public static ArrayList<KalenderTag> dbAbfrageKalenderDaten(GruppeOderFamilie gruppeOderFamilie, Integer jahr) throws SQLException {
        ArrayList<KalenderTag> ausgewaelteTage = new ArrayList<>();

        LocalDate firstWerktagOfYear = getFirstWerktagOfYear(jahr);
        checkForKalenderDatensatzAndGenerateIfMissing(gruppeOderFamilie, jahr);
        ArrayList<Gruppe> ausgewaelteGruppen = new ArrayList<>();
        if(gruppeOderFamilie.getClass() == GruppenFamilie.class) {
            ausgewaelteGruppen.addAll(((GruppenFamilie) gruppeOderFamilie).getGruppenListe());
        } else {
            ausgewaelteGruppen.add((Gruppe) gruppeOderFamilie);
        }
        for (Gruppe gruppe : ausgewaelteGruppen) {
            try (Statement statement = conn.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("select g.gruppe_id , g.datum, g.gruppenstatus, if(g.datum = f.datum, 1, 0) as feiertag, k.geoeffnet from gruppenkalender g  \n" +
                        "left join kuechenplanung k on k.datum = g.datum left join feiertag f on f.datum = g.datum\n" +
                        "where g.datum >= \""+ jahr +"-01-01\" and g.datum <= \"" + jahr + "-12-31\" and g.gruppe_id = " + gruppe.getGruppenID() +";")) {
                    while (resultSet.next()) {
                        Integer gruppenID = resultSet.getInt("gruppe_id");
                        LocalDate datum = LocalDate.parse(resultSet.getDate("datum").toString());
                        Character status = resultSet.getString("gruppenstatus").charAt(0);
                        Boolean kuecheGeoffnet = resultSet.getBoolean("geoeffnet");
                        Boolean isFeiertag = resultSet.getBoolean("feiertag");
                        KalenderTag tag = new KalenderTag(datum, status, gruppenID, kuecheGeoffnet, isFeiertag);
                        ausgewaelteTage.add(tag);
                    }
                }
            }
        }
        return ausgewaelteTage;
    }

    /**
     * überprüft für ein Objekt GruppeOderGruppenfamilie ob für die Darin enthaltene Gruppe, oder Gruppen in der Gruppenfamilie
     * bereits Kalenderdatensätze in der Entsprechenden Tabelle Datenbankttabelle vorhanden sind für das gegebene Jahr.
     * Überprüft ob es sich beim Übergebnene GruppenOderGruppenfamilie objekt um eien Gruppe oder eine Gruppenfamilie handelt.
     * Fügt jeweils, entweder dei Gruppe oder alle in der Familie enthaltnen Gruppen zu eienr Arraylist hinzu.
     * Überprüft für jedes Element der Arraylist ob Datensätze für das gegebene Jahr vorhanden sind und erzeugt
     * wenn nicht alle für die entsprechende Gruppe und das Entsprechende Jahr.
     * Hinweis: Es wird davon Ausgegangen das Datensätez immer nur für ein Ganzes Jahr und nur für Werktage
     * in der Datenbank vorhandne sind (inklusive Feirtage die auf einen Werktag fallen). Daher genügt es
     * den Datensatz für den Ersten Werktag eiens Jahres zu prüfen. Ist dieser nicht in der Datenbank vorhanden kann
     * davon ausgegangen werden das für dieses Jahr und diese Gruppe noch keien Datensätze existieren. Die Methode generiert
     * Datensätze nur für alle Werktage eiens Jahres
     * @param gruppeOderFamilie kann sowohl eine Gruppe als auch eine GruppenFamilie enthlaten für welche geprüft werden soll
     *                          ob datensätze vorhanden sind und wenn nicht neue generiert werden.
     * @param jahr jahr für welches die Prüfung und bei bedarf generierung durchgeführt werden soll
     * @throws SQLException
     */
    private static void checkForKalenderDatensatzAndGenerateIfMissing(GruppeOderFamilie gruppeOderFamilie, Integer jahr) throws SQLException {
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
                            generateDummyDatensaetzeForYearAndGroups(jahr, g);
                        }
                    }
                }
            }
        }
    }

    /**
     * generirt für alle Werktage des übergebenen Jahres für die Übergebene Gruppe Kalenderdatensätze in der Datenbank.
     * Diese Methode sollte nur aufgerufen werden wenn für das besagte Jahr noch keine Datensätze existieren.
     * Hinweis: Es wird davon Ausgegangen das Datensetez immer nur für ein Ganzes Jarh und nur für Werktage
     * in der Datenbank vorhandne sind (inklusive Feirtage die auf einen Werktag fallen). Daher genügt es vor dem
     * Methodenaufruf den Ersten Werktag eines Jahres zu prüfen. Ist dieser nicht in der Datenbank vorhanden kann
     * davon ausgegangen werden das für dieses Jahr und diese Gruppe noch keien Datensätze existieren.
     * @param jahr jahr für das Datensätze generiert werden. Die Methode generiert Datensätze nur für alle Werktage
     * eiens Jahres.
     * @param gruppe Grupep für welche Datensätze generiert werden
     * @throws SQLException
     */
    private static void generateDummyDatensaetzeForYearAndGroups(Integer jahr, Gruppe gruppe) throws SQLException {
        ArrayList<LocalDate> alleWerktageImJahr = getAlleWerktageImJahr(jahr);

        try(Statement statement = conn.createStatement()){
            for (LocalDate datum : alleWerktageImJahr) {
                statement.execute("insert into gruppenkalender (gruppe_id, datum, essensangebot, gruppenstatus)" +
                        "Values ("+ gruppe.getGruppenID() + ", \"" + datum.toString() + "\", true, \"" + UsefullConstants.getDefaulStatus() + "\");");
            }
        }
    }

    /**
     * Diese Methode ermittelt für ein Jahr ALLE Werktage (d. h. kein Wochenende) und gibt diese in einer ArrayList
     * von LocalDates zurück. Feiertage werden nicht berücksichtigt
     * @param jahr jahr für das alle Werktage ermitelt werden sollen
     * @return ArrayList<LocalDate> Welche alle Werktage(inkl. Feiertage die auf Werktage fallen) des übergebenen Jahres enthält
     */
    private static ArrayList<LocalDate> getAlleWerktageImJahr(Integer jahr) {
        LocalDate countDate = LocalDate.of(jahr, Month.JANUARY, 01);
        ArrayList<LocalDate> datumsListe = new ArrayList<>();
        while(countDate.getYear() == jahr) {
            if(!(countDate.getDayOfWeek() == DayOfWeek.SATURDAY || countDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                datumsListe.add(countDate);
            }
            countDate = countDate.plusDays(1);
        }
        return datumsListe;
    }

    /**
     * Ermittelt für das übergebene Jahres den Ersten Tag welcher ein Werktag (d. h. kein Wochenende)
     * ist und gibt für diesen das entsprechende LocalDate zurück. Feiertage werden NICHT berücksichtigt.
     * @param jahr jahr in welchem sich der Tag befinden soll
     * @return LocalDate des ermittelten Tages
     */
    public static LocalDate getFirstWerktagOfYear(Integer jahr) {
        Month monat = Month.JANUARY;
        LocalDate firstDayOfMonth = getFirstWerktagOfMonth(jahr, monat);
        return firstDayOfMonth;
    }

    /**
     * Ermittelt für den Übergebenen Monat des Übergebenen Jahres den Ersten Tag welcher ein Werktag (d. h. kein Wochenende)
     * ist und gibt für diesen das entsprechende LocalDate zurück. Feiertage werden NICHT berücksichtigt.
     * @param jahr jahr in welchem sich der Tag befinden soll
     * @param monat für den der Tag ermittelt werden soll
     * @return LocalDate des ermittelten Tages
     */
    public static LocalDate getFirstWerktagOfMonth(Integer jahr, Month monat) {
        LocalDate firstDayOfMonth = LocalDate.of(jahr, monat, 1);
        switch (firstDayOfMonth.getDayOfWeek()) {
            case SATURDAY -> firstDayOfMonth = firstDayOfMonth.plusDays(2);
            case SUNDAY -> firstDayOfMonth = firstDayOfMonth.plusDays(1);
            default -> {}
        }
        return firstDayOfMonth;
    }

    /**
     * fragt aus der Datenbank für ein übergebenes Jahr alle einträge der Tabelle kuechenplanung ab und konvertiert diese in
     * eine ArrayList der Klasse KuechenTag. Berücksichtigt dabei gesetzliche Feiertage. Sind für das gegebene Jarh noch keine
     * Datensätze vorhanden werden diese in der Datenbank zuerst genneiriert
     * @param jahr jahr für das daten ausgelesen werdne sollen
     * @return ArrayList<KuechenTag> welche aller Einträge in der tabelle keuchenplaunung aus der Datenbank für das gegebene Jahr enthält
     * @throws SQLException
     */
    public static ArrayList<KuechenTag> dbAbfrageKuechenTage(Integer jahr) throws SQLException {
        ArrayList<KuechenTag> ausgewaelteTage = new ArrayList<>();
        LocalDate firstWerktagOfYear = getFirstWerktagOfYear(jahr);
        checkForKuechenDatensatzAndGenerateIfMissing(jahr);

        try (Statement statement = conn.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select k.datum, k.geoeffnet , if(k.datum = f.datum, 1, 0) as feiertag \n" +
                    "from kuechenplanung k left join feiertag f on f.datum = k.datum\n" +
                    "where k.datum >= \"" + jahr + "-01-01\" and k.datum <= \"" + jahr + "-12-31\";")) {
                while (resultSet.next()) {
                    LocalDate datum = LocalDate.parse(resultSet.getDate("datum").toString());

                    Boolean geoeffnet = resultSet.getBoolean("geoeffnet");
                    Boolean isFeiertag = resultSet.getBoolean("feiertag");
                    Integer kuechenStatus = 0;

                    if(geoeffnet) {
                        kuechenStatus = 1;
                    }
                    if(isFeiertag) {
                        kuechenStatus = 2;
                    }

                    KuechenTag kTag = new KuechenTag(datum, kuechenStatus);
                    ausgewaelteTage.add(kTag);
                }
            }
        }
        return ausgewaelteTage;
    }

    /**
     * Erstellt eine Arraylist mit BetriebsurlaubsTag obejekten für das gegebene Jahr. Liest aus der Datenbank aus welcher Tag ein
     * Betriebsurlaubstag ist, berücksichtigt gesetzliche Feiertage. Da die datenbanktabelle für den Betriebsurlaub nur einträge für Tage
     * enthälte die tatsächlich Betriebsurlaub sind wird, damit für jeden Werktag des Jahres ein eintrag in der ArrayList erstellt werden kann
     * das Datum aus der tabelle kuechenplanung der Datenbank verwendet. sind in dieser Tabelle für das gegebene jahr noch keine einträge
     * vorhanden werden sie generiert.
     * @param jahr jahr für das Daten ausgelesen werden sollen
     * @return ArrayList<BetriebsurlaubsTag> welche BetriebsurlaubsTag objekte für jeden Werktag des übergebenen Jahres enthält
     * @throws SQLException
     */
    public static ArrayList<BetriebsurlaubsTag> dbAbfrageBetriebsurlaubTage(Integer jahr) throws SQLException {
        ArrayList<BetriebsurlaubsTag> ausgewaelteTage = new ArrayList<>();
        LocalDate firstWerktagOfYear = getFirstWerktagOfYear(jahr);

        // This is neccesary becaus the SQL Command uses the datum atribute from the table Containing KeuchenDatensatz
        checkForKuechenDatensatzAndGenerateIfMissing(jahr);

        try (Statement statement = conn.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select k.datum, if(b.datum is null, 0, 1) as betriebsurlaub, if(k.datum = f.datum, 1, 0) as feiertag \n" +
                    "from kuechenplanung k left join betriebsurlaub b on k.datum  = b.datum left join feiertag f on f.datum = k.datum\n" +
                    "where k.datum >= \"" + jahr + "-01-01\" and k.datum <= \"" + jahr + "-12-31\";")) {
                while (resultSet.next()) {
                    LocalDate datum = LocalDate.parse(resultSet.getDate("datum").toString());

                    Boolean isBetriebsurlaub = resultSet.getBoolean("betriebsurlaub");
                    Boolean isFeiertag = resultSet.getBoolean("feiertag");
                    Integer betriebsurlaubsStatus = 0;

                    if(isBetriebsurlaub) {
                        betriebsurlaubsStatus = 1;
                    }
                    if(isFeiertag) {
                        betriebsurlaubsStatus = 2;
                    }

                    BetriebsurlaubsTag bTag = new BetriebsurlaubsTag(datum, betriebsurlaubsStatus);
                    ausgewaelteTage.add(bTag);
                }
            }
        }
        return ausgewaelteTage;
    }

    /**
     * Überprüft ob in der Tabelle kuechenplanung für ein gegebenes Jahr Datensätze vorhanden sind. Generiert diese wenn nicht.
     * Anmerkung: Daten können nur für alle Werktage eines Jahres (inklusive gesetzlicher Feiertage welche auf werktage fallen)
     * generiert werden. Deshalb wird hier eine Abfrage für den ersten Werktag eines Jahres gemacht. Ist ein eintrag für diesen
     * Vorhanden wird davon ausgegangen das für das Jahr alle Datensätze vollständig sind.
     * @param jahr jahr für welches Daten überprüft und möglicherweise generiert werden sollen.
     * @throws SQLException
     */
    private static void checkForKuechenDatensatzAndGenerateIfMissing(Integer jahr) throws SQLException {
        LocalDate firstWerktagOfYear = getFirstWerktagOfYear(jahr);
        try (Statement statement = conn.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery("select exists (select * from kuechenplanung k where datum = \"" + firstWerktagOfYear.toString() + "\") as dayExists;")) {
                while (resultSet.next()) {
                    if(!resultSet.getBoolean("dayExists")) {
                        generateDummyDatensaetzeFuerKuche(jahr);
                    }
                }
            }
        }
    }

    /**
     * generiert für alle Werktage des übergebenen jahres in der tabelle kuechenpalnung der Datenbank default datensätze
     * Diese Methode solte nru aufgerufen werdne wenn vorher geprüft wurde ob die entsprechenden Datensätze schon vorhanden sind
     * @param jahr jahr für das daten generiert werden sollen
     * @throws SQLException
     */
    private static void generateDummyDatensaetzeFuerKuche(Integer jahr) throws SQLException {
        ArrayList<LocalDate> alleWerktageImJahr = getAlleWerktageImJahr(jahr);

        try(Statement statement = conn.createStatement()){
            for (LocalDate datum : alleWerktageImJahr) {
                statement.execute("insert into kuechenplanung (datum, geoeffnet)" +
                        "Values (\"" + datum.toString() + "\", true);");
            }
        }
    }

    /**
     * Löscht alle Einträge der Tabelle Betriebsurlaub für das jahr auf welches sich die Datensätze in der übergebenen ObservableList mit
     * Betriebsurlaubstagen beziehen aus der Tabelle betriebsurlaub. Schreibt für alle BetriebsurlaubsTag Objekte aus deren Status hervorgeht
     * das für den besagten Tag betriebsurlaub angesetzt ist einen eintrag in die Tabelle betriebsurlaub in der Datenbank.
     * @param betriebsurlaubsTage ObersvableList<BetriebsurlaubsTag> nach der Die Datenbank aktualisiert werden soll. Darf nur Datensätze für
     *                            EIN JAHR enthalten. Nicht für mehrere.
     * @throws SQLException
     */
    public static void dbSpeichernBetriebsurlaubTage(ObservableList<BetriebsurlaubsTag> betriebsurlaubsTage) throws SQLException {
        Integer aktuellesJahr = betriebsurlaubsTage.get(0).getDatum().getYear();
        try(Statement statement = conn.createStatement()){
            statement.execute("delete from betriebsurlaub where datum >= \"" + aktuellesJahr + "-01-01\" and datum < \"" + (aktuellesJahr + 1) + "-01-01\"");
            for (BetriebsurlaubsTag tag : betriebsurlaubsTage) {
                if(tag.getIstBetriebsurlaub() == 1) {
                    statement.execute("insert into betriebsurlaub (datum) values (\"" + tag.getDatum().toString() + "\");");
                }
            }
        }
    }

    /**
     * Erhält eine ObesrvableList mit KeuchenTag Objekten und akktualisiert anhand der Daten aus dieser die Datenbanktabelle kuechenplanug.
     * Es wird davon ausgegangen das alle benötigten datensätze bereits vorhanden sind und nur akktualisert werden müssen (Update)
     * @param keuchenTage ObservableList<KuechenTag> anhand welcher die Datenbank akktualisiert wird.
     * @throws SQLException
     */
    public static void dbSpeichernKuechenTage(ObservableList<KuechenTag> keuchenTage) throws SQLException {
        try(Statement statement = conn.createStatement()){
            for (KuechenTag tag : keuchenTage) {
                boolean geoffnet = (tag.getKuechenStatus() == 1);
                System.out.println(geoffnet + " |" + tag.getDatum().toString() + "|");
                statement.execute("update kuechenplanung set geoeffnet = " + geoffnet + " where datum = \"" + tag.getDatum().toString() + "\";");
            }
        }
    }

    /**
     * Erhält eine ObservableList mit KalenderTag Objekten und akktualisiert an hand dieser die Datenbanktabelle gruppenkalender.
     * Es wird davon ausgegangen das alle benötigten datensätze bereits vorhanden sind und nur akktualisert werden müssen (Update).
     * @param kalenderTage ObservableList<KalenderTag> anhand welcher die Datenbank akktualisiert wird.
     * @throws SQLException
     */
    public static void dbSpeichernKalenderDaten(ObservableList<KalenderTag> kalenderTage) throws SQLException {
        try(Statement statement = conn.createStatement()){
            for (KalenderTag tag : kalenderTage) {
                statement.execute("update gruppenkalender" +
                        " set gruppenstatus = \"" + tag.getStatus() + "\", essensangebot = " + tag.getEssenVerfuegbar() +
                        " where datum = \"" + tag.getDatum() + "\" and gruppe_id = " + tag.getGruppenID() + ";");
            }
        }
    }

    /**
     * Fragt für das Übergebene Jahr alle Betriebsurlaubsdaten aus der Datenbank ab. gibt eine ArrayList<LocalDate> zurück
     * welche für jeden Eintrag in der Tabelle betriebsurlaub in der Datenbank für das gegebene Jahr, d. h. für jeden
     * Tag des Jahres der stand jetzt Betriebsurlaub ist, das entsprechende Datum enthält. Diese methode ist dazu die Daten
     * zu liefern welche benötigt werden um den bisher Festgelegten Betriebsurlaub in einen Gruppenkalender zu übernehmen,
     * daher der Name.
     * @param jahr jahr für das die Betriebsurlaubsdaten abgefafragt werden sollen.
     * @return ArrayList<LocalDate> mit dem Datum aller Tage des übergebenen jahres für die Betriebsurlaub angesetzt ist
     * @throws SQLException
     */
    public static ArrayList<LocalDate> dbAbfrageBetriebsurlaubUebernehmen(int jahr) throws SQLException {

        ArrayList<LocalDate> betriebsurlaubsDatenListe = new ArrayList<>();

        try (Statement statement = conn.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select * from betriebsurlaub b where datum >= \"" + jahr + "-01-01\" and datum < \"" + (jahr + 1) + "-01-01\"")) {
                while (resultSet.next()) {
                    LocalDate datum = LocalDate.parse(resultSet.getDate("datum").toString());
                    betriebsurlaubsDatenListe.add(datum);
                }
            }
        }
        return betriebsurlaubsDatenListe;
    }
}