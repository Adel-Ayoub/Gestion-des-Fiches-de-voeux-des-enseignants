package usthb.fi.fichevoeux.teacher;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TEACHER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEACHER_ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private Long userId;

    @Column(name = "GRADE")
    private String grade;

    @Column(name = "OFFICE_NUMBER")
    private String officeNumber;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
}
