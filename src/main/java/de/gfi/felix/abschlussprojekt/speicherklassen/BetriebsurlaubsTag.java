package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag extends Tag {
    private Integer istBetriebsurlaub;

    public BetriebsurlaubsTag(LocalDate datum, Integer istBetriebsurlaub) {
        this.datum = datum;
        this.istBetriebsurlaub = istBetriebsurlaub;
    }

    public Integer getIstBetriebsurlaub() {
        return istBetriebsurlaub;
    }

    public void setIstBetriebsurlaub(Integer istBetriebsurlaub) {
        this.istBetriebsurlaub = istBetriebsurlaub;
    }
}
