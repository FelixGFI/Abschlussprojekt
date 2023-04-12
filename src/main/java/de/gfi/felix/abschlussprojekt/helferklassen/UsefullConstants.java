package de.gfi.felix.abschlussprojekt.helferklassen;

import java.util.ArrayList;
import java.util.Arrays;

public class UsefullConstants {

    private final static ArrayList<Character> statusListCharacterFormat = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N', 'F'));
    private final static ArrayList<Character> statusListCharacterFormatOhneFeiertag = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N'));
    private final static ArrayList<String> statusListStringFormat = new ArrayList<>(Arrays.asList("Anwesend", "Homeoffice", "Auswärts", "Berufsschule", "Urlaub", "N/A", "gesetzlicher Feiertag"));
    private final static ArrayList<String> statusListStringFormatOhneFeiertag = new ArrayList<>(Arrays.asList("Anwesend", "Homeoffice", "Auswärts", "Berufsschule", "Urlaub", "N/A"));
    public static ArrayList<Character> getStatusListCharacterFormat () {
        return statusListCharacterFormat;
    }
    public static ArrayList<Character> getStatusListCharacterFormatOhneFeiertag () {
        return statusListCharacterFormatOhneFeiertag;
    }
    public static ArrayList<String> getStatusListStringFormat () {
        return statusListStringFormat;
    }
    public static ArrayList<String> getStatusListStringFormatOhneFeiertag () {
        return statusListStringFormatOhneFeiertag;
    }

}
