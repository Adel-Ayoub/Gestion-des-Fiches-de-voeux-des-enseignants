package usthb.fi.fichevoeux.export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import usthb.fi.fichevoeux.fichedevoeux.FicheChoice;
import usthb.fi.fichevoeux.fichedevoeux.FicheDeVoeux;
import usthb.fi.fichevoeux.module.Module;
import usthb.fi.fichevoeux.user.User;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PdfExportUtil {

    private static final Logger logger = LoggerFactory.getLogger(PdfExportUtil.class);

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font FONT_SECTION_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font FONT_LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font FONT_VALUE = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font FONT_TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font FONT_TABLE_CELL = FontFactory.getFont(FontFactory.HELVETICA, 9);


    public ByteArrayOutputStream generateYearlyFichePdf(
            FicheDeVoeux yearlyFiche, User teacher,
            List<FicheChoice> s1Choices, List<FicheChoice> s2Choices,
            Map<Long, Module> moduleMap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph header = new Paragraph("Fiche de Voeux Annuelle Enseignant", FONT_TITLE);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20f);
            document.add(header);

            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(80);
            metaTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            metaTable.setWidths(new float[]{1f, 3f});
            metaTable.setSpacingAfter(15f);

            addMetaRow(metaTable, "Année Académique:", yearlyFiche.getAcademicYear());
            String teacherInfo = teacher != null ? String.format("%s (%s)", teacher.getName(), teacher.getEmail()) : "ID: " + yearlyFiche.getTeacherId();
            addMetaRow(metaTable, "Enseignant:", teacherInfo);
            addMetaRow(metaTable, "Heures Supp S1:", Optional.ofNullable(yearlyFiche.getWantsSupplementaryHoursS1()).map(Object::toString).orElse("0"));
            addMetaRow(metaTable, "Heures Supp S2:", Optional.ofNullable(yearlyFiche.getWantsSupplementaryHoursS2()).map(Object::toString).orElse("0"));
            addMetaRow(metaTable, "PFE Licence Proposés:", Optional.ofNullable(yearlyFiche.getProposedPfeL()).map(Object::toString).orElse("0"));
            addMetaRow(metaTable, "PFE Master Proposés:", Optional.ofNullable(yearlyFiche.getProposedPfeM()).map(Object::toString).orElse("0"));
            document.add(metaTable);

            document.add(new Paragraph("Commentaires:", FONT_LABEL));
            String commentsText = Optional.ofNullable(yearlyFiche.getComments()).filter(c -> !c.trim().isEmpty()).orElse("Aucun commentaire.");
            Paragraph commentsPara = new Paragraph(commentsText, FONT_VALUE);
            commentsPara.setSpacingAfter(20f);
            document.add(commentsPara);


            Paragraph s1ChoiceHeader = new Paragraph("Choix des Modules - Semestre 1", FONT_SECTION_HEADER);
            s1ChoiceHeader.setSpacingBefore(10f);
            s1ChoiceHeader.setSpacingAfter(10f);
            document.add(s1ChoiceHeader);

            if (s1Choices == null || s1Choices.isEmpty()) {
                document.add(new Paragraph("Aucun choix de module enregistré pour le Semestre 1.", FONT_VALUE));
            } else {
                document.add(createChoicesTable(s1Choices, moduleMap));
            }

            document.add(new Paragraph("\n"));

            Paragraph s2ChoiceHeader = new Paragraph("Choix des Modules - Semestre 2", FONT_SECTION_HEADER);
            s2ChoiceHeader.setSpacingBefore(10f);
            s2ChoiceHeader.setSpacingAfter(10f);
            document.add(s2ChoiceHeader);

            if (s2Choices == null || s2Choices.isEmpty()) {
                document.add(new Paragraph("Aucun choix de module enregistré pour le Semestre 2.", FONT_VALUE));
            } else {
                document.add(createChoicesTable(s2Choices, moduleMap));
            }

        } catch (DocumentException e) {
            logger.error("Failed to generate PDF report for yearly fiche ID {}: {}", yearlyFiche.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF report for yearly fiche " + yearlyFiche.getId(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return baos;
    }

    private PdfPTable createChoicesTable(List<FicheChoice> choices, Map<Long, Module> moduleMap) {
        PdfPTable choicesTable = new PdfPTable(5);
        choicesTable.setWidthPercentage(100);
        try {
            choicesTable.setWidths(new float[]{4f, 1.5f, 1f, 1.2f, 1.2f});
        } catch (DocumentException e) {
            logger.warn("Error setting PDF table widths: {}", e.getMessage());
        }

        addHeaderCell(choicesTable, "Module (Niveau/Spécialité)");
        addHeaderCell(choicesTable, "Rang Choix");
        addHeaderCell(choicesTable, "Cours");
        addHeaderCell(choicesTable, "TD");
        addHeaderCell(choicesTable, "TP");

        for (FicheChoice choice : choices) {
            Module module = moduleMap.get(choice.getModuleId());

            String moduleDisplayString;
            if (module != null) {
                String moduleName = module.getModuleName();
                String originalLevel = module.getLevel();
                String processedLevel = (originalLevel != null) ? originalLevel.replaceAll("-S\\d+$", "").trim() : "";

                if (processedLevel.isEmpty()) {
                    moduleDisplayString = moduleName;
                } else {
                    moduleDisplayString = String.format("%s (%s)", moduleName, processedLevel);
                }
            } else {
                moduleDisplayString = "Module Inconnu (ID: " + choice.getModuleId() + ")";
            }

            addDataCell(choicesTable, moduleDisplayString, Element.ALIGN_LEFT);
            addDataCell(choicesTable, choice.getRank() != null ? choice.getRank().toString() : "-", Element.ALIGN_CENTER);
            addDataCell(choicesTable, Boolean.TRUE.equals(choice.getWantsCours()) ? "Oui" : "Non", Element.ALIGN_CENTER);
            addDataCell(choicesTable, choice.getWantsTd() != null ? choice.getWantsTd().toString() : "0", Element.ALIGN_CENTER);
            addDataCell(choicesTable, choice.getWantsTp() != null ? choice.getWantsTp().toString() : "0", Element.ALIGN_CENTER);
        }
        return choicesTable;
    }

    private void addMetaRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_LABEL));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(2f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "-", FONT_VALUE));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(2f);
        table.addCell(valueCell);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell headerCell = new PdfPCell(new Phrase(text, FONT_TABLE_HEADER));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setBackgroundColor(new Color(220, 220, 220));
        headerCell.setPadding(5f);
        table.addCell(headerCell);
    }

    private void addDataCell(PdfPTable table, String text, int horizontalAlignment) {
        PdfPCell dataCell = new PdfPCell(new Phrase(text != null ? text : "-", FONT_TABLE_CELL));
        dataCell.setHorizontalAlignment(horizontalAlignment);
        dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        dataCell.setPadding(4f);
        table.addCell(dataCell);
    }
}
