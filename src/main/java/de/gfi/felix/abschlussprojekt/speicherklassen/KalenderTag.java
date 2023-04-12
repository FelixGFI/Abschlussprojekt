package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.time.LocalDate;

public class KalenderTag extends Tag{
    private Boolean essenVerfuegbar;
    private Character status;
    private Integer gruppenBezeichnung;

    public KalenderTag(LocalDate datum, Character status, Integer gruppenBezeichnung) {
        this.datum = datum;
        this.status = status;
        this.gruppenBezeichnung = gruppenBezeichnung;
        calculateEssenVerfuegbar();
    }

    public void calculateEssenVerfuegbar() {
        //TODO implement Method
        essenVerfuegbar = true;
    }

    public Boolean getEssenVerfuegbar() {
        return essenVerfuegbar;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Integer getGruppenBezeichnung() {
        return gruppenBezeichnung;
    }

    public void setGruppenBezeichnung(Integer gruppenBezeichnung) {
        this.gruppenBezeichnung = gruppenBezeichnung;
    }
}
