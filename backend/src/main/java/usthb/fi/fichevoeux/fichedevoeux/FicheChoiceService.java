package usthb.fi.fichevoeux.fichedevoeux;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import usthb.fi.fichevoeux.Exception.ResourceNotFoundException;


@Service
public class FicheChoiceService {

    private final FicheChoiceRepository ficheChoiceRepository ;

    @Autowired
    public FicheChoiceService(FicheChoiceRepository ficheChoiceRepository ) {
        this.ficheChoiceRepository = ficheChoiceRepository ;
    }

    public List<FicheChoice> getFichesChoices() {
        return ficheChoiceRepository.findAll() ;
    }


    public void addFicheChoice(FicheChoice ficheChoice) {
                try {
            
            ficheChoiceRepository.saveAndFlush(ficheChoice) ;
        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println(e.getMessage());
        }

    } 
    public void deleteFicheChoice(long id ) {
                Optional<FicheChoice> ficheChoice = ficheChoiceRepository.findById(id) ;
        
        if (ficheChoice.isPresent()) {
            
            ficheChoiceRepository.deleteById(id);
        } else {
            System.out.println("Fiche choice with id:"+ id  + " doesnt exist");
        }

    }


    public void updateFicheChoice(long id, FicheChoice newFicheChoice) {
        FicheChoice oldFicheDeVoeux = ficheChoiceRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("FicheChoice not found with id " + id)) ;

         
        
        oldFicheDeVoeux.setGrade(newFicheChoice.getGrade());
        oldFicheDeVoeux.setModuleId(newFicheChoice.getModuleId());
        oldFicheDeVoeux.setWantsCours(newFicheChoice.getWantsCours());
        oldFicheDeVoeux.setWantsTd(newFicheChoice.getWantsTd());
        oldFicheDeVoeux.setWantsTp(newFicheChoice.getWantsTp());


        ficheChoiceRepository.save(oldFicheDeVoeux) ;
    }


}
