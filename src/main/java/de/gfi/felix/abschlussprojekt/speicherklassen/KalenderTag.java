package de.gfi.felix.abschlussprojekt.speicherklassen;

import de.gfi.felix.abschlussprojekt.helferklassen.UsefullConstants;

import java.time.LocalDate;

public class KalenderTag extends Tag{
    private Boolean essenVerfuegbar;
    private Character status;
    private Integer gruppenID;
    private Boolean kuecheGeoffnet;

    public KalenderTag(LocalDate datum, Character status, Integer gruppenID, Boolean kuecheGeoffnet, Boolean isFeiertag) {
        this.datum = datum;
        this.status = status;
        this.gruppenID = gruppenID;
        this.kuecheGeoffnet = kuecheGeoffnet;
        this.isFeiertag = isFeiertag;

        if(this.isFeiertag) {
            this.status = UsefullConstants.getFeiertagStatus();
        } else if (this.status == UsefullConstants.getFeiertagStatus()) {
            this.status = UsefullConstants.getDefaulStatus();
        }
        calculateEssenVerfuegbar();
    }

    public void calculateEssenVerfuegbar() {
        if(!kuecheGeoffnet) {
            essenVerfuegbar = false;
        } else if(UsefullConstants.getStatusWithoutEssenList().contains(status)) {
            essenVerfuegbar = false;
        } else {
            essenVerfuegbar = true;
        }
    }

    public Boolean getEssenVerfuegbar() {
        return essenVerfuegbar;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
        calculateEssenVerfuegbar();
    }

    public Integer getGruppenID() {
        return gruppenID;
    }

    public void setGruppenID(Integer gruppenID) {
        this.gruppenID = gruppenID;
    }
}
