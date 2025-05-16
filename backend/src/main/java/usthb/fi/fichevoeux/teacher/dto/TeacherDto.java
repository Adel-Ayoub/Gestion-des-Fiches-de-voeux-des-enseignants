package usthb.fi.fichevoeux.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usthb.fi.fichevoeux.teacher.Teacher;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {

    private Long id;
    private Long userId;
    private String grade;
    private String officeNumber;
    private String departmentName;

    public TeacherDto(Teacher teacher) {
        this.id = teacher.getId();
        this.userId = teacher.getUserId();
        this.grade = teacher.getGrade();
        this.officeNumber = teacher.getOfficeNumber();
        this.departmentName = teacher.getDepartmentName();
    }
}