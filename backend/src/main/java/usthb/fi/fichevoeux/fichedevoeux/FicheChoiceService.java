package usthb.fi.fichevoeux.fichedevoeux;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usthb.fi.fichevoeux.exception.OperationNotAllowedException;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheChoiceDto;
import usthb.fi.fichevoeux.module.Module;
import usthb.fi.fichevoeux.module.ModuleRepository;

@Service
@RequiredArgsConstructor
public class FicheChoiceService {

    private static final Logger logger = LoggerFactory.getLogger(FicheChoiceService.class);

    private final FicheChoiceRepository ficheChoiceRepository;
    private final ModuleRepository moduleRepository;

    private FicheChoiceDto mapToDto(FicheChoice choice) {
        if (choice == null) return null;
        return new FicheChoiceDto(
                choice.getId(), choice.getFicheId(), choice.getModuleId(),
                choice.getRank(), choice.getWantsCours(), choice.getWantsTd(),
                choice.getWantsTp(), choice.getTargetSemester()
        );
    }

    private FicheChoice mapToEntity(FicheChoiceDto dto) {
        if (dto == null) return null;
        FicheChoice choice = new FicheChoice();
        choice.setFicheId(dto.getFicheId());
        choice.setModuleId(dto.getModuleId());
        choice.setRank(dto.getRank());
        choice.setWantsCours(dto.getWantsCours() != null ? dto.getWantsCours() : false);
        choice.setWantsTd(dto.getWantsTd() != null ? dto.getWantsTd() : 0);
        choice.setWantsTp(dto.getWantsTp() != null ? dto.getWantsTp() : 0);
        choice.setTargetSemester(dto.getTargetSemester());
        return choice;
    }

    private void updateEntityFromDto(FicheChoice existingChoice, FicheChoiceDto dto) {
        existingChoice.setModuleId(dto.getModuleId());
        existingChoice.setRank(dto.getRank());
        existingChoice.setWantsCours(dto.getWantsCours() != null ? dto.getWantsCours() : false);
        existingChoice.setWantsTd(dto.getWantsTd() != null ? dto.getWantsTd() : 0);
        existingChoice.setWantsTp(dto.getWantsTp() != null ? dto.getWantsTp() : 0);
        existingChoice.setTargetSemester(dto.getTargetSemester());
    }


    private void validateChoiceAgainstModule(FicheChoiceDto choiceDto) {
        if (choiceDto.getModuleId() == null) {
            throw new IllegalArgumentException("Module ID is required.");
        }
        if (choiceDto.getTargetSemester() == null ||
                (!choiceDto.getTargetSemester().equals("S1") && !choiceDto.getTargetSemester().equals("S2"))) {
            throw new IllegalArgumentException("Target semester (S1 or S2) is required for FicheChoice.");
        }

        Module module = moduleRepository.findById(choiceDto.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + choiceDto.getModuleId()));

        int expectedModuleSemester = choiceDto.getTargetSemester().equals("S1") ? 1 : 2;
        if (module.getSemester() != expectedModuleSemester) {
            throw new OperationNotAllowedException(
                    "Module '" + module.getModuleName() + "' is for semester " + module.getSemester() +
                            " and cannot be chosen for the target semester " + choiceDto.getTargetSemester() +
                            " (which implies the module should be of semester " + expectedModuleSemester + ")."
            );
        }

        if (choiceDto.getWantsTd() != null && choiceDto.getWantsTd() > 0 && !module.isHasTd()) {
            throw new OperationNotAllowedException(
                    "Module '" + module.getModuleName() + "' (ID: " + module.getId() + ") does not offer TD. Cannot request TD."
            );
        }
        if (choiceDto.getWantsTp() != null && choiceDto.getWantsTp() > 0 && !module.isHasTp()) {
            throw new OperationNotAllowedException(
                    "Module '" + module.getModuleName() + "' (ID: " + module.getId() + ") does not offer TP. Cannot request TP."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<FicheChoiceDto> getAllFichesChoices() {
        logger.debug("Request to get all FicheChoices");
        return ficheChoiceRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FicheChoiceDto> getFichesChoicesByFicheId(Long ficheId) {
        logger.debug("Request to get FicheChoices for ficheId: {}", ficheId);
        return ficheChoiceRepository.findByFicheId(ficheId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FicheChoiceDto getFicheChoiceById(Long id) {
        logger.debug("Request to get FicheChoice with ID: {}", id);
        FicheChoice choice = ficheChoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FicheChoice not found with id " + id));
        return mapToDto(choice);
    }

    @Transactional
    public FicheChoiceDto addFicheChoice(FicheChoiceDto ficheChoiceDto) {
        if (ficheChoiceDto.getFicheId() == null) {
            throw new IllegalArgumentException("Fiche ID is required to add a FicheChoice.");
        }
        validateChoiceAgainstModule(ficheChoiceDto);

        logger.info("Request to add new FicheChoice for ficheId: {}, moduleId: {}, semester: {}",
                ficheChoiceDto.getFicheId(), ficheChoiceDto.getModuleId(), ficheChoiceDto.getTargetSemester());
        try {
            FicheChoice choiceToSave = mapToEntity(ficheChoiceDto);
            FicheChoice savedChoice = ficheChoiceRepository.save(choiceToSave);
            logger.info("Successfully added FicheChoice with ID: {}", savedChoice.getId());
            return mapToDto(savedChoice);
        } catch (Exception e) {
            logger.error("Error adding FicheChoice: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save FicheChoice", e);
        }
    }

    @Transactional
    public void deleteFicheChoice(long id) {
        logger.warn("Request to delete FicheChoice with ID: {}", id);
        if (!ficheChoiceRepository.existsById(id)) {
            logger.error("Attempted to delete non-existent FicheChoice with id: {}", id);
            throw new ResourceNotFoundException("FicheChoice not found with id " + id);
        }
        try {
            ficheChoiceRepository.deleteById(id);
            logger.info("Successfully deleted FicheChoice with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting FicheChoice with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not delete FicheChoice with id " + id, e);
        }
    }

    @Transactional
    public FicheChoiceDto updateFicheChoice(long id, FicheChoiceDto ficheChoiceDto) {
        FicheChoice existingChoice = ficheChoiceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Update failed: FicheChoice not found with id {}", id);
                    return new ResourceNotFoundException("FicheChoice not found with id " + id);
                });

        validateChoiceAgainstModule(ficheChoiceDto);

        logger.info("Request to update FicheChoice with ID: {}", id);
        try {
            updateEntityFromDto(existingChoice, ficheChoiceDto);

            FicheChoice updatedChoice = ficheChoiceRepository.save(existingChoice);
            logger.info("Successfully updated FicheChoice with ID: {}", updatedChoice.getId());
            return mapToDto(updatedChoice);
        } catch (Exception e) {
            logger.error("Error updating FicheChoice with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not update FicheChoice with id " + id, e);
        }
    }
}