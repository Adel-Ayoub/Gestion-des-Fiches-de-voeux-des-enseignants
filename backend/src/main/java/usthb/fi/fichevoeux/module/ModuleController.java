package usthb.fi.fichevoeux.module;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModuleController {
    private final ModuleService moduleService; 


    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService ;
    }

        @GetMapping("/module")
    public List<Module> getModules() {
        return moduleService.getModules() ;
    }


    @PostMapping("/module")
    public String registerNewModule(@RequestBody Module module) {
        moduleService.addModule(module);
        return "Module created successfully ! \n" ;
    }

       @DeleteMapping(path = "/module/{moduleId}")
    public String deleteModule(@PathVariable("moduleId")  long id) {
        moduleService.deleteModule(id) ;
        return "Module deleted successfully !\n" ;
    }

    @PutMapping("module/{id}")
    public String updateModule(@PathVariable long id, @RequestBody Module newModule) {
        moduleService.updateModule(id , newModule) ;

        return "update done";
    }


}
