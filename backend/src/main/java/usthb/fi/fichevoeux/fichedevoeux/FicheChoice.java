package usthb.fi.fichevoeux.fichedevoeux;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FICHE_CHOICE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FicheChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHOICE_ID")
    private Long id;

    @Column(name = "FICHE_ID", nullable = false)
    private Long ficheId;

    @Column(name = "MODULE_ID", nullable = false)
    private Long moduleId;

    @Column(name = "RANK", nullable = false)
    private Integer rank;

    @Column(name = "WANTS_COURS")
    private Boolean wantsCours = false;

    @Column(name = "WANTS_TD")
    private Integer wantsTd = 0;

    @Column(name = "WANTS_TP")
    private Integer wantsTp = 0;

    @Column(name = "TARGET_SEMESTER", nullable = false)
    private String targetSemester;
}
