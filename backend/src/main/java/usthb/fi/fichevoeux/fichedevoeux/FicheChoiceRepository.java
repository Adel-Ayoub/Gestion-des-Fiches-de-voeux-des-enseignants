package usthb.fi.fichevoeux.fichedevoeux;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FicheChoiceRepository extends JpaRepository<FicheChoice, Long> {
    /*List<FicheChoice> findByficheId(Long ficheID);

    List<FicheChoice> findBymoduleId(Long moduleID);*/

    List<FicheChoice> findByFicheId(Long ficheId);
    List<FicheChoice> findByModuleId(Long moduleId);

    //List<FicheChoice> findByModuleId_Year_Semester(Long moduleId, String Year, String semester);

    /* @Query("SELECT fc FROM FicheChoice fc JOIN FicheDeVoeux fv ON fc.ficheId = fv.id WHERE fv.academicYear = :year AND fv.semester = :semester")
    List<FicheChoice> findChoicesByPeriod(@Param("year") String academicYear, @Param("semester") String semester);
*/
}
