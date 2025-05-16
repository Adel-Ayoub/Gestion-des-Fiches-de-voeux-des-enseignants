package usthb.fi.fichevoeux.teacher;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.teacher.dto.TeacherDto;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

    private final TeacherRepository teacherRepository;

    private TeacherDto mapToDto(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        return new TeacherDto(
                teacher.getId(),
                teacher.getUserId(),
                teacher.getGrade(),
                teacher.getOfficeNumber(),
                teacher.getDepartmentName()
        );
    }

    private Teacher mapToEntity(TeacherDto dto) {
        if (dto == null) {
            return null;
        }
        Teacher teacher = new Teacher();
        teacher.setUserId(dto.getUserId());
        teacher.setGrade(dto.getGrade());
        teacher.setOfficeNumber(dto.getOfficeNumber());
        teacher.setDepartmentName(dto.getDepartmentName());
        return teacher;
    }

    private void updateEntityFromDto(Teacher existingTeacher, TeacherDto dto) {
        existingTeacher.setGrade(dto.getGrade());
        existingTeacher.setOfficeNumber(dto.getOfficeNumber());
        existingTeacher.setDepartmentName(dto.getDepartmentName());
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> getAllTeachers() {
        logger.debug("Request to get all Teachers");
        return teacherRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherDto getTeacherById(Long id) {
        logger.debug("Request to get Teacher with ID: {}", id);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id " + id));
        return mapToDto(teacher);
    }

    @Transactional(readOnly = true)
    public TeacherDto getTeacherByUserId(Long userId) {
        logger.debug("Request to get Teacher by userId: {}", userId);
        Teacher teacher = teacherRepository.findByuserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found for user id " + userId));
        return mapToDto(teacher);
    }


    @Transactional
    public TeacherDto addTeacher(TeacherDto teacherDto) {
        logger.info("Request to add new Teacher profile for userId: {}", teacherDto.getUserId());
        try {
            Teacher teacherToSave = mapToEntity(teacherDto);
            Teacher savedTeacher = teacherRepository.save(teacherToSave);
            logger.info("Successfully added Teacher profile with ID: {}", savedTeacher.getId());
            return mapToDto(savedTeacher);
        } catch (Exception e) {
            logger.error("Error adding Teacher profile: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save Teacher profile", e);
        }
    }

    @Transactional
    public void deleteTeacher(long id) {
        logger.warn("Request to delete Teacher with ID: {}", id);
        if (!teacherRepository.existsById(id)) {
            logger.error("Attempted to delete non-existent Teacher with id: {}", id);
            throw new ResourceNotFoundException("Teacher not found with id " + id);
        }
        try {
            teacherRepository.deleteById(id);
            logger.info("Successfully deleted Teacher with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting Teacher with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not delete Teacher with id " + id, e);
        }
    }

    @Transactional
    public TeacherDto updateTeacher(long id , TeacherDto teacherDto) {
        logger.info("Request to update Teacher with ID: {}", id);
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Update failed: Teacher not found with id {}", id);
                    return new ResourceNotFoundException("Teacher not found with id " + id);
                });

        try {
            updateEntityFromDto(existingTeacher, teacherDto);
            Teacher updatedTeacher = teacherRepository.save(existingTeacher);
            logger.info("Successfully updated Teacher with ID: {}", updatedTeacher.getId());
            return mapToDto(updatedTeacher);
        } catch (Exception e) {
            logger.error("Error updating Teacher with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not update Teacher with id " + id, e);
        }
    }
}