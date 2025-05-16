package usthb.fi.fichevoeux.fichedevoeux;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheDeVoeuxDto;


@RestController
@RequestMapping("/api/fiches-de-voeux")
@RequiredArgsConstructor
public class FicheDeVoeuxController {

    private final FicheDeVoeuxService ficheDeVoeuxService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FicheDeVoeuxDto>> getAllFichesDeVoeux() {
        List<FicheDeVoeuxDto> fiches = ficheDeVoeuxService.getAllFichesDeVoeux();
        return ResponseEntity.ok(fiches);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<FicheDeVoeuxDto> getFicheDeVoeuxById(@PathVariable Long id) {
        FicheDeVoeuxDto ficheDto = ficheDeVoeuxService.getFicheDeVoeuxById(id);
        return ResponseEntity.ok(ficheDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<FicheDeVoeuxDto> createFicheDeVoeux(@Valid @RequestBody FicheDeVoeuxDto ficheDto) {
        FicheDeVoeuxDto createdFiche = ficheDeVoeuxService.addFicheDeVoeux(ficheDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFiche.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdFiche);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteFicheDeVoeux(@PathVariable long id) {
        ficheDeVoeuxService.deleteFicheDeVoeux(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<FicheDeVoeuxDto> updateFicheDeVoeux(
            @PathVariable long id,
            @Valid @RequestBody FicheDeVoeuxDto ficheDto
    ) {
        FicheDeVoeuxDto updatedFiche = ficheDeVoeuxService.updateFicheDeVoeux(id, ficheDto);
        return ResponseEntity.ok(updatedFiche);
    }
}