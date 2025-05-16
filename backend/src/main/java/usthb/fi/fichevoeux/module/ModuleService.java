package usthb.fi.fichevoeux.module;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.module.dto.ModuleDto;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private static final Logger logger = LoggerFactory.getLogger(ModuleService.class);

    private final ModuleRepository moduleRepository;

    private ModuleDto mapToDto(Module module) {
        if (module == null) {
            return null;
        }
        return new ModuleDto(module);
    }

    private Module mapToEntity(ModuleDto dto) {
        if (dto == null) {
            return null;
        }
        Module module = new Module();
        module.setModuleName(dto.getModuleName());
        module.setLevel(dto.getLevel());
        module.setSemester(dto.getSemester());
        module.setHasTd(dto.isHasTd());
        module.setHasTp(dto.isHasTp());
        return module;
    }

    private void updateEntityFromDto(Module existingModule, ModuleDto dto) {
        existingModule.setModuleName(dto.getModuleName());
        existingModule.setLevel(dto.getLevel());
        existingModule.setSemester(dto.getSemester());
        existingModule.setHasTd(dto.isHasTd());
        existingModule.setHasTp(dto.isHasTp());
    }

    @Transactional(readOnly = true)
    public List<ModuleDto> getAllModules() {
        logger.debug("Request to get all Modules (non-paginated)");
        return moduleRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ModuleDto getModuleById(Long id) {
        logger.debug("Request to get Module with ID: {}", id);
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + id));
        return mapToDto(module);
    }

    @Transactional
    public ModuleDto addModule(ModuleDto moduleDto) {
        if (moduleDto.getSemester() < 1) {
            throw new IllegalArgumentException("Module semester number must be a positive integer (e.g., 1, 2, 3...).");
        }
        logger.info("Request to add new Module: Name='{}', Level='{}', SemesterNum='{}', HasTD='{}', HasTP='{}'",
                moduleDto.getModuleName(), moduleDto.getLevel(), moduleDto.getSemester(),
                moduleDto.isHasTd(), moduleDto.isHasTp());
        try {
            Module moduleToSave = mapToEntity(moduleDto);
            Module savedModule = moduleRepository.save(moduleToSave);
            logger.info("Successfully added Module with ID: {}", savedModule.getId());
            return mapToDto(savedModule);
        } catch (Exception e) {
            logger.error("Error adding Module: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save Module: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteModule(long id) {
        logger.warn("Request to delete Module with ID: {}", id);
        if (!moduleRepository.existsById(id)) {
            logger.error("Attempted to delete non-existent Module with id: {}", id);
            throw new ResourceNotFoundException("Module not found with id " + id);
        }
        try {
            moduleRepository.deleteById(id);
            logger.info("Successfully deleted Module with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting Module with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not delete Module with id " + id, e);
        }
    }

    @Transactional
    public ModuleDto updateModule(long id, ModuleDto moduleDto) {
        if (moduleDto.getSemester() < 1) {
            throw new IllegalArgumentException("Module semester number must be a positive integer (e.g., 1, 2, 3...) for update.");
        }
        logger.info("Request to update Module with ID: {}, New Data: Name='{}', Level='{}', SemesterNum='{}', HasTD='{}', HasTP='{}'",
                id, moduleDto.getModuleName(), moduleDto.getLevel(), moduleDto.getSemester(),
                moduleDto.isHasTd(), moduleDto.isHasTp());
        Module existingModule = moduleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Update failed: Module not found with id {}", id);
                    return new ResourceNotFoundException("Module not found with id " + id);
                });
        try {
            updateEntityFromDto(existingModule, moduleDto);
            Module updatedModule = moduleRepository.save(existingModule);
            logger.info("Successfully updated Module with ID: {}", updatedModule.getId());
            return mapToDto(updatedModule);
        } catch (Exception e) {
            logger.error("Error updating Module with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not update Module with id " + id, e);
        }
    }

    @Transactional(readOnly = true)
    public List<ModuleDto> getModulesBySemesterNumber(int semesterNumber) {
        if (semesterNumber < 1) {
            throw new IllegalArgumentException("Semester number must be a positive integer.");
        }
        logger.debug("Request to get modules for semester number: {} (non-paginated)", semesterNumber);
        return moduleRepository.findBySemester(semesterNumber).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModuleDto> getModulesByLevelAndSemesterNumber(String level, int semesterNumber) {
        if (semesterNumber < 1) {
            throw new IllegalArgumentException("Semester number must be a positive integer.");
        }
        logger.debug("Request to get modules for level: {}, semester number: {} (non-paginated)", level, semesterNumber);
        return moduleRepository.findByLevelAndSemester(level, semesterNumber).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}