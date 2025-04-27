package usthb.fi.fichevoeux.teacher;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
public class TeacherController {
       private final TeacherService teacherService ;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService ;
    }

    @GetMapping("/teacher")
    public List<Teacher> getTeachers() {
        return teacherService.getTeachers() ;
    }


    @PostMapping("/teacher")
    public String registerNewTeacher(@RequestBody Teacher teacher) {
        teacherService.addTeacher(teacher);
        return "Teacher : created successfully ! \n" ;
    }


      @DeleteMapping(path = "/teacher/{teacherId}")
    public String deleteTeacher(@PathVariable("teacherId")  long id) {
        teacherService.deleteteacher(id) ;
        return " deleted successfully !\n" ;
    }

    @PutMapping("teacher/{id}")
    public String updateTeacher(@PathVariable long id, @RequestBody Teacher newTeacher) {
        teacherService.updateTeacher(id , newTeacher) ;

        return "update done";
    }

}
