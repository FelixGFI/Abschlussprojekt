package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.time.LocalDate;

public class KuechenTag extends Tag {
    private Integer kuechenStatus;

    public KuechenTag(LocalDate datum, Integer kuechenStatus) {
        this.datum = datum;
        this.kuechenStatus = kuechenStatus;
    }

    public Integer getKuechenStatus() {
        return kuechenStatus;
    }

    public void setKuechenStatus(Integer kuechenStatus) {
        this.kuechenStatus = kuechenStatus;
    }
}
