package usthb.fi.fichevoeux.module;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByModuleName(String moduleName);

    Page<Module> findByModuleName(String moduleName, Pageable pageable);

    List<Module> findByLevel(String level);

    Page<Module> findByLevel(String level, Pageable pageable);

    List<Module> findBySemester(Integer semester);

    Page<Module> findBySemester(Integer semester, Pageable pageable);

    List<Module> findByModuleNameAndSemester(String moduleName, Integer semester);
    Page<Module> findByModuleNameAndSemester(String moduleName, Integer semester, Pageable pageable);

    List<Module> findByLevelAndSemester(String level, Integer semester);
    Page<Module> findByLevelAndSemester(String level, Integer semester, Pageable pageable);

    Optional<Module> findByModuleNameAndLevelAndSemester(String moduleName, String level, Integer semester);

    boolean existsByModuleNameAndLevelAndSemester(String moduleName, String level, Integer semester);

}