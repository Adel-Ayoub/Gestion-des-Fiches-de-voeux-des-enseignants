package usthb.fi.fichevoeux.fichedevoeux;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FicheChoiceRepository extends JpaRepository<FicheChoice, Long> {
    List<FicheChoice> findByficheId(Long ficheID);

    List<FicheChoice> findBymoduleId(Long moduleID);

    //List<FicheChoice> findByModuleId_Year_Semester(Long moduleId, String Year, String semester);

}
