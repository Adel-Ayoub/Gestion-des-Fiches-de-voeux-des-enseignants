package usthb.fi.fichevoeux.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByuserId(Long userID);
    List<Teacher> findBydepartmentName(String department); // balak nzidou
}

