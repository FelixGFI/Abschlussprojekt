package de.gfi.felix.abschlussprojekt.helferklassen;

import de.gfi.felix.abschlussprojekt.gui.Controller;
import de.gfi.felix.abschlussprojekt.speicherklassen.Gruppe;
import de.gfi.felix.abschlussprojekt.speicherklassen.KalenderTag;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PDFCreator {

    /**
     * schreibt ein PDF Dokument für alle Einträge in der Übergebenen Einträge in der ObservableList des Typs KalenderTag welch
     * die Methode erhält. Fragt mittels Filechooser den Ort ab an welchem das PDF gespeichert werden soll. Ist der ort ungültig
     * oder schlägt das Speichern aus anderem Grund fehl wird ein Fehelrpopup erzeugt. Erzeugt im PDF Dokument eine Überschrift,
     * eine Tabelle mit Spaltenüberschriften und darunter einträge mit Korrekt formatiertem Datum, Gruppenbezeichung, Status und
     * Information ob Essen am besagten Tag verfügbar ist. Bricht der Nutzer die Pfadauswahl ab so wird die Methode beendet und
     * kein PDF erzeugt.
     * @param tage ObservableList<KalenderTag> welche in das PDF geschrieben werden sollen.
     * @param parentStage stage des aufrufenden Fensters damit im Bezugnahme darauf das Fenster des
     *                    Filechoosers geöffnet werden kann.
     * @param gruppenListe ArrayList<Gruppe> welche mindestens alle Gruppen enthalten sollte zu welchen Einträge in der
     *                     ObservableList tage vorhanden sind
     */
    public static void writeKalenderPDF(ObservableList<KalenderTag> tage, Stage parentStage, ArrayList<Gruppe> gruppenListe) {

        Integer fontSizeUeaberschrift = 20;
        Integer fontSizeTabelle = 15;

        if(tage.isEmpty()) {
            return;
        }
        String speicherOrt = requestSpeicherortFromUser(parentStage);
        if(speicherOrt.equals("") || speicherOrt == null) {
            return;
        }
        if(!speicherOrt.endsWith(".pdf")){
            Controller.createAndShowErrorAlert("Fehler", "Ungültiger Pfad",
                    "Der Ausgewählte Pfad ist ungültig oder existiert nicht." +
                            " Bitte stellen Sie sicher einen gültigen Pfad zu verwenden welcher auf ein PDF dokument verweist.");
            return;
        }
        try{
            PdfWriter writer = new PdfWriter(speicherOrt);
            // Creating a PdfDocument
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            // Adding a new page
            pdfDocument.addNewPage();
            // Creating a Document
            Document document = new Document(pdfDocument);
            Paragraph ueberschrift = new Paragraph();
            ueberschrift.setFontSize(fontSizeUeaberschrift);
            ueberschrift.setTextAlignment(TextAlignment.CENTER);
            ueberschrift.add("Gruppenkalender");

            Table tabelle = setupTabelle(fontSizeTabelle);

            for(KalenderTag tag : tage) {
                fuegeZeileFuerTagHinzu(fontSizeTabelle, tabelle, tag, gruppenListe);
            }

            document.add(ueberschrift);
            document.add(tabelle);
            document.close();

        } catch (FileNotFoundException fnfe) {
            Controller.createAndShowErrorAlert("Fehler", "Ungültiger Pfad",
                    "Der Ausgewählte Pfad ist ungültig oder existiert nicht." +
                            " Bitte stellen Sie sicher einen gültigen Pfad zu verwenden welcher auf ein PDF dokument verweist.");
        }
    }

    /**
     * öffnet in bezugnahme auf die übergebene Stage ein Filechooser Fenster und gibt den vom Nutzer ausgewählten Filepfad
     * zurück.Macht der Nutzer keine Eingabe wird ein Leerstring zurück gegeben.
     * @param parentStage stage des aufrufenden Fensters
     * @return String mit entweder ausgewähltem Filepfad oder einem leerstring wenn der nutzer nichts ausgewählt hat.
     */
    private static String requestSpeicherortFromUser(Stage parentStage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Speicherort Auswahl");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = chooser.showSaveDialog(parentStage);
        if(file == null) {
            return "";
        }
        return file.getAbsolutePath();
    }

    /**
     * generiert eine Leere Zelle mit Formatierung und fügt den übergebenen Paragrahph in sie hinzu
     * @param cellText Paragraph welchen die Zelle enthalten soll
     * @param fontSizeTabelle Font Size auf welchen die Zelle formatiert werden soll
     * @return die fertige Cell
     */
    private static Cell getCellWithContent(Paragraph cellText, Integer fontSizeTabelle) {
        Cell cell = new Cell();
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setFontSize(fontSizeTabelle);
        cell.add(cellText);
        return cell;
    }

    /**
     * erzegut eine Tabelle mit vier spalten, gedacht für eine DinA4 Seite. Erzeugt eine erste Zeile mit
     * Spaltenüberschriften mit dem Übergebenen Schriftgröße
     * @param fontSizeTabelle schriftgröße welche für die Überschriften verwendet werden soll
     * @return die Fertig aufgesetzte Tabelle der nun zeilen hinzugefügt werden können.
     */
    private static Table setupTabelle(Integer fontSizeTabelle) {
        float[] pointColumnWidths = {150F, 100F, 250F, 100F};
        Table table = new Table(pointColumnWidths);
        Paragraph datumText = new Paragraph();
        Paragraph gruppeText = new Paragraph();
        Paragraph statusText = new Paragraph();
        Paragraph essenVerfuegbarText = new Paragraph();

        datumText.add("Datum");
        gruppeText.add("Gruppe");
        statusText.add("Status");
        essenVerfuegbarText.add("Essen verfügbar");

        table.addCell(getCellWithContent(datumText, fontSizeTabelle));
        table.addCell(getCellWithContent(gruppeText, fontSizeTabelle));
        table.addCell(getCellWithContent(statusText, fontSizeTabelle));
        table.addCell(getCellWithContent(essenVerfuegbarText, fontSizeTabelle));
        return table;
    }

    /**
     * fügt der Übergebnene Tabelle (erzeugbar mit der Methode setupTabelle()) eine Zeile (vier Zellen) für den Übergebenen
     * KalenderTag hinzu. sorgt dafür für die Richtige Formatierung des Datums, Status und der Essensverfügbarkeit
     * sowie die richtige Gruppenbeziechung anhand der Übergebenen liste gruppenListe welche logischerweise mindestens
     * die Gruppe des übergebenen KalenderTages enthalten sollte. Soltle die Grupep des übergebenen KalenderTages nicht
     * gefunden werden wird einfach die GruppenID stattdessen verwendet.
     * @param fontSizeTabelle für die Zeile zu verwendende Schriftgröße
     * @param table Tabelle welcher die Zeile hinzugefügt werden soll
     * @param tag KalenderTag für den die Zeile erzeugt werden soll
     * @param gruppenListe ArrayList<Gruppe> aus welcher, fals vorhanden die richtige Gruppenbezeichung herausgesucht wird.
     */
    private static void fuegeZeileFuerTagHinzu(Integer fontSizeTabelle, Table table, KalenderTag tag, ArrayList<Gruppe> gruppenListe) {
        Paragraph datumText = new Paragraph();
        Paragraph gruppeText = new Paragraph();
        Paragraph statusText = new Paragraph();
        Paragraph essenVerfuegbarText = new Paragraph();

        datumText.add(getFormatedDatum(tag));
        gruppeText.add(getGruppenBezeichnungFuerTag(tag, gruppenListe));
        statusText.add(UsefullConstants.getStatusStringForCharacter(tag.getStatus()));
        essenVerfuegbarText.add(tag.getEssenVerfuegbar() ? "Ja" : "Nein");

        table.addCell(getCellWithContent(datumText, fontSizeTabelle));
        table.addCell(getCellWithContent(gruppeText, fontSizeTabelle));
        table.addCell(getCellWithContent(statusText, fontSizeTabelle));
        table.addCell(getCellWithContent(essenVerfuegbarText, fontSizeTabelle));
    }

    /**
     * Sucht aus der übergebenen ArrayList welche Gurppen enhält die passende Gruppe für den Übergebenen Tag heraus.
     * gibt die entsprechend Gruppenbeziechung als string zurück. Falls die Gruppe nicht gefunden wurde oder das
     * ermitteln der Beziechung auf andere weise fehlgeschlagen ist wird einfach die GruppenID des Tages als
     * String zurückgegeben.
     * @param tag KalenderTag für den die richtige Gruppenbezeichung gefunden werden soll
     * @param gruppenListe ArrayList<Gruppe> in welcher nach der richtigen Gruppe für den entsprechenden Tag gesucht wird
     *                     um seine Gruppenbezeichung zu ermitteln
     * @return im ideallfall die Richtige Gruppenbezeichung für den Tag als String. Alternativ die Gurppenid des Tages als String
     */
    private static String getGruppenBezeichnungFuerTag(KalenderTag tag, ArrayList<Gruppe> gruppenListe) {
        try {
            for (Gruppe gruppe : gruppenListe) {
                if(gruppe.getGruppenID() == tag.getGruppenID()) {
                    return gruppe.getBezeichnung();
                }
            }
        } catch (Exception e) {

        }
        return tag.getGruppenID().toString();
    }

    /**
     * formatiert das Datum des Übergebenen KalenderTages nach dem Musster "Wo, dd.MM.yyyy" also z. b. "Fr, 05.05.2023"
     * gibt das Datum in dem entsprechenden Format als string zurück.
     * @param tag Kalendertag für den das Datum formatiert werden soll
     * @return String welcher das jeweilige Datum im Korrekten Format enthält.
     */
    private static String getFormatedDatum(KalenderTag tag) {
        String datumText;
        switch(tag.getDatum().getDayOfWeek()) {
            case MONDAY -> datumText = "Mo, ";
            case TUESDAY -> datumText = "Di, ";
            case WEDNESDAY -> datumText = "Mi, ";
            case THURSDAY -> datumText = "Do, ";
            case FRIDAY -> datumText = "Fr, ";
            default -> datumText = "";
        }

        return datumText += tag.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }


}
