package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.time.LocalDate;

public class Tag {
    protected LocalDate datum;
    protected Boolean isFeiertag;

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
