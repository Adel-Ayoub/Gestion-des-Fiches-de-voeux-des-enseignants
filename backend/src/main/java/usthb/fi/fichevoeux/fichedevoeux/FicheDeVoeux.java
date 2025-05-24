package usthb.fi.fichevoeux.fichedevoeux;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.temporal.ChronoUnit;
import jakarta.persistence.PrePersist;
import java.time.Instant;


@Entity
@Table(name = "FICHE_DE_VOEUX",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"TEACHER_ID", "ACADEMIC_YEAR"})
})
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

    /*@Column(name = "SEMESTER")
    private String semester;*/

    @Column(name = "WANTS_SUPPLEMENTARY_HOURS_S1")
    private Integer wantsSupplementaryHoursS1;

    @Column(name = "WANTS_SUPPLEMENTARY_HOURS_S2") // kader ndirou wahda
    private Integer wantsSupplementaryHoursS2;

    @Column(name = "PROPOSED_PFEL",columnDefinition = "INT DEFAULT 0")
    private Integer proposedPfeL;

    @Column(name = "PROPOSED_PFEM",columnDefinition = "INT DEFAULT 0")
    private Integer proposedPfeM;

    @Column(name = "COMMENTS", columnDefinition = "TEXT")
    private String comments;
    
    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt= Instant.now();

    @PrePersist
    public void prePersist() {
        if (createdAt == null){
            Instant intt= Instant.now();
            this.createdAt = intt.ofEpochSecond(intt.getEpochSecond());
        }
    }

}
