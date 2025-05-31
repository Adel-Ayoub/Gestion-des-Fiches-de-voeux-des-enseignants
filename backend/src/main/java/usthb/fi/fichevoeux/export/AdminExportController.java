/*package usthb.fi.fichevoeux.export;

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

    @GetMapping("/teacher/{teacherId}/year/{academicYear:.+}")
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
*/

/*
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/admin/export")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://127.0.0.1:5173", "http://localhost:5173"})
public class AdminExportController {

    private static final Logger logger = LoggerFactory.getLogger(AdminExportController.class);
    private final AdminExportService adminExportService;

    @GetMapping("/fiche/{ficheId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportYearlyFicheVoeuxById(
            @PathVariable Long ficheId,
            @RequestParam String format) {
        String operation = "exportYearlyFicheVoeuxById";
        logger.info("({}) Received request for yearly fiche ID: {} with format: {}", operation, ficheId, format);
        return export(format,
                () -> adminExportService.exportFicheToPdf(ficheId),
                () -> adminExportService.exportFicheToExcel(ficheId),
                "fiche_voeux_" + ficheId,
                String.valueOf(ficheId),
                operation
        );
    }

    @GetMapping("/teacher/{teacherId}/year/{academicYear:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportTeacherYearlyVoeux(
            @PathVariable Long teacherId,
            @PathVariable String academicYear,
            @RequestParam String format) {
        String operation = "exportTeacherYearlyVoeux";

        try {
            // Decode the academic year parameter to handle URL encoding
            String decodedAcademicYear = URLDecoder.decode(academicYear, StandardCharsets.UTF_8);
            String identifier = "TeacherID:" + teacherId + "_Year:" + decodedAcademicYear;
            logger.info("({}) Received request for {}, format: {}", operation, identifier, format);

            return export(format,
                    () -> adminExportService.exportTeacherYearlyVoeuxToPdf(teacherId, decodedAcademicYear),
                    () -> adminExportService.exportTeacherYearlyVoeuxToExcel(teacherId, decodedAcademicYear),
                    "enseignant_" + teacherId + "_annee_" + decodedAcademicYear.replace("/", "_").replace("-", "_"),
                    identifier,
                    operation
            );
        } catch (Exception e) {
            logger.error("({}) Error processing request for teacherId: {}, academicYear: {}", operation, teacherId, academicYear, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all/year/{academicYear:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportAllFichesForAcademicYear(
            @PathVariable String academicYear,
            @RequestParam(defaultValue = "excel") String format) {
        String operation = "exportAllFichesForAcademicYear";

        try {
            // Decode the academic year parameter to handle URL encoding
            String decodedAcademicYear = URLDecoder.decode(academicYear, StandardCharsets.UTF_8);
            logger.info("({}) Received request for all fiches for academic year: {}, format: {}", operation, decodedAcademicYear, format);

            if (!"excel".equalsIgnoreCase(format)) {
                logger.warn("({}) Invalid export format requested: {}. Only 'excel' is supported for this bulk operation.", operation, format);
                return ResponseEntity.badRequest().body(null);
            }

            return export(format,
                    null,
                    () -> adminExportService.exportAllFichesToExcelByYear(decodedAcademicYear),
                    "toutes_les_fiches_" + decodedAcademicYear.replace("/", "_").replace("-", "_"),
                    "AcademicYear:" + decodedAcademicYear,
                    operation
            );
        } catch (Exception e) {
            logger.error("({}) Error processing request for academicYear: {}", operation, academicYear, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Add OPTIONS handler for preflight requests
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*")
                .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600")
                .build();
    }

    // Alternative endpoint using query parameters to avoid URL encoding issues
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportWithQueryParams(
            @RequestParam String startYear,
            @RequestParam String endYear,
            @RequestParam(defaultValue = "excel") String format) {
        String operation = "exportWithQueryParams";
        String academicYear = startYear + "/" + endYear;
        logger.info("({}) Received request for academic year: {}, format: {}", operation, academicYear, format);

        if (!"excel".equalsIgnoreCase(format)) {
            logger.warn("({}) Invalid export format requested: {}. Only 'excel' is supported for this bulk operation.", operation, format);
            return ResponseEntity.badRequest().body(null);
        }

        return export(format,
                null,
                () -> adminExportService.exportAllFichesToExcelByYear(academicYear),
                "toutes_les_fiches_" + startYear + "_" + endYear,
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
                    logger.error("({}) Excel export requested for identifier '{}', but no supplier is configured for this operation/format combination.", operation, identifier);
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
                }
                data = excelSupplier.get();
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";
            } else if ("pdf".equalsIgnoreCase(format)) {
                if (pdfSupplier == null) {
                    logger.error("({}) PDF export requested for identifier '{}', but no supplier is configured for this operation/format combination.", operation, identifier);
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
                }
                data = pdfSupplier.get();
                contentType = MediaType.APPLICATION_PDF_VALUE;
                fileExtension = ".pdf";
            } else {
                logger.warn("({}) Invalid export format requested: '{}' for identifier '{}'", operation, format, identifier);
                return ResponseEntity.badRequest().build();
            }

            if (data == null || data.length == 0) {
                logger.warn("({}) Export for identifier '{}' resulted in empty data. This might be due to no data found or an issue in generation.", operation, identifier);
                if (data == null) data = new byte[0];
            }

            String filename = String.format("%s_%s%s", baseFilename, timestamp, fileExtension);
            ByteArrayResource resource = new ByteArrayResource(data);
            logger.info("({}) Prepared file '{}' ({} bytes) for download for identifier: {}.", operation, filename, data.length, identifier);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(resource);

        } catch (ResourceNotFoundException e) {
            logger.warn("({}) Export failed for identifier {}: Resource not found - {}", operation, identifier, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            logger.error("({}) IO Error during export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logger.error("({}) Unexpected error during export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @FunctionalInterface
    interface PdfSupplier {
        byte[] get() throws Exception;
    }

    @FunctionalInterface
    interface ExcelSupplier {
        byte[] get() throws IOException;
    }
}*/


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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/admin/export")
@RequiredArgsConstructor
// REMOVED @CrossOrigin - let global CORS config handle it
public class AdminExportController {

    private static final Logger logger = LoggerFactory.getLogger(AdminExportController.class);
    private final AdminExportService adminExportService;

    @GetMapping("/fiche/{ficheId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportYearlyFicheVoeuxById(
            @PathVariable Long ficheId,
            @RequestParam String format) {
        String operation = "exportYearlyFicheVoeuxById";
        logger.info("({}) Received request for yearly fiche ID: {} with format: {}", operation, ficheId, format);
        return export(format,
                () -> adminExportService.exportFicheToPdf(ficheId),
                () -> adminExportService.exportFicheToExcel(ficheId),
                "fiche_voeux_" + ficheId,
                String.valueOf(ficheId),
                operation
        );
    }

    @GetMapping("/teacher/{teacherId}/year/{academicYear:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportTeacherYearlyVoeux(
            @PathVariable Long teacherId,
            @PathVariable String academicYear,
            @RequestParam String format) {
        String operation = "exportTeacherYearlyVoeux";

        try {
            // Decode the academic year parameter to handle URL encoding
            String decodedAcademicYear = URLDecoder.decode(academicYear, StandardCharsets.UTF_8);
            String identifier = "TeacherID:" + teacherId + "_Year:" + decodedAcademicYear;
            logger.info("({}) Received request for {}, format: {}", operation, identifier, format);

            return export(format,
                    () -> adminExportService.exportTeacherYearlyVoeuxToPdf(teacherId, decodedAcademicYear),
                    () -> adminExportService.exportTeacherYearlyVoeuxToExcel(teacherId, decodedAcademicYear),
                    "enseignant_" + teacherId + "_annee_" + decodedAcademicYear.replace("/", "_").replace("-", "_"),
                    identifier,
                    operation
            );
        } catch (Exception e) {
            logger.error("({}) Error processing request for teacherId: {}, academicYear: {}", operation, teacherId, academicYear, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all/year/{academicYear:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportAllFichesForAcademicYear(
            @PathVariable String academicYear,
            @RequestParam(defaultValue = "excel") String format) {
        String operation = "exportAllFichesForAcademicYear";

        try {
            // Decode the academic year parameter to handle URL encoding
            String decodedAcademicYear = URLDecoder.decode(academicYear, StandardCharsets.UTF_8);
            logger.info("({}) Received request for all fiches for academic year: {}, format: {}", operation, decodedAcademicYear, format);

            if (!"excel".equalsIgnoreCase(format)) {
                logger.warn("({}) Invalid export format requested: {}. Only 'excel' is supported for this bulk operation.", operation, format);
                return ResponseEntity.badRequest().body(null);
            }

            return export(format,
                    null,
                    () -> adminExportService.exportAllFichesToExcelByYear(decodedAcademicYear),
                    "toutes_les_fiches_" + decodedAcademicYear.replace("/", "_").replace("-", "_"),
                    "AcademicYear:" + decodedAcademicYear,
                    operation
            );
        } catch (Exception e) {
            logger.error("({}) Error processing request for academicYear: {}", operation, academicYear, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // REMOVED the manual OPTIONS handler - let global CORS config handle it

    // Add a direct endpoint that matches your URL pattern
    @GetMapping("/{startYear}/{endYear}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportByYearRange(
            @PathVariable String startYear,
            @PathVariable String endYear,
            @RequestParam(defaultValue = "excel") String format) {
        String operation = "exportByYearRange";
        String academicYear = startYear + "/" + endYear;
        logger.info("({}) Received request for academic year: {}, format: {}", operation, academicYear, format);

        if (!"excel".equalsIgnoreCase(format)) {
            logger.warn("({}) Invalid export format requested: {}. Only 'excel' is supported for this bulk operation.", operation, format);
            return ResponseEntity.badRequest().body(null);
        }

        return export(format,
                null,
                () -> adminExportService.exportAllFichesToExcelByYear(academicYear),
                "toutes_les_fiches_" + startYear + "_" + endYear,
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
                    logger.error("({}) Excel export requested for identifier '{}', but no supplier is configured for this operation/format combination.", operation, identifier);
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
                }
                data = excelSupplier.get();
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";
            } else if ("pdf".equalsIgnoreCase(format)) {
                if (pdfSupplier == null) {
                    logger.error("({}) PDF export requested for identifier '{}', but no supplier is configured for this operation/format combination.", operation, identifier);
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
                }
                data = pdfSupplier.get();
                contentType = MediaType.APPLICATION_PDF_VALUE;
                fileExtension = ".pdf";
            } else {
                logger.warn("({}) Invalid export format requested: '{}' for identifier '{}'", operation, format, identifier);
                return ResponseEntity.badRequest().build();
            }

            if (data == null || data.length == 0) {
                logger.warn("({}) Export for identifier '{}' resulted in empty data. This might be due to no data found or an issue in generation.", operation, identifier);
                if (data == null) data = new byte[0];
            }

            String filename = String.format("%s_%s%s", baseFilename, timestamp, fileExtension);
            ByteArrayResource resource = new ByteArrayResource(data);
            logger.info("({}) Prepared file '{}' ({} bytes) for download for identifier: {}.", operation, filename, data.length, identifier);

            // REMOVED manual CORS headers - let global CORS config handle it
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(resource);

        } catch (ResourceNotFoundException e) {
            logger.warn("({}) Export failed for identifier {}: Resource not found - {}", operation, identifier, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            logger.error("({}) IO Error during export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logger.error("({}) Unexpected error during export for identifier {}: {}", operation, identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @FunctionalInterface
    interface PdfSupplier {
        byte[] get() throws Exception;
    }

    @FunctionalInterface
    interface ExcelSupplier {
        byte[] get() throws IOException;
    }
}