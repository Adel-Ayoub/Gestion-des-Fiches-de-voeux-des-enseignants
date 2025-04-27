package usthb.fi.fichevoeux.teacher;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usthb.fi.fichevoeux.Exception.ResourceNotFoundException;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository ;


    @Autowired
    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository ;
    }

    public List<Teacher> getTeachers() {
        return teacherRepository.findAll();
    }

    public void addTeacher(Teacher teacher) {

        try {
            
            teacherRepository.saveAndFlush(teacher) ;
        } catch (Exception e) {
            System.out.println("error in inserting " + e.getMessage());

        }
        System.out.println("insertion done");
    }

    public void deleteteacher(long id) {
    
        Optional<Teacher> teacher = teacherRepository.findById(id) ;
        
        if (teacher.isPresent()) {
            
            teacherRepository.deleteById(id);
        } else {
            System.out.println("Teacher with id : " +id+" doesnt exist");
        }
    }

    public void updateTeacher(long id , Teacher newTeacher) {
        Teacher oldTeacher = (teacherRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("teacher with id :" + id + " doesnt exist"))) ; // .orElseThrow(()-> new ResourceNotFoundException("teacher with id :" + id + " doesnt exist"))
            
 
            oldTeacher.setDepartmentName(newTeacher.getDepartmentName());
            oldTeacher.setGrade(newTeacher.getGrade());
            oldTeacher.setOfficeNumber(newTeacher.getOfficeNumber());;
            oldTeacher.setUserId(newTeacher.getUserId());
            
            teacherRepository.save(oldTeacher) ;

        
    }
    
}
