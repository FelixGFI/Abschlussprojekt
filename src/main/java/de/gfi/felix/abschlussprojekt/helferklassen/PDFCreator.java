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

    //TODO Document all methods of class
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

    private static Cell getCellWithContent(Paragraph cellText, Integer fontSizeTabelle) {
        Cell cell = new Cell();
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setFontSize(fontSizeTabelle);
        cell.add(cellText);
        return cell;
    }

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

    private static void fuegeZeileFuerTagHinzu(Integer fontSizeTabelle, Table table, KalenderTag tag, ArrayList<Gruppe> gruppenListe) {
        Paragraph datumText = new Paragraph();
        Paragraph gruppeText = new Paragraph();
        Paragraph statusText = new Paragraph();
        Paragraph essenVerfuegbarText = new Paragraph();

        datumText.add(getFormatedDatum(tag));
        gruppeText.add(getGruppeFuerTag(tag, gruppenListe));
        statusText.add(UsefullConstants.getStatusStringForCharacter(tag.getStatus()));
        essenVerfuegbarText.add(tag.getEssenVerfuegbar() ? "Ja" : "Nein");

        table.addCell(getCellWithContent(datumText, fontSizeTabelle));
        table.addCell(getCellWithContent(gruppeText, fontSizeTabelle));
        table.addCell(getCellWithContent(statusText, fontSizeTabelle));
        table.addCell(getCellWithContent(essenVerfuegbarText, fontSizeTabelle));
    }

    private static String getGruppeFuerTag(KalenderTag tag, ArrayList<Gruppe> gruppenListe) {
        for (Gruppe gruppe : gruppenListe) {
            if(gruppe.getGruppenID() == tag.getGruppenID()) {
                return gruppe.getBezeichnung();
            }
        }
        return tag.getGruppenID().toString();
    }

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
