package de.gfi.felix.abschlussprojekt.speicherklassen;

import java.util.ArrayList;

public class GruppenFamilie extends GruppeOderFamilie {
    private Integer familienID;
    private String bezeichnung;
    private ArrayList<Gruppe> gruppenListe;


    public GruppenFamilie(Integer familienID, String bezeichnung, ArrayList<Gruppe> gruppenListe) {
        this.familienID = familienID;
        this.bezeichnung = bezeichnung;
        this.gruppenListe = gruppenListe;
        if(gruppenListe.isEmpty() || gruppenListe == null) {
            return;
        }
        for (Gruppe gruppe : gruppenListe) {
            gruppe.setFamilienID(this.familienID);
        }
    }

    public Integer getFamilienID() {
        return familienID;
    }

    public void setFamilienID(Integer familienID) {
        this.familienID = familienID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public ArrayList<Gruppe> getGruppenListe() {
        return gruppenListe;
    }

    public void addToGruppenListe(Gruppe gruppe) {
        this.gruppenListe.add(gruppe);
        gruppe.setFamilienID(this.familienID);
    }
}
