package usthb.fi.fichevoeux.fichedevoeux;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.fichedevoeux.dto.FicheSubmissionRequestDto;
import usthb.fi.fichevoeux.user.User;
import usthb.fi.fichevoeux.user.UserRepository;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/fiches-de-voeux/submissions")
@RequiredArgsConstructor
public class FicheDeVoeuxSubmissionController {

    private static final Logger logger = LoggerFactory.getLogger(FicheDeVoeuxSubmissionController.class);

    private final FicheDeVoeuxSubmissionService ficheDeVoeuxSubmissionService;
    private final UserRepository userRepository;

    @PostMapping("/yearly")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> submitOrUpdateYearlyFicheDeVoeux(
            @Valid @RequestBody FicheSubmissionRequestDto submissionRequestDto,
            Authentication authentication) {

        String userEmail = authentication.getName();
        logger.debug("Submission request received for user: {}", userEmail);

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found in database: {}", userEmail);
                    return new ResourceNotFoundException("Authenticated user data not found");
                });
        Long teacherUserIdForFiche = currentUser.getId();

        FicheDeVoeux processedFiche = ficheDeVoeuxSubmissionService.submitFicheDeVoeux(teacherUserIdForFiche, submissionRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/fiches-de-voeux/{id}")
                .buildAndExpand(processedFiche.getId())
                .toUri();

        logger.info("Yearly Fiche de Voeux submitted/updated successfully with ID: {} for teacher User ID: {}", processedFiche.getId(), teacherUserIdForFiche);
        return ResponseEntity.created(location).body(Map.of("message", "Yearly Fiche de Voeux submitted/updated successfully", "ficheId", processedFiche.getId()));
    }
}

