package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.time.LocalDate;

public class KuechenTag extends Tag {
    private Integer tcKuechenStatus;

    public KuechenTag(LocalDate datum, Integer tcKuechenStatus) {
        this.datum = datum;
        this.tcKuechenStatus = tcKuechenStatus;
    }

    public Integer getTcKuechenStatus() {
        return tcKuechenStatus;
    }

    public void setTcKuechenStatus(Integer tcKuechenStatus) {
        this.tcKuechenStatus = tcKuechenStatus;
    }
}
