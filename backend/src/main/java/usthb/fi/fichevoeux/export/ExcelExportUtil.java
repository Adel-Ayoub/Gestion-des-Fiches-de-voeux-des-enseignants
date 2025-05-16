package usthb.fi.fichevoeux.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import usthb.fi.fichevoeux.fichedevoeux.FicheChoice;
import usthb.fi.fichevoeux.fichedevoeux.FicheDeVoeux;
import usthb.fi.fichevoeux.module.Module;
import usthb.fi.fichevoeux.user.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExcelExportUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);

    public ByteArrayOutputStream generateYearlyFicheExcel(
            FicheDeVoeux yearlyFiche, User teacher,
            List<FicheChoice> s1Choices, List<FicheChoice> s2Choices,
            Map<Long, Module> moduleMap) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Fiche Voeux " + yearlyFiche.getId());

            Font titleFont = workbook.createFont(); titleFont.setBold(true); titleFont.setFontHeightInPoints((short) 14);
            CellStyle titleStyle = workbook.createCellStyle(); titleStyle.setFont(titleFont); titleStyle.setAlignment(HorizontalAlignment.CENTER);

            Font headerFont = workbook.createFont(); headerFont.setBold(true); headerFont.setFontHeightInPoints((short) 11);
            CellStyle headerStyle = workbook.createCellStyle(); headerStyle.setFont(headerFont); headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN); headerStyle.setBorderTop(BorderStyle.THIN); headerStyle.setBorderLeft(BorderStyle.THIN); headerStyle.setBorderRight(BorderStyle.THIN);

            Font boldFont = workbook.createFont(); boldFont.setBold(true);
            CellStyle boldLabelStyle = workbook.createCellStyle(); boldLabelStyle.setFont(boldFont);

            CellStyle dataCellStyle = workbook.createCellStyle(); dataCellStyle.setAlignment(HorizontalAlignment.LEFT);
            CellStyle centerDataStyle = workbook.createCellStyle(); centerDataStyle.cloneStyleFrom(dataCellStyle); centerDataStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle wrapStyle = workbook.createCellStyle(); wrapStyle.setWrapText(true); wrapStyle.setAlignment(HorizontalAlignment.LEFT); wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

            int rowIndex = 0;

            Row mainTitleRow = sheet.createRow(rowIndex++);
            Cell mainTitleCell = mainTitleRow.createCell(0);
            mainTitleCell.setCellValue("Fiche de Voeux Annuelle Enseignant");
            mainTitleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(mainTitleRow.getRowNum(), mainTitleRow.getRowNum(), 0, 4));

            rowIndex++;

            addKeyValueRow(sheet, rowIndex++, "Année Académique:", yearlyFiche.getAcademicYear(), boldLabelStyle, dataCellStyle);
            String teacherInfo = teacher != null ? String.format("%s (%s)", teacher.getName(), teacher.getEmail()) : "ID: " + yearlyFiche.getTeacherId();
            addKeyValueRow(sheet, rowIndex++, "Enseignant:", teacherInfo, boldLabelStyle, dataCellStyle);
            addKeyValueRow(sheet, rowIndex++, "H. Supp S1:", Optional.ofNullable(yearlyFiche.getWantsSupplementaryHoursS1()).map(Object::toString).orElse("0"), boldLabelStyle, centerDataStyle);
            addKeyValueRow(sheet, rowIndex++, "H. Supp S2:", Optional.ofNullable(yearlyFiche.getWantsSupplementaryHoursS2()).map(Object::toString).orElse("0"), boldLabelStyle, centerDataStyle);
            addKeyValueRow(sheet, rowIndex++, "PFE Proposés:", Optional.ofNullable(yearlyFiche.getProposedPfe()).map(Object::toString).orElse("0"), boldLabelStyle, centerDataStyle);

            Row commentRow = sheet.createRow(rowIndex++);
            Cell commentLabelCell = commentRow.createCell(0); commentLabelCell.setCellValue("Commentaires:"); commentLabelCell.setCellStyle(boldLabelStyle);
            Cell commentValueCell = commentRow.createCell(1); commentValueCell.setCellValue(Optional.ofNullable(yearlyFiche.getComments()).filter(c->!c.trim().isEmpty()).orElse("Aucun")); commentValueCell.setCellStyle(wrapStyle);
            sheet.addMergedRegion(new CellRangeAddress(commentRow.getRowNum(), commentRow.getRowNum(), 1, 4));


            rowIndex += 2;

            rowIndex = renderChoicesSection(sheet, rowIndex, "Choix des Modules - Semestre 1", s1Choices, moduleMap, headerStyle, dataCellStyle, centerDataStyle, boldLabelStyle);

            rowIndex += 2;

            renderChoicesSection(sheet, rowIndex, "Choix des Modules - Semestre 2", s2Choices, moduleMap, headerStyle, dataCellStyle, centerDataStyle, boldLabelStyle);

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            if (sheet.getColumnWidth(0) < 10000) sheet.setColumnWidth(0, 10000);
            if (sheet.getColumnWidth(1) < 3000) sheet.setColumnWidth(1, 3000);


            workbook.write(baos);
            return baos;
        } catch (IOException e) {
            logger.error("Error generating Excel for yearly fiche ID {}: {}", yearlyFiche.getId(), e.getMessage(), e);
            throw e;
        }
    }

    private int renderChoicesSection(Sheet sheet, int startRowIndex, String sectionTitle, List<FicheChoice> choices, Map<Long, Module> moduleMap,
                                     CellStyle headerStyle, CellStyle dataCellStyle, CellStyle centerDataStyle, CellStyle sectionTitleStyle) {
        int rowIndex = startRowIndex;

        Row choiceSectionTitleRow = sheet.createRow(rowIndex++);
        Cell choiceSectionTitleCell = choiceSectionTitleRow.createCell(0);
        choiceSectionTitleCell.setCellValue(sectionTitle);
        choiceSectionTitleCell.setCellStyle(sectionTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(choiceSectionTitleRow.getRowNum(), choiceSectionTitleRow.getRowNum(), 0, 4));

        Row choiceHeaderRow = sheet.createRow(rowIndex++);
        String[] columns = {"Module (Niveau/Spécialité)", "Rang Choix", "Cours", "TD", "TP"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = choiceHeaderRow.createCell(i); cell.setCellValue(columns[i]); cell.setCellStyle(headerStyle);
        }

        if (choices != null && !choices.isEmpty()) {
            for (FicheChoice choice : choices) {
                Row dataRow = sheet.createRow(rowIndex++);
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

                Cell cell0 = dataRow.createCell(0); cell0.setCellValue(moduleDisplayString); cell0.setCellStyle(dataCellStyle);
                Cell cell1 = dataRow.createCell(1); cell1.setCellValue(choice.getRank() != null ? choice.getRank().toString() : "-"); cell1.setCellStyle(centerDataStyle);
                Cell cell2 = dataRow.createCell(2); cell2.setCellValue(Boolean.TRUE.equals(choice.getWantsCours()) ? "Oui" : "Non"); cell2.setCellStyle(centerDataStyle);

                Integer wantsTdGroups = choice.getWantsTd();
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue(wantsTdGroups != null ? wantsTdGroups.toString() : "0");
                cell3.setCellStyle(centerDataStyle);

                Integer wantsTpGroups = choice.getWantsTp();
                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue(wantsTpGroups != null ? wantsTpGroups.toString() : "0");
                cell4.setCellStyle(centerDataStyle);
            }
        } else {
            Row noDataRow = sheet.createRow(rowIndex++);
            Cell noDataCell = noDataRow.createCell(0);
            noDataCell.setCellValue("Aucun choix de module enregistré pour cette période.");
            sheet.addMergedRegion(new CellRangeAddress(noDataRow.getRowNum(), noDataRow.getRowNum(), 0, 4));
        }
        return rowIndex;
    }

    private void addKeyValueRow(Sheet sheet, int rowIndex, String key, String value, CellStyle keyStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowIndex);
        Cell keyCell = row.createCell(0); keyCell.setCellValue(key); if (keyStyle != null) keyCell.setCellStyle(keyStyle);
        Cell valueCell = row.createCell(1); valueCell.setCellValue(value != null ? value : "-"); if (valueStyle != null) valueCell.setCellStyle(valueStyle);
    }
}