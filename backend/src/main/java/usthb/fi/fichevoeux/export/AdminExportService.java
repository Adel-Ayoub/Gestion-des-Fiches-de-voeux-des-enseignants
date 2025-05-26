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
import usthb.fi.fichevoeux.teacher.Teacher;
import usthb.fi.fichevoeux.teacher.TeacherRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
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
    private final TeacherRepository teacherRepository;

    private final PdfExportUtil pdfExportUtil;
    private final ExcelExportUtil excelExportUtil;

    record FicheFullData(
            FicheDeVoeux fiche,
            User user,
            Teacher teacherDomain,
            List<FicheChoice> s1Choices,
            List<FicheChoice> s2Choices
    ) {}

    private User fetchUserDetailsForFiche(FicheDeVoeux fiche) {
        if (fiche.getTeacherId() == null) {
            logger.warn("FicheDeVoeux with ID {} has a null teacherId.", fiche.getId());
            return null;
        }
        Optional<Teacher> teacherOpt = teacherRepository.findById(fiche.getTeacherId());
        if (teacherOpt.isEmpty()) {
            logger.warn("No Teacher found with ID {} (from FicheDeVoeux ID {}).", fiche.getTeacherId(), fiche.getId());
            return null;
        }
        Teacher teacher = teacherOpt.get();
        if (teacher.getUserId() == null) {
            logger.warn("Teacher with ID {} has a null userId.", teacher.getId());
            return null;
        }
        return userRepository.findById(teacher.getUserId()).orElseGet(() -> {
            logger.warn("No User found with ID {} (from Teacher ID {}).", teacher.getUserId(), teacher.getId());
            return null;
        });
    }


    @Transactional(readOnly = true)
    public byte[] exportFicheToPdf(Long yearlyFicheId) throws Exception {
        logger.info("Initiating PDF export for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        FicheDeVoeux yearlyFiche = findFicheByIdOrThrow(yearlyFicheId);
        User userDetails = fetchUserDetailsForFiche(yearlyFiche);

        List<FicheChoice> allChoices = ficheChoiceRepository.findByFicheId(yearlyFicheId);
        List<FicheChoice> s1Choices = allChoices.stream().filter(c -> "S1".equalsIgnoreCase(c.getTargetSemester())).collect(Collectors.toList());
        List<FicheChoice> s2Choices = allChoices.stream().filter(c -> "S2".equalsIgnoreCase(c.getTargetSemester())).collect(Collectors.toList());
        Map<Long, Module> moduleMap = getModuleMapForChoices(allChoices);

        ByteArrayOutputStream baos = pdfExportUtil.generateYearlyFichePdf(yearlyFiche, userDetails, s1Choices, s2Choices, moduleMap);
        logger.info("Generated PDF for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        return baos.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] exportFicheToExcel(Long yearlyFicheId) throws IOException {
        logger.info("Initiating Excel export for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        FicheDeVoeux yearlyFiche = findFicheByIdOrThrow(yearlyFicheId);
        User userDetails = fetchUserDetailsForFiche(yearlyFiche);

        List<FicheChoice> allChoices = ficheChoiceRepository.findByFicheId(yearlyFicheId);
        List<FicheChoice> s1Choices = allChoices.stream().filter(c -> "S1".equalsIgnoreCase(c.getTargetSemester())).collect(Collectors.toList());
        List<FicheChoice> s2Choices = allChoices.stream().filter(c -> "S2".equalsIgnoreCase(c.getTargetSemester())).collect(Collectors.toList());
        Map<Long, Module> moduleMap = getModuleMapForChoices(allChoices);

        ByteArrayOutputStream baos = excelExportUtil.generateYearlyFicheExcel(yearlyFiche, userDetails, s1Choices, s2Choices, moduleMap);
        logger.info("Generated Excel for YEARLY FicheDeVoeux ID: {}", yearlyFicheId);
        return baos.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] exportTeacherYearlyVoeuxToPdf(Long teacherDomainId, String academicYear) throws Exception {
        logger.info("Initiating PDF export for teacher domain ID: {} and year: {}", teacherDomainId, academicYear);
        FicheDeVoeux yearlyFiche = ficheDeVoeuxRepository
                .findByTeacherIdAndAcademicYear(teacherDomainId, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException("FicheDeVoeux not found for teacher (domain ID " + teacherDomainId + ") and year " + academicYear));
        return exportFicheToPdf(yearlyFiche.getId());
    }

    @Transactional(readOnly = true)
    public byte[] exportTeacherYearlyVoeuxToExcel(Long teacherDomainId, String academicYear) throws IOException {
        logger.info("Initiating Excel export for teacher domain ID: {} and year: {}", teacherDomainId, academicYear);
        FicheDeVoeux yearlyFiche = ficheDeVoeuxRepository
                .findByTeacherIdAndAcademicYear(teacherDomainId, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException("FicheDeVoeux not found for teacher (domain ID " + teacherDomainId + ") and year " + academicYear));
        return exportFicheToExcel(yearlyFiche.getId());
    }

    @Transactional(readOnly = true)
    public byte[] exportAllFichesToExcelByYear(String academicYear) throws IOException {
        logger.info("Initiating bulk Excel export for all fiches in academic year: {}", academicYear);
        List<FicheDeVoeux> fichesForYear = ficheDeVoeuxRepository.findByAcademicYear(academicYear);

        if (fichesForYear.isEmpty()) {
            logger.warn("No FicheDeVoeux found for academic year {} to export.", academicYear);
        }

        List<Long> teacherDomainIdsFromFiches = fichesForYear.stream()
                .map(FicheDeVoeux::getTeacherId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Teacher> teacherDomainMap = teacherRepository.findAllById(teacherDomainIdsFromFiches).stream()
                .collect(Collectors.toMap(Teacher::getId, Function.identity()));

        List<Long> userIdsFromTeachers = teacherDomainMap.values().stream()
                .map(Teacher::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userDetailsMap = userRepository.findAllById(userIdsFromTeachers).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<Long> ficheIds = fichesForYear.stream().map(FicheDeVoeux::getId).collect(Collectors.toList());
        Map<Long, List<FicheChoice>> allChoicesGroupedByFicheId = ficheChoiceRepository.findByFicheIdIn(ficheIds)
                .stream()
                .collect(Collectors.groupingBy(FicheChoice::getFicheId));

        Set<Long> allModuleIdsNeeded = allChoicesGroupedByFicheId.values().stream()
                .flatMap(List::stream)
                .map(FicheChoice::getModuleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Module> globalModuleMap = moduleRepository.findAllById(allModuleIdsNeeded).stream()
                .collect(Collectors.toMap(Module::getId, Function.identity()));


        List<FicheFullData> fichesFullDataList = new ArrayList<>();
        for (FicheDeVoeux fiche : fichesForYear) {
            Teacher teacherDomain = (fiche.getTeacherId() != null) ? teacherDomainMap.get(fiche.getTeacherId()) : null;

            User userDetails = (teacherDomain != null && teacherDomain.getUserId() != null)
                    ? userDetailsMap.get(teacherDomain.getUserId())
                    : null;

            List<FicheChoice> currentFicheChoices = allChoicesGroupedByFicheId.getOrDefault(fiche.getId(), Collections.emptyList());
            List<FicheChoice> s1Choices = currentFicheChoices.stream()
                    .filter(c -> "S1".equalsIgnoreCase(c.getTargetSemester()))
                    .collect(Collectors.toList());
            List<FicheChoice> s2Choices = currentFicheChoices.stream()
                    .filter(c -> "S2".equalsIgnoreCase(c.getTargetSemester()))
                    .collect(Collectors.toList());

            fichesFullDataList.add(new FicheFullData(fiche, userDetails, teacherDomain, s1Choices, s2Choices));
        }

        fichesFullDataList.sort(Comparator
                .comparing((FicheFullData data) -> data.user() != null ? data.user().getName() : null, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(data -> data.fiche().getId())
        );

        ByteArrayOutputStream baos = excelExportUtil.generateCombinedYearlyFichesExcel(academicYear, fichesFullDataList, globalModuleMap);
        logger.info("Generated combined Excel for {} fiches for academic year: {}", fichesFullDataList.size(), academicYear);
        return baos.toByteArray();
    }


    private FicheDeVoeux findFicheByIdOrThrow(Long ficheId) {
        return ficheDeVoeuxRepository.findById(ficheId)
                .orElseThrow(() -> {
                    logger.warn("FicheDeVoeux not found for export with ID: {}", ficheId);
                    return new ResourceNotFoundException("FicheDeVoeux not found with id " + ficheId);
                });
    }

    private Map<Long, Module> getModuleMapForChoices(List<FicheChoice> choices) {
        if (choices == null || choices.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> moduleIds = choices.stream()
                .map(FicheChoice::getModuleId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (moduleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Module> modules = moduleRepository.findAllById(moduleIds);
        return modules.stream()
                .collect(Collectors.toMap(Module::getId, Function.identity()));
    }
}