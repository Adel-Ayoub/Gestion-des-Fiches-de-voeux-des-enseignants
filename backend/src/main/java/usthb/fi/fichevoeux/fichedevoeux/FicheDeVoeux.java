package usthb.fi.fichevoeux.fichedevoeux;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "FICHE_DE_VOEUX")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FicheDeVoeux {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FICHE_ID")
    private Long id;

    @Column(name = "TEACHER_ID", nullable = false)
    private Long teacherId;

    @Column(name = "ACADEMIC_YEAR", nullable = false)
    private String academicYear;

    @Column(name = "SEMESTER", nullable = false)
    private String semester;

    @Column(name = "WANTS_SUPPLEMENTARY_HOURS_S1")
    private Integer wantsSupplementaryHoursS1;

    @Column(name = "WANTS_SUPPLEMENTARY_HOURS_S2") // kader ndirou wahda
    private Integer wantsSupplementaryHoursS2;

    @Column(name = "PROPOSED_PFE")
    private Integer proposedPfe;

    @Column(name = "COMMENTS", columnDefinition = "TEXT")
    private String comments;
}
