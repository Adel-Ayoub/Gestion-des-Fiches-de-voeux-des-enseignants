package usthb.fi.fichevoeux.fichedevoeux;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheChoiceDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FicheChoiceController {

    private final FicheChoiceService ficheChoiceService;

    @GetMapping("/ficheChoice")
    public ResponseEntity<List<FicheChoiceDto>> getAllFichesChoices() {
        List<FicheChoiceDto> choices = ficheChoiceService.getAllFichesChoices();
        return ResponseEntity.ok(choices);
    }

    @GetMapping("/ficheChoice/{id}")
    public ResponseEntity<FicheChoiceDto> getFicheChoiceById(@PathVariable("id") Long id) {
        FicheChoiceDto choiceDto = ficheChoiceService.getFicheChoiceById(id);
        return ResponseEntity.ok(choiceDto);
    }

    @GetMapping("/ficheChoice/by-fiche/{ficheId}")
    public ResponseEntity<List<FicheChoiceDto>> getFichesChoicesByFicheId(@PathVariable("ficheId") Long ficheId) {
        List<FicheChoiceDto> choices = ficheChoiceService.getFichesChoicesByFicheId(ficheId);
        return ResponseEntity.ok(choices);
    }

    @PostMapping("/ficheChoice")
    public ResponseEntity<FicheChoiceDto> addFicheChoice(@Valid @RequestBody FicheChoiceDto ficheChoiceDto) {
        FicheChoiceDto savedChoice = ficheChoiceService.addFicheChoice(ficheChoiceDto);
        return new ResponseEntity<>(savedChoice, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/ficheChoice/{id}")
    public ResponseEntity<Void> deleteFicheChoice(@PathVariable("id") long id) {
        ficheChoiceService.deleteFicheChoice(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ficheChoice/{id}")
    public ResponseEntity<FicheChoiceDto> updateFicheChoice(@PathVariable long id, @Valid @RequestBody FicheChoiceDto ficheChoiceDto) {
        FicheChoiceDto updatedChoice = ficheChoiceService.updateFicheChoice(id, ficheChoiceDto);
        return ResponseEntity.ok(updatedChoice);
    }
}