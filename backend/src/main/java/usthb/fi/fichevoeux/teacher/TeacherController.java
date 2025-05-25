package usthb.fi.fichevoeux.teacher;

import java.net.URI;
import java.util.List;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import usthb.fi.fichevoeux.teacher.dto.TeacherDto;
import usthb.fi.fichevoeux.exception.OperationNotAllowedException;
import usthb.fi.fichevoeux.fichedevoeux.FicheDeVoeux;
import usthb.fi.fichevoeux.fichedevoeux.FicheDeVoeuxService;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheDeVoeuxDto;
import java.util.stream.Collectors;

import java.util.Optional;
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final FicheDeVoeuxService ficheDeVoeuxService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        List<TeacherDto> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

@GetMapping("/teacher/submitted-fiches/{id}")
@PreAuthorize("hasRole('TEACHER')")
public ResponseEntity<List<FicheDeVoeuxDto>> getTeacherSubmittedFiches(@PathVariable Long id,Authentication authentication) {
        TeacherDto teacherdto = teacherService.getTeacherById(id);
        Teacher teacher = teacherService.mapToEntity(teacherdto);
    List<FicheDeVoeux> submittedFiches = ficheDeVoeuxService.findSubmittedFichesByTeacherId(teacher.getId());
    List<FicheDeVoeuxDto> ficheDtos = submittedFiches.stream()
        .map(p -> {return ficheDeVoeuxService.mapToDto(p);})
        .collect(Collectors.toList());
    
    return ResponseEntity.ok(ficheDtos);
}
@GetMapping("/teacher/fiche/{ficheId}")
@PreAuthorize("hasRole('TEACHER')")
public ResponseEntity<FicheDeVoeuxDto> getTeacherFicheById(
        @PathVariable Long ficheId,
        Authentication authentication) {
    
    FicheDeVoeux fiche = ficheDeVoeuxService.findById(ficheId);
    
    // Verify the fiche belongs to the teacher
    
    FicheDeVoeuxDto ficheDto = ficheDeVoeuxService.mapToDto(fiche);
    return ResponseEntity.ok(ficheDto);
}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<TeacherDto> getTeacherById(@PathVariable Long id) {
        TeacherDto teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<TeacherDto> getTeacherByUserId(@PathVariable Long userId) {
        TeacherDto teacher = teacherService.getTeacherByUserId(userId);
        return ResponseEntity.ok(teacher);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto createdTeacher = teacherService.addTeacher(teacherDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTeacher.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTeacher);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<TeacherDto> updateTeacher(@PathVariable long id, @Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto updatedTeacher = teacherService.updateTeacher(id , teacherDto);
        return ResponseEntity.ok(updatedTeacher);
    }
}
