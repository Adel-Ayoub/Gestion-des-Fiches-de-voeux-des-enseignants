package usthb.fi.fichevoeux.fichedevoeux;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usthb.fi.fichevoeux.exception.OperationNotAllowedException;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheChoiceDto;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheSubmissionRequestDto;
import usthb.fi.fichevoeux.module.Module;
import usthb.fi.fichevoeux.module.ModuleRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FicheDeVoeuxSubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(FicheDeVoeuxSubmissionService.class);

    private final FicheDeVoeuxRepository ficheDeVoeuxRepository;
    private final FicheChoiceRepository ficheChoiceRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    public FicheDeVoeux submitFicheDeVoeux(Long teacherUserId, FicheSubmissionRequestDto requestDto) {
        logger.info("Attempting to submit yearly FicheDeVoeux for teacherId: {}, year: {}",
                teacherUserId, requestDto.getAcademicYear());

        FicheDeVoeux yearlyFiche = ficheDeVoeuxRepository
                .findByTeacherIdAndAcademicYear(teacherUserId, requestDto.getAcademicYear())
                .orElseGet(() -> {
                    FicheDeVoeux newFiche = new FicheDeVoeux();
                    newFiche.setTeacherId(teacherUserId);
                    newFiche.setAcademicYear(requestDto.getAcademicYear());
                    return newFiche;
                });

        yearlyFiche.setWantsSupplementaryHoursS1(requestDto.getWantsSupplementaryHoursS1());
        yearlyFiche.setWantsSupplementaryHoursS2(requestDto.getWantsSupplementaryHoursS2());
        yearlyFiche.setProposedPfe(requestDto.getProposedPfe());
        yearlyFiche.setComments(requestDto.getComments());

        FicheDeVoeux savedYearlyFiche = ficheDeVoeuxRepository.save(yearlyFiche);
        logger.info("Saved/Updated yearly FicheDeVoeux with ID: {}", savedYearlyFiche.getId());

        List<FicheChoice> existingChoices = ficheChoiceRepository.findByFicheId(savedYearlyFiche.getId());
        if (!existingChoices.isEmpty()) {
            ficheChoiceRepository.deleteAllInBatch(existingChoices);
            logger.info("Deleted {} existing choices for FicheDeVoeux ID: {}", existingChoices.size(), savedYearlyFiche.getId());
        }

        List<FicheChoice> allNewChoicesToSave = new ArrayList<>();
        Set<Long> processedModuleIdsS1 = new HashSet<>();
        Set<Long> processedModuleIdsS2 = new HashSet<>();

        if (requestDto.getSemester1Choices() != null) {
            for (FicheChoiceDto choiceDto : requestDto.getSemester1Choices()) {
                validateAndPrepareModuleChoice(choiceDto, 1, "Semester 1", processedModuleIdsS1);
                allNewChoicesToSave.add(mapToFicheChoiceEntity(choiceDto, savedYearlyFiche.getId(), "S1"));
            }
        }

        if (requestDto.getSemester2Choices() != null) {
            for (FicheChoiceDto choiceDto : requestDto.getSemester2Choices()) {
                validateAndPrepareModuleChoice(choiceDto, 2, "Semester 2", processedModuleIdsS2);
                allNewChoicesToSave.add(mapToFicheChoiceEntity(choiceDto, savedYearlyFiche.getId(), "S2"));
            }
        }

        if (!allNewChoicesToSave.isEmpty()) {
            ficheChoiceRepository.saveAll(allNewChoicesToSave);
            logger.info("Saved {} new choices for FicheDeVoeux ID: {}", allNewChoicesToSave.size(), savedYearlyFiche.getId());
        }

        return savedYearlyFiche;
    }

    private void validateAndPrepareModuleChoice(FicheChoiceDto choiceDto, int expectedModuleSemester, String conceptualSemesterSlot, Set<Long> processedModuleIdsInThisSemester) {
        if (choiceDto.getModuleId() == null) {
            throw new IllegalArgumentException("Module ID cannot be null for choices in " + conceptualSemesterSlot + ".");
        }

        Module module = moduleRepository.findById(choiceDto.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Module not found with ID: " + choiceDto.getModuleId() + " (chosen for " + conceptualSemesterSlot + ")."));

        if (module.getSemester() != expectedModuleSemester) {
            throw new OperationNotAllowedException(
                    "Module '" + module.getModuleName() + "' (ID: " + module.getId() + ") is for semester " + module.getSemester() +
                            " and cannot be chosen for the " + conceptualSemesterSlot + " slot (which expects modules of semester " + expectedModuleSemester + ")."
            );
        }

        if (choiceDto.getWantsTd() != null && choiceDto.getWantsTd() > 0 && !module.isHasTd()) {
            throw new OperationNotAllowedException(
                    "Module '" + module.getModuleName() + "' (ID: " + module.getId() + ") does not offer TD. Cannot request " + choiceDto.getWantsTd() + " TD groups for it."
            );
        }

        if (choiceDto.getWantsTp() != null && choiceDto.getWantsTp() > 0 && !module.isHasTp()) {
            throw new OperationNotAllowedException(
                    "Module '" + module.getModuleName() + "' (ID: " + module.getId() + ") does not offer TP. Cannot request " + choiceDto.getWantsTp() + " TP groups for it."
            );
        }

        if (!processedModuleIdsInThisSemester.add(choiceDto.getModuleId())) {
            throw new OperationNotAllowedException("Duplicate module ID " + choiceDto.getModuleId() + " found within the submitted choices for " + conceptualSemesterSlot + ".");
        }

        logger.debug("Module ID {} (Name: '{}') validated successfully for {} slot.",
                module.getId(), module.getModuleName(), conceptualSemesterSlot);
    }


    private FicheChoice mapToFicheChoiceEntity(FicheChoiceDto choiceDto, Long ficheId, String targetSemesterLabel) {
        FicheChoice choice = new FicheChoice();
        choice.setFicheId(ficheId);
        choice.setModuleId(choiceDto.getModuleId());
        choice.setRank(choiceDto.getRank());
        choice.setWantsCours(choiceDto.getWantsCours() != null ? choiceDto.getWantsCours() : false);
        choice.setWantsTd(choiceDto.getWantsTd() != null ? choiceDto.getWantsTd() : 0);
        choice.setWantsTp(choiceDto.getWantsTp() != null ? choiceDto.getWantsTp() : 0);
        choice.setTargetSemester(targetSemesterLabel);
        return choice;
    }
}