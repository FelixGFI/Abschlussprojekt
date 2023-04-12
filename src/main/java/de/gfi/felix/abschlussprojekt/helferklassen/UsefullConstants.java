package de.gfi.felix.abschlussprojekt.helferklassen;

import java.util.ArrayList;
import java.util.Arrays;

public class UsefullConstants {

    private final static ArrayList<Character> statusListCharacterFormat = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N', 'F'));
    private final static ArrayList<Character> statusListCharacterFormatOhneFeiertag = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N'));

    public static ArrayList<Character> getStatusListCharacterFormat () {
        return statusListCharacterFormat;
    }
    public static ArrayList<Character> getStatusListCharacterFormatOhneFeiertag () {
        return statusListCharacterFormatOhneFeiertag;
    }

}
