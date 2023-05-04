package de.gfi.felix.abschlussprojekt.helferklassen;

import java.util.ArrayList;
import java.util.Arrays;

public class UsefullConstants {

    private final static Character defaultStatus = 'N';
    private final static Character feiertagStatus = 'F';
    private final static ArrayList<Character> statusListCharacterFormat = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N', 'F'));
    private final static ArrayList<Character> statusListCharacterFormatOhneFeiertag = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N'));
    private final static ArrayList<Character> statusWithoutEssenList = new ArrayList<>(Arrays.asList('O', 'A', 'B', 'U', 'F'));
    private final static ArrayList<String> statusListStringFormat = new ArrayList<>(Arrays.asList("Anwesend", "Homeoffice", "Auswärts", "Berufsschule", "Urlaub", "N/A", "gesetzlicher Feiertag"));
    private final static ArrayList<String> statusListStringFormatOhneFeiertag = new ArrayList<>(Arrays.asList("Anwesend", "Homeoffice", "Auswärts", "Berufsschule", "Urlaub", "N/A"));
    private final static ArrayList<Integer> jahreListe = new ArrayList<>(Arrays.asList(2020, 2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2034, 2035, 2036, 2037, 2038, 2039, 2040));
    public static ArrayList<Character> getStatusListCharacterFormat () {
        return statusListCharacterFormat;
    }
    public static ArrayList<Character> getStatusListCharacterFormatOhneFeiertag () {
        return statusListCharacterFormatOhneFeiertag;
    }
    public static ArrayList<String> getStatusListStringFormatOhneFeiertag () {
        return statusListStringFormatOhneFeiertag;
    }
    public static ArrayList<String> getStatusListStringFormat () {
        return statusListStringFormat;
    }
    public static ArrayList<Integer> getJahreListe() {
        return jahreListe;
    }
    public static Character getDefaulStatus() {
        return defaultStatus;
    }
    public static ArrayList<Character> getStatusWithoutEssenList() {
        return statusWithoutEssenList;
    }
    public static Character getFeiertagStatus() {
        return feiertagStatus;
    }

    /**
     * gibt einen für einen übergebenen Status Character den entsprecehnden Status als String zurück.
     * @param statusCharacter Character zu dem der Status strign ermittelt werden soll
     * @return Status String wenn gefunden, wenn nicht gefunden null.
     */
    public static String getStatusStringForCharacter(Character statusCharacter) {
        String statusString;
        int statusIndex = UsefullConstants.getStatusListCharacterFormat().indexOf(statusCharacter);
        statusString = UsefullConstants.getStatusListStringFormat().get(statusIndex);
        return statusString;
    }
}
