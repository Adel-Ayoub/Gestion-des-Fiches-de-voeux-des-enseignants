package usthb.fi.fichevoeux.fichedevoeux;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class FicheChoiceController {
    private final FicheChoiceService ficheChoiceService ;  
    
    public FicheChoiceController(FicheChoiceService ficheChoiceService) {
        this.ficheChoiceService = ficheChoiceService ;
    }


    @GetMapping("/ficheChoice")
    public List<FicheChoice> getFichesChoices() {
        return ficheChoiceService.getFichesChoices();
    }

    @PostMapping("/ficheChoice")
    public String addFicheChoice(@RequestBody FicheChoice ficheChoice) {
        ficheChoiceService.addFicheChoice(ficheChoice) ;
        return "insertion of choice file done \n";
    }
    

       @DeleteMapping(path = "/ficheChoice/{ficheChoiceId}")
    public String deleteficheChoice(@PathVariable("ficheChoiceId")  long id) {
        ficheChoiceService.deleteFicheChoice(id) ;
        return "ficheDeVoeux deleted successfully !\n" ;
    }

    @PutMapping("ficheChoice/{id}")
    public String updateficheChoice(@PathVariable long id, @RequestBody FicheChoice newficheChoice) {
        ficheChoiceService.updateFicheChoice(id , newficheChoice) ;

        return "update done";
    }

}
