package usthb.fi.fichevoeux.fichedevoeux;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FicheDeVoeuxController {
    private final FicheDeVoeuxService ficheDeVoeuxService ;
    
    public FicheDeVoeuxController(FicheDeVoeuxService ficheDeVoeuxService ) {
        this.ficheDeVoeuxService = ficheDeVoeuxService ;
    }



        @GetMapping("/ficheDeVoeux")
    public List<FicheDeVoeux> getFichiersDeVoeux() {
        return ficheDeVoeuxService.getFicheDeVoeux() ;
    }


    @PostMapping("/ficheDeVoeux")
    public String registerNewficheDeVoeux(@RequestBody FicheDeVoeux ficheDeVoeux) {
        ficheDeVoeuxService.addFicheDeVoeux(ficheDeVoeux);
        return "ficheDeVoeux created successfully ! \n" ;
    }

       @DeleteMapping(path = "/ficheDeVoeux/{ficheDeVoeuxId}")
    public String deleteficheDeVoeux(@PathVariable("ficheDeVoeuxId")  long id) {
        ficheDeVoeuxService.deleteFicheDeVoeux(id) ;
        return "ficheDeVoeux deleted successfully !\n" ;
    }

    @PutMapping("ficheDeVoeux/{id}")
    public String updateficheDeVoeux(@PathVariable long id, @RequestBody FicheDeVoeux newficheDeVoeux) {
        ficheDeVoeuxService.updateFicheDeVoeux(id , newficheDeVoeux) ;

        return "update done";
    }

}
