package de.gfi.felix.abschlussprojekt.speicherklassen;

public class Gruppe extends GruppeOderFamilie {
    private Integer gruppenID;
    private String bezeichnung;
    private Integer familienID;

    public Gruppe(Integer gruppenID, String bezeichnung) {
        this.gruppenID = gruppenID;
        this.bezeichnung = bezeichnung;
    }

    public Integer getGruppenID() {
        return gruppenID;
    }

    public void setGruppenID(Integer gruppenID) {
        this.gruppenID = gruppenID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Integer getFamilienID() {
        return familienID;
    }

    protected void setFamilienID(Integer familienID) {
        this.familienID = familienID;
    }
}
