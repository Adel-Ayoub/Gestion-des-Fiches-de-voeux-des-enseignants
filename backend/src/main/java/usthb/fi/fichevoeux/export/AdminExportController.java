package usthb.fi.fichevoeux.export;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Resource> exportYearlyFicheVoeuxById(
            @PathVariable Long ficheId,
            @RequestParam String format) {
        String operation = "exportYearlyFicheVoeuxById";
        logger.info("({}}) Received request for yearly fiche ID: {} with format: {}", operation, ficheId, format);
        return export(format,
                () -> adminExportService.exportFicheToPdf(ficheId),
                () -> adminExportService.exportFicheToExcel(ficheId),
                "yearly_fiche_voeux_" + ficheId,
                ficheId,
                operation
        );
    }

    @GetMapping("/teacher/{teacherId}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Resource> exportTeacherYearlyVoeux(
            @PathVariable Long teacherId,
            @PathVariable String academicYear,
            @RequestParam String format) {
        String operation = "exportTeacherYearlyVoeux";
        logger.info("({}}) Received request for teacher ID: {}, year: {}, format: {}", operation, teacherId, academicYear, format);
        return export(format,
                () -> adminExportService.exportTeacherYearlyVoeuxToPdf(teacherId, academicYear),
                () -> adminExportService.exportTeacherYearlyVoeuxToExcel(teacherId, academicYear),
                "teacher_" + teacherId + "_year_" + academicYear.replace("-",""),
                teacherId,
                operation
        );
    }

    private ResponseEntity<Resource> export(String format,
                                            PdfSupplier pdfSupplier,
                                            ExcelSupplier excelSupplier,
                                            String baseFilename,
                                            Object identifier,
                                            String operation
    ) {
        try {
            byte[] data;
            String contentType;
            String fileExtension;
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if ("excel".equalsIgnoreCase(format)) {
                data = excelSupplier.get();
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";
            } else if ("pdf".equalsIgnoreCase(format)) {
                data = pdfSupplier.get();
                contentType = MediaType.APPLICATION_PDF_VALUE;
                fileExtension = ".pdf";
            } else {
                logger.warn("({}}) Invalid export format requested: {}", operation, format);
                return ResponseEntity.badRequest().body(null);
            }

            String filename = String.format("%s_%s%s", baseFilename, timestamp, fileExtension);
            ByteArrayResource resource = new ByteArrayResource(data);
            logger.info("({}}) Prepared file '{}' for download for identifier: {}.", operation, filename, identifier);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(resource);

        } catch (ResourceNotFoundException e) {
            logger.warn("({}}) Export failed for identifier {}: {}", operation, identifier, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            logger.error("({}}) IO Error during Excel export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        } catch (Exception e) {
            logger.error("({}}) Unexpected error during export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @FunctionalInterface
    interface PdfSupplier { byte[] get() throws Exception; }
    @FunctionalInterface
    interface ExcelSupplier { byte[] get() throws IOException; }
}

