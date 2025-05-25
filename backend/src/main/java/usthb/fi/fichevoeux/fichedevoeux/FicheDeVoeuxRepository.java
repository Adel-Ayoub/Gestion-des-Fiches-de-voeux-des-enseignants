package usthb.fi.fichevoeux.fichedevoeux;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usthb.fi.fichevoeux.teacher.Teacher;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FicheDeVoeuxRepository extends JpaRepository<FicheDeVoeux, Long> {

    //Optional<FicheDeVoeux> findByTeacherIdAndAcademicYearAndSemester(Long teacherId, String academicYear, String semester);

    Optional<FicheDeVoeux> findByTeacherIdAndAcademicYear(Long teacherId, String academicYear);

    List<FicheDeVoeux> findByTeacherId(Long teacherId); 
    // will try to find another way of doing these operations
    /*@Query("SELECT DISTINCT fv.teacherId FROM FicheDeVoeux fv JOIN FicheChoice fc ON fv.id = fc.ficheId WHERE fc.moduleId = :moduleId AND fv.academicYear = :year AND fv.semester = :semester")
    Set<Long> findTeacherIdsWhoChoseModule(@Param("moduleId") Long moduleId, @Param("year") String academicYear, @Param("semester") String semester);*/
}
