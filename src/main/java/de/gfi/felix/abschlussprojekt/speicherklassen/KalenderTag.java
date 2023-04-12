package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.time.LocalDate;

public class KalenderTag extends Tag{
    private Boolean essenVerfuegbar;
    private Character status;
    private Integer gruppenBeziechnung;

    public KalenderTag(LocalDate datum, Character status, Integer gruppenBeziechnung) {
        this.datum = datum;
        this.status = status;
        this.gruppenBeziechnung = gruppenBeziechnung;
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

    public Integer getGruppenBeziechnung() {
        return gruppenBeziechnung;
    }

    public void setGruppenBeziechnung(Integer gruppenBeziechnung) {
        this.gruppenBeziechnung = gruppenBeziechnung;
    }
}
