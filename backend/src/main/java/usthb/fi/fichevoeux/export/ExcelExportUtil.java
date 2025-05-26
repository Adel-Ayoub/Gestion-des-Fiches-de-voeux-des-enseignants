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
import java.util.regex.Pattern;

@Component
public class ExcelExportUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);
    private static final Pattern INVALID_SHEET_CHARS = Pattern.compile("[\\\\/*?\\[\\]:]");


    public ByteArrayOutputStream generateYearlyFicheExcel(
            FicheDeVoeux yearlyFiche, User userDetails,
            List<FicheChoice> s1Choices, List<FicheChoice> s2Choices,
            Map<Long, Module> moduleMap) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            String sheetNameBase = "Fiche";
            if (userDetails != null && userDetails.getName() != null && !userDetails.getName().isEmpty()) {
                sheetNameBase = userDetails.getName() + "_" + yearlyFiche.getAcademicYear().replace("-", "");
            } else {
                sheetNameBase = "Fiche_EnsID-" + yearlyFiche.getTeacherId() + "_" + yearlyFiche.getAcademicYear().replace("-", "");
            }
            String sheetName = sanitizeSheetName(sheetNameBase + "_FicheID-" + yearlyFiche.getId());


            Sheet sheet = workbook.createSheet(sheetName);
            populateSheetWithFicheData(sheet, workbook, yearlyFiche, userDetails, s1Choices, s2Choices, moduleMap);

            workbook.write(baos);
            return baos;
        } catch (IOException e) {
            logger.error("Error generating Excel for single yearly fiche ID {}: {}", yearlyFiche.getId(), e.getMessage(), e);
            throw e;
        }
    }

    public ByteArrayOutputStream generateCombinedYearlyFichesExcel(
            String academicYear,
            List<AdminExportService.FicheFullData> fichesDataList,
            Map<Long, Module> globalModuleMap) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            if (fichesDataList.isEmpty()) {
                Sheet sheet = workbook.createSheet("Information");
                Row row = sheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune fiche de voeux trouvée pour l'année académique " + academicYear);
                sheet.autoSizeColumn(0);
                workbook.write(baos);
                return baos;
            }

            for (AdminExportService.FicheFullData ficheData : fichesDataList) {
                FicheDeVoeux fiche = ficheData.fiche();
                User user = ficheData.user();

                String teacherNamePart = (user != null && user.getName() != null && !user.getName().isBlank())
                        ? user.getName()
                        : "Enseignant_ID-" + fiche.getTeacherId();

                String sheetName = sanitizeSheetName(teacherNamePart + "_Fiche-" + fiche.getId());

                Sheet sheet = workbook.createSheet(sheetName);
                populateSheetWithFicheData(sheet, workbook, fiche, user,
                        ficheData.s1Choices(), ficheData.s2Choices(), globalModuleMap);
            }
            workbook.write(baos);
            return baos;
        } catch (IOException e) {
            logger.error("Error generating combined Excel for academic year {}: {}", academicYear, e.getMessage(), e);
            throw e;
        }
    }


    private void populateSheetWithFicheData(
            Sheet sheet, Workbook workbook,
            FicheDeVoeux yearlyFiche, User userDetails,
            List<FicheChoice> s1Choices, List<FicheChoice> s2Choices,
            Map<Long, Module> moduleMap) {

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

        addKeyValueRow(sheet, rowIndex++, "Année Académique:", yearlyFiche.getAcademicYear(), boldLabelStyle, dataCellStyle, false);
        String teacherInfo = (userDetails != null && userDetails.getName() != null)
                ? String.format("%s (%s)", userDetails.getName(), userDetails.getEmail())
                : "ID Enseignant (Domaine): " + yearlyFiche.getTeacherId();
        addKeyValueRow(sheet, rowIndex++, "Enseignant:", teacherInfo, boldLabelStyle, dataCellStyle, false);
        addKeyValueRow(sheet, rowIndex++, "H. Supp S1:", Optional.ofNullable(yearlyFiche.getWantsSupplementaryHoursS1()).map(Object::toString).orElse("0"), boldLabelStyle, centerDataStyle, true);
        addKeyValueRow(sheet, rowIndex++, "H. Supp S2:", Optional.ofNullable(yearlyFiche.getWantsSupplementaryHoursS2()).map(Object::toString).orElse("0"), boldLabelStyle, centerDataStyle, true);
        addKeyValueRow(sheet, rowIndex++, "PFE Licence Proposés:", Optional.ofNullable(yearlyFiche.getProposedPfeL()).map(Object::toString).orElse("0"), boldLabelStyle, centerDataStyle, true);
        addKeyValueRow(sheet, rowIndex++, "PFE Master Proposés:", Optional.ofNullable(yearlyFiche.getProposedPfeM()).map(Object::toString).orElse("0"), boldLabelStyle, centerDataStyle, true);

        Row commentRow = sheet.createRow(rowIndex++);
        Cell commentLabelCell = commentRow.createCell(0); commentLabelCell.setCellValue("Commentaires:"); commentLabelCell.setCellStyle(boldLabelStyle);
        Cell commentValueCell = commentRow.createCell(1); commentValueCell.setCellValue(Optional.ofNullable(yearlyFiche.getComments()).filter(c -> !c.trim().isEmpty()).orElse("Aucun")); commentValueCell.setCellStyle(wrapStyle);

        sheet.addMergedRegion(new CellRangeAddress(commentRow.getRowNum(), commentRow.getRowNum(), 1, 4));

        float defaultRowHeightPoints = sheet.getDefaultRowHeightInPoints();
        int numLines = 1;
        if (commentValueCell.getStringCellValue() != null && !commentValueCell.getStringCellValue().isEmpty()) {
            numLines = commentValueCell.getStringCellValue().split("\r\n|\r|\n", -1).length;
            if (numLines < 5) {
                int estimatedLinesFromLength = (commentValueCell.getStringCellValue().length() / 60) +1;
                numLines = Math.max(numLines, estimatedLinesFromLength);
            }
        }

        float calculatedHeight = Math.max(defaultRowHeightPoints, (numLines * defaultRowHeightPoints * 0.9f));
        commentRow.setHeightInPoints(calculatedHeight);


        rowIndex++;

        rowIndex = renderChoicesSection(sheet, rowIndex, "Choix des Modules - Semestre 1", s1Choices, moduleMap, headerStyle, dataCellStyle, centerDataStyle, boldLabelStyle);

        rowIndex++;

        renderChoicesSection(sheet, rowIndex, "Choix des Modules - Semestre 2", s2Choices, moduleMap, headerStyle, dataCellStyle, centerDataStyle, boldLabelStyle);

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        sheet.setColumnWidth(0, Math.max(sheet.getColumnWidth(0), 15 * 256));
        sheet.setColumnWidth(1, Math.max(sheet.getColumnWidth(1), 40 * 256));
        if (sheet.getColumnWidth(2) < 6*256) sheet.setColumnWidth(2, 6*256);
        if (sheet.getColumnWidth(3) < 6*256) sheet.setColumnWidth(3, 6*256);
        if (sheet.getColumnWidth(4) < 6*256) sheet.setColumnWidth(4, 6*256);
    }

    private int renderChoicesSection(Sheet sheet, int startRowIndex, String sectionTitle, List<FicheChoice> choices, Map<Long, Module> moduleMap,
                                     CellStyle headerStyle, CellStyle dataCellStyle, CellStyle centerDataStyle, CellStyle boldLabelStyle) {
        int rowIndex = startRowIndex;

        Row choiceSectionTitleRow = sheet.createRow(rowIndex++);
        Cell choiceSectionTitleCell = choiceSectionTitleRow.createCell(0);
        choiceSectionTitleCell.setCellValue(sectionTitle);
        choiceSectionTitleCell.setCellStyle(boldLabelStyle);
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
                    String processedLevel = (originalLevel != null) ? originalLevel.replaceFirst("-S\\d+$", "").trim() : "";

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
            noDataCell.setCellStyle(dataCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(noDataRow.getRowNum(), noDataRow.getRowNum(), 0, 4));
        }
        return rowIndex;
    }

    private void addKeyValueRow(Sheet sheet, int rowIndex, String key, String value, CellStyle keyStyle, CellStyle valueStyle, boolean mergeValue) {
        Row row = sheet.createRow(rowIndex);
        Cell keyCell = row.createCell(0); keyCell.setCellValue(key); if (keyStyle != null) keyCell.setCellStyle(keyStyle);
        Cell valueCell = row.createCell(1); valueCell.setCellValue(value != null ? value : "-"); if (valueStyle != null) valueCell.setCellStyle(valueStyle);

        if (mergeValue) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 4));
        }
    }

    private String sanitizeSheetName(String name) {
        if (name == null || name.isEmpty()) {
            return "Sheet";
        }
        String sanitized = INVALID_SHEET_CHARS.matcher(name).replaceAll("_");
        if (sanitized.length() > 31) {
            sanitized = sanitized.substring(0, 31);
        }
        return sanitized;
    }
}