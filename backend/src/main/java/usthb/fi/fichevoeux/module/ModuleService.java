package usthb.fi.fichevoeux.module;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import usthb.fi.fichevoeux.Exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.user.UserRepository;




@Service
public class ModuleService {
    private final ModuleRepository moduleRepository ;


    @Autowired
    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository ;
    }


    public List<Module> getModules() {
        return moduleRepository.findAll() ;
    }

    public void addModule(Module module) {
        
        try {
            
            moduleRepository.saveAndFlush(module) ;
        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("done ");
    }


    public void deleteModule(long id) {
        
        
        Optional<Module> module = moduleRepository.findById(id) ;
        
        if (module.isPresent()) {
            
            moduleRepository.deleteById(id);
        } else {
            System.out.println("Module with id : "+id+" doesnt exist");
        }
    }


    public void updateModule(long id, Module newModule) {
        Module oldModule = moduleRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Module not found with id " + id)) ;

        oldModule.setLevel(newModule.getLevel());
        oldModule.setModuleName(newModule.getModuleName());


        moduleRepository.save(oldModule) ;
    }
    
}
