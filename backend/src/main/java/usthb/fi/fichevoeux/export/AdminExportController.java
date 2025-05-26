package usthb.fi.fichevoeux.export;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/admin/export")
@RequiredArgsConstructor
public class AdminExportController {

    private static final Logger logger = LoggerFactory.getLogger(AdminExportController.class);
    private final AdminExportService adminExportService;

    @GetMapping("/fiche/{ficheId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportYearlyFicheVoeuxById(
            @PathVariable Long ficheId,
            @RequestParam String format) {
        String operation = "exportYearlyFicheVoeuxById";
        logger.info("({}}) Received request for yearly fiche ID: {} with format: {}", operation, ficheId, format);
        return export(format,
                () -> adminExportService.exportFicheToPdf(ficheId),
                () -> adminExportService.exportFicheToExcel(ficheId),
                "fiche_voeux_" + ficheId,
                String.valueOf(ficheId),
                operation
        );
    }

    @GetMapping("/teacher/{teacherId}/year/{academicYear}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportTeacherYearlyVoeux(
            @PathVariable Long teacherId,
            @PathVariable String academicYear,
            @RequestParam String format) {
        String operation = "exportTeacherYearlyVoeux";
        String identifier = "TeacherDomainID:" + teacherId + "_Year:" + academicYear;
        logger.info("({}}) Received request for {}, format: {}", operation, identifier, format);
        return export(format,
                () -> adminExportService.exportTeacherYearlyVoeuxToPdf(teacherId, academicYear),
                () -> adminExportService.exportTeacherYearlyVoeuxToExcel(teacherId, academicYear),
                "enseignant_" + teacherId + "_annee_" + academicYear.replace("-",""),
                identifier,
                operation
        );
    }

    @GetMapping("/all/year/{academicYear}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportAllFichesForAcademicYear(
            @PathVariable String academicYear,
            @RequestParam(defaultValue = "excel") String format) {
        String operation = "exportAllFichesForAcademicYear";
        logger.info("({}}) Received request for all fiches for academic year: {}, format: {}", operation, academicYear, format);

        if (!"excel".equalsIgnoreCase(format)) {
            logger.warn("({}}) Invalid export format requested: {}. Only 'excel' is supported for this bulk operation.", operation, format);
            return ResponseEntity.badRequest().body(null);
        }

        return export(format,
                null,
                () -> adminExportService.exportAllFichesToExcelByYear(academicYear),
                "toutes_les_fiches_" + academicYear.replace("-", ""),
                "AcademicYear:" + academicYear,
                operation
        );
    }


    private ResponseEntity<Resource> export(String format,
                                            PdfSupplier pdfSupplier,
                                            ExcelSupplier excelSupplier,
                                            String baseFilename,
                                            String identifier,
                                            String operation
    ) {
        try {
            byte[] data;
            String contentType;
            String fileExtension;
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if ("excel".equalsIgnoreCase(format)) {
                if (excelSupplier == null) {
                    logger.error("({}}) Excel export requested for identifier '{}', but no supplier is configured for this operation/format combination.", operation, identifier);
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
                }
                data = excelSupplier.get();
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";
            } else if ("pdf".equalsIgnoreCase(format)) {
                if (pdfSupplier == null) {
                    logger.error("({}}) PDF export requested for identifier '{}', but no supplier is configured for this operation/format combination.", operation, identifier);
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
                }
                data = pdfSupplier.get();
                contentType = MediaType.APPLICATION_PDF_VALUE;
                fileExtension = ".pdf";
            } else {
                logger.warn("({}}) Invalid export format requested: '{}' for identifier '{}'", operation, format, identifier);
                return ResponseEntity.badRequest().build();
            }

            if (data == null || data.length == 0) {
                logger.warn("({}}) Export for identifier '{}' resulted in empty data. This might be due to no data found or an issue in generation.", operation, identifier);
                if (data == null ) data = new byte[0];
            }


            String filename = String.format("%s_%s%s", baseFilename, timestamp, fileExtension);
            ByteArrayResource resource = new ByteArrayResource(data);
            logger.info("({}}) Prepared file '{}' ({} bytes) for download for identifier: {}.", operation, filename, data.length, identifier);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(resource);

        } catch (ResourceNotFoundException e) {
            logger.warn("({}}) Export failed for identifier {}: Resource not found - {}", operation, identifier, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            logger.error("({}}) IO Error during export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logger.error("({}}) Unexpected error during export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @FunctionalInterface
    interface PdfSupplier { byte[] get() throws Exception; }
    @FunctionalInterface
    interface ExcelSupplier { byte[] get() throws IOException; }
}

