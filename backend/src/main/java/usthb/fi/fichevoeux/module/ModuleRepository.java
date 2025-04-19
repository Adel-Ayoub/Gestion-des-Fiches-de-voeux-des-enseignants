package usthb.fi.fichevoeux.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByModuleName(String moduleName);
    List<Module> findByLevel(String level);
}