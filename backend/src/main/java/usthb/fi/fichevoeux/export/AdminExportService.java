package usthb.fi.fichevoeux.export;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.fichedevoeux.FicheChoice;
import usthb.fi.fichevoeux.fichedevoeux.FicheChoiceRepository;
import usthb.fi.fichevoeux.fichedevoeux.FicheDeVoeux;
import usthb.fi.fichevoeux.fichedevoeux.FicheDeVoeuxRepository;
import usthb.fi.fichevoeux.module.Module;
import usthb.fi.fichevoeux.module.ModuleRepository;
import usthb.fi.fichevoeux.user.User;
import usthb.fi.fichevoeux.user.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminExportService {

    private static final Logger logger = LoggerFactory.getLogger(AdminExportService.class);

    private final FicheDeVoeuxRepository ficheDeVoeuxRepository;
    private final FicheChoiceRepository ficheChoiceRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    private final PdfExportUtil pdfExportUtil;
    private final ExcelExportUtil excelExportUtil;

    @Transactional(readOnly = true)
    public byte[] exportFicheToPdf(Long yearlyFicheId) {
        logger.info("Initiating PDF export for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        FicheDeVoeux yearlyFiche = findFicheByIdOrThrow(yearlyFicheId);
        User teacher = userRepository.findById(yearlyFiche.getTeacherId()).orElse(null);

        List<FicheChoice> allChoices = ficheChoiceRepository.findByFicheId(yearlyFicheId);

        List<FicheChoice> s1Choices = allChoices.stream()
                .filter(c -> "S1".equalsIgnoreCase(c.getTargetSemester()))
                .collect(Collectors.toList());
        List<FicheChoice> s2Choices = allChoices.stream()
                .filter(c -> "S2".equalsIgnoreCase(c.getTargetSemester()))
                .collect(Collectors.toList());

        Map<Long, Module> moduleMap = getModuleMapForChoices(allChoices);

        ByteArrayOutputStream baos = pdfExportUtil.generateYearlyFichePdf(yearlyFiche, teacher, s1Choices, s2Choices, moduleMap);
        logger.info("Generated PDF for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        return baos.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] exportFicheToExcel(Long yearlyFicheId) throws IOException {
        logger.info("Initiating Excel export for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        FicheDeVoeux yearlyFiche = findFicheByIdOrThrow(yearlyFicheId);
        User teacher = userRepository.findById(yearlyFiche.getTeacherId()).orElse(null);
        List<FicheChoice> allChoices = ficheChoiceRepository.findByFicheId(yearlyFicheId);

        List<FicheChoice> s1Choices = allChoices.stream()
                .filter(c -> "S1".equalsIgnoreCase(c.getTargetSemester()))
                .collect(Collectors.toList());
        List<FicheChoice> s2Choices = allChoices.stream()
                .filter(c -> "S2".equalsIgnoreCase(c.getTargetSemester()))
                .collect(Collectors.toList());

        Map<Long, Module> moduleMap = getModuleMapForChoices(allChoices);

        ByteArrayOutputStream baos = excelExportUtil.generateYearlyFicheExcel(yearlyFiche, teacher, s1Choices, s2Choices, moduleMap);
        logger.info("Generated Excel for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        return baos.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] exportTeacherYearlyVoeuxToPdf(Long teacherId, String academicYear) {
        logger.info("Initiating PDF export for teacher ID: {} and year: {}", teacherId, academicYear);
        FicheDeVoeux yearlyFiche = ficheDeVoeuxRepository
                .findByTeacherIdAndAcademicYear(teacherId, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException("FicheDeVoeux not found for teacher " + teacherId + " and year " + academicYear));
        return exportFicheToPdf(yearlyFiche.getId());
    }

    @Transactional(readOnly = true)
    public byte[] exportTeacherYearlyVoeuxToExcel(Long teacherId, String academicYear) throws IOException {
        logger.info("Initiating Excel export for teacher ID: {} and year: {}", teacherId, academicYear);
        FicheDeVoeux yearlyFiche = ficheDeVoeuxRepository
                .findByTeacherIdAndAcademicYear(teacherId, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException("FicheDeVoeux not found for teacher " + teacherId + " and year " + academicYear));
        return exportFicheToExcel(yearlyFiche.getId());
    }

    private FicheDeVoeux findFicheByIdOrThrow(Long ficheId) {
        return ficheDeVoeuxRepository.findById(ficheId)
                .orElseThrow(() -> {
                    logger.warn("FicheDeVoeux not found for export with ID: {}", ficheId);
                    return new ResourceNotFoundException("FicheDeVoeux", "id", ficheId);
                });
    }

    private Map<Long, Module> getModuleMapForChoices(List<FicheChoice> choices) {
        if (choices == null || choices.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> moduleIds = choices.stream()
                .map(FicheChoice::getModuleId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if(moduleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Module> modules = moduleRepository.findAllById(moduleIds);
        return modules.stream()
                .collect(Collectors.toMap(Module::getId, Function.identity()));
    }
}