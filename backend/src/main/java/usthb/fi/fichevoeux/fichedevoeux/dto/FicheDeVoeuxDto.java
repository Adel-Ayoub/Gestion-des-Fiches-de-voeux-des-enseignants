package usthb.fi.fichevoeux.fichedevoeux.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usthb.fi.fichevoeux.fichedevoeux.FicheDeVoeux;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FicheDeVoeuxDto {

    private Long id;
    private Long teacherId;
    private String academicYear;
    private Integer wantsSupplementaryHoursS1;
    private Integer wantsSupplementaryHoursS2;
    private Integer proposedPfe;
    private String comments;

    public FicheDeVoeuxDto(FicheDeVoeux fiche) {
        this.id = fiche.getId();
        this.teacherId = fiche.getTeacherId();
        this.academicYear = fiche.getAcademicYear();
        this.wantsSupplementaryHoursS1 = fiche.getWantsSupplementaryHoursS1();
        this.wantsSupplementaryHoursS2 = fiche.getWantsSupplementaryHoursS2();
        this.proposedPfe = fiche.getProposedPfe();
        this.comments = fiche.getComments();
    }
}