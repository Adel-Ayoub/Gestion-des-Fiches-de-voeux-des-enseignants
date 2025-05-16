package usthb.fi.fichevoeux.module;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import usthb.fi.fichevoeux.module.dto.ModuleDto;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ModuleDto>> getAllModules() {
        List<ModuleDto> modules = moduleService.getAllModules();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ModuleDto> getModuleById(@PathVariable Long id) {
        ModuleDto moduleDto = moduleService.getModuleById(id);
        return ResponseEntity.ok(moduleDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleDto> createModule(@Valid @RequestBody ModuleDto moduleDto) {
        ModuleDto createdModule = moduleService.addModule(moduleDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdModule.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdModule);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteModule(@PathVariable long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleDto> updateModule(
            @PathVariable long id,
            @Valid @RequestBody ModuleDto moduleDto) {
        ModuleDto updatedModule = moduleService.updateModule(id, moduleDto);
        return ResponseEntity.ok(updatedModule);
    }

    @GetMapping("/by-semester")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ModuleDto>> getModulesBySemester(
            @RequestParam("semester") int semesterNumber) {
        List<ModuleDto> modules = moduleService.getModulesBySemesterNumber(semesterNumber);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/by-level-semester")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ModuleDto>> getModulesByLevelAndSemester(
            @RequestParam("level") String level,
            @RequestParam("semester") int semesterNumber) {
        List<ModuleDto> modules = moduleService.getModulesByLevelAndSemesterNumber(level, semesterNumber);
        return ResponseEntity.ok(modules);
    }
}