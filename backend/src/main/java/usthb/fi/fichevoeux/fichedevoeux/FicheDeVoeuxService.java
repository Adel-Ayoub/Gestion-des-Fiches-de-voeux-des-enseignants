package usthb.fi.fichevoeux.fichedevoeux;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.exception.OperationNotAllowedException;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheDeVoeuxDto;

@Service
@RequiredArgsConstructor
public class FicheDeVoeuxService {

    private static final Logger logger = LoggerFactory.getLogger(FicheDeVoeuxService.class);

    private final FicheDeVoeuxRepository ficheDeVoeuxRepository;
    private final FicheChoiceRepository ficheChoiceRepository;

    private FicheDeVoeuxDto mapToDto(FicheDeVoeux fiche) {
        if (fiche == null) {
            return null;
        }
        return new FicheDeVoeuxDto(
                fiche.getId(),
                fiche.getTeacherId(),
                fiche.getAcademicYear(),
                fiche.getWantsSupplementaryHoursS1(),
                fiche.getWantsSupplementaryHoursS2(),
                fiche.getProposedPfe(),
                fiche.getComments());
    }

    private FicheDeVoeux mapToEntity(FicheDeVoeuxDto dto) {
        if (dto == null) {
            return null;
        }
        FicheDeVoeux fiche = new FicheDeVoeux();
        fiche.setTeacherId(dto.getTeacherId());
        fiche.setAcademicYear(dto.getAcademicYear());
        fiche.setWantsSupplementaryHoursS1(dto.getWantsSupplementaryHoursS1());
        fiche.setWantsSupplementaryHoursS2(dto.getWantsSupplementaryHoursS2());
        fiche.setProposedPfe(dto.getProposedPfe());
        fiche.setComments(dto.getComments());
        return fiche;
    }

    private void updateEntityFromDto(FicheDeVoeux existingFiche, FicheDeVoeuxDto dto) {
        existingFiche.setAcademicYear(dto.getAcademicYear());
        existingFiche.setWantsSupplementaryHoursS1(dto.getWantsSupplementaryHoursS1());
        existingFiche.setWantsSupplementaryHoursS2(dto.getWantsSupplementaryHoursS2());
        existingFiche.setProposedPfe(dto.getProposedPfe());
        existingFiche.setComments(dto.getComments());
    }

    @Transactional(readOnly = true)
    public List<FicheDeVoeuxDto> getAllFichesDeVoeux() {
        logger.debug("Request to get all Fiches de Voeux");
        return ficheDeVoeuxRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FicheDeVoeuxDto getFicheDeVoeuxById(Long id) {
        logger.debug("Request to get Fiche de Voeux with ID: {}", id);
        FicheDeVoeux fiche = ficheDeVoeuxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FicheDeVoeux not found with id " + id));
        return mapToDto(fiche);
    }

    @Transactional
    public FicheDeVoeuxDto addFicheDeVoeux(FicheDeVoeuxDto ficheDto) {
        logger.info("Request to add new Fiche de Voeux for teacherId: {}", ficheDto.getTeacherId());
        if (ficheDto.getTeacherId() == null || ficheDto.getAcademicYear() == null) {
            throw new IllegalArgumentException("Teacher ID and Academic Year are required to create a FicheDeVoeux.");
        }
        ficheDeVoeuxRepository.findByTeacherIdAndAcademicYear(ficheDto.getTeacherId(), ficheDto.getAcademicYear())
                .ifPresent(existing -> {
                    throw new OperationNotAllowedException("A Fiche de Voeux for this teacher and academic year already exists. Use update or yearly submission.");
                });

        try {
            FicheDeVoeux ficheToSave = mapToEntity(ficheDto);
            FicheDeVoeux savedFiche = ficheDeVoeuxRepository.save(ficheToSave);
            logger.info("Successfully added Fiche de Voeux with ID: {}", savedFiche.getId());
            return mapToDto(savedFiche);
        } catch (Exception e) {
            logger.error("Error adding Fiche de Voeux: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save Fiche de Voeux", e);
        }
    }

    @Transactional
    public void deleteFicheDeVoeux(long id) {
        logger.warn("Request to delete Fiche de Voeux with ID: {}", id);
        FicheDeVoeux fiche = ficheDeVoeuxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FicheDeVoeux not found with id " + id));

        List<FicheChoice> choices = ficheChoiceRepository.findByFicheId(id);
        ficheChoiceRepository.deleteAll(choices);
        logger.info("Deleted {} associated choices for FicheDeVoeux ID: {}", choices.size(), id);

        ficheDeVoeuxRepository.delete(fiche);
        logger.info("Successfully deleted Fiche de Voeux with ID: {}", id);
    }

    @Transactional
    public FicheDeVoeuxDto updateFicheDeVoeux(long id, FicheDeVoeuxDto ficheDto) {
        logger.info("Request to update Fiche de Voeux with ID: {}", id);
        FicheDeVoeux existingFiche = ficheDeVoeuxRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Update failed: FicheDeVoeux not found with id {}", id);
                    return new ResourceNotFoundException("FicheDeVoeux not found with id " + id);
                });

        if (!existingFiche.getTeacherId().equals(ficheDto.getTeacherId()) ||
                !existingFiche.getAcademicYear().equals(ficheDto.getAcademicYear())) {
            Optional<FicheDeVoeux> conflict = ficheDeVoeuxRepository
                    .findByTeacherIdAndAcademicYear(ficheDto.getTeacherId(), ficheDto.getAcademicYear());
            if (conflict.isPresent() && !conflict.get().getId().equals(id)) {
                throw new OperationNotAllowedException("Another Fiche de Voeux for " + ficheDto.getTeacherId() + " and " + ficheDto.getAcademicYear() + " already exists.");
            }
        }

        try {
            updateEntityFromDto(existingFiche, ficheDto);
            FicheDeVoeux updatedFiche = ficheDeVoeuxRepository.save(existingFiche);
            logger.info("Successfully updated Fiche de Voeux with ID: {}", updatedFiche.getId());
            return mapToDto(updatedFiche);
        } catch (Exception e) {
            logger.error("Error updating Fiche de Voeux with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not update Fiche de Voeux with id " + id, e);
        }
    }
}