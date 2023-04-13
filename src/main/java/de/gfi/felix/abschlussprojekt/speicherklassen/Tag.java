package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.time.LocalDate;

public class Tag {
    public LocalDate datum;
    public Boolean isFeiertag;

    public Boolean getFeiertag() {
        return isFeiertag;
    }
    public LocalDate getDatum() {
        return datum;
    }
    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }
}
