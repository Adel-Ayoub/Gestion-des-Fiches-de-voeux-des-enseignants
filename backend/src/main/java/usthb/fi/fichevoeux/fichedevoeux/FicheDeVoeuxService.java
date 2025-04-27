package usthb.fi.fichevoeux.fichedevoeux;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import usthb.fi.fichevoeux.module.ModuleRepository;
import usthb.fi.fichevoeux.Exception.ResourceNotFoundException;



@Service
public class FicheDeVoeuxService {
    private final FicheDeVoeuxRepository ficheDeVoeuxRepository ;


    @Autowired
    public FicheDeVoeuxService(FicheDeVoeuxRepository ficheDeVoeuxRepository) {
        this.ficheDeVoeuxRepository = ficheDeVoeuxRepository ;
    }


    public List<FicheDeVoeux> getFicheDeVoeux() {
        return ficheDeVoeuxRepository.findAll() ;
    }

    public void addFicheDeVoeux(FicheDeVoeux ficheDeVoeux) {
        
        try {
            ficheDeVoeuxRepository.saveAndFlush(ficheDeVoeux) ;
            
        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("done ");
    }


    public void deleteFicheDeVoeux(long id) {
        
        
        Optional<FicheDeVoeux> ficheDeVoeux = ficheDeVoeuxRepository.findById(id) ;
        
        if (ficheDeVoeux.isPresent()) {
            
            ficheDeVoeuxRepository.deleteById(id);
        } else {
            System.out.println("Fiche Voeux with id : "+id+ " doesnt exist");
        }
    }


    public void updateFicheDeVoeux(long id, FicheDeVoeux newFicheDeVoeux) {
        FicheDeVoeux oldFicheDeVoeux = ficheDeVoeuxRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("FicheDeVoeux not found with id " + id)) ;

        oldFicheDeVoeux.setAcademicYear(newFicheDeVoeux.getAcademicYear());
        oldFicheDeVoeux.setComments(newFicheDeVoeux.getComments());
        oldFicheDeVoeux.setProposedPfe(newFicheDeVoeux.getProposedPfe());
        oldFicheDeVoeux.setSemester(newFicheDeVoeux.getSemester());
        oldFicheDeVoeux.setTeacherId(newFicheDeVoeux.getTeacherId());
        oldFicheDeVoeux.setWantsSupplementaryHoursS1(newFicheDeVoeux.getWantsSupplementaryHoursS1());
        oldFicheDeVoeux.setWantsSupplementaryHoursS2(newFicheDeVoeux.getWantsSupplementaryHoursS2());

        ficheDeVoeuxRepository.save(oldFicheDeVoeux) ;
    }

    
}
