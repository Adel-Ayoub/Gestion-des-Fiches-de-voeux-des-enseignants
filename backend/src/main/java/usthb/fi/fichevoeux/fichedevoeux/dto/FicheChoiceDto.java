package usthb.fi.fichevoeux.fichedevoeux.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usthb.fi.fichevoeux.fichedevoeux.FicheChoice;

@Data
@NoArgsConstructor
public class FicheChoiceDto {
    private Long id;
    private Long ficheId;

    @NotNull(message = "Module ID cannot be null")
    private Long moduleId;

    @NotNull(message = "Rank cannot be null")
    @Min(value = 1, message = "Rank must be at least 1")
    private Integer rank;

    @NotNull(message = "wantsCours value cannot be null")
    private Boolean wantsCours = false;

    @NotNull(message = "wantsTd value cannot be null")
    @Min(value = 0, message = "wantsTd must be at least 0")
    @Max(value = 4, message = "wantsTd must be at most 4")
    private Integer wantsTd = 0;

    @NotNull(message = "wantsTp value cannot be null")
    @Min(value = 0, message = "wantsTp must be at least 0")
    @Max(value = 4, message = "wantsTp must be at most 4")
    private Integer wantsTp = 0;

    private String targetSemester;


    public FicheChoiceDto(FicheChoice choice) {
        if (choice != null) {
            this.id = choice.getId();
            this.ficheId = choice.getFicheId();
            this.moduleId = choice.getModuleId();
            this.rank = choice.getRank();
            this.wantsCours = choice.getWantsCours();
            this.wantsTd = choice.getWantsTd();
            this.wantsTp = choice.getWantsTp();
            this.targetSemester = choice.getTargetSemester();
        }
    }


    public FicheChoiceDto(Long id, Long ficheId, Long moduleId, Integer rank, Boolean wantsCours, Integer wantsTd, Integer wantsTp, String targetSemester) {
        this.id = id;
        this.ficheId = ficheId;
        this.moduleId = moduleId;
        this.rank = rank;
        this.wantsCours = wantsCours;
        this.wantsTd = wantsTd;
        this.wantsTp = wantsTp;
        this.targetSemester = targetSemester;
    }


    public FicheChoiceDto(Long id, Long ficheId, Long moduleId, Integer rank, Boolean wantsCours, Integer wantsTd, Integer wantsTp) {
        this.id = id;
        this.ficheId = ficheId;
        this.moduleId = moduleId;
        this.rank = rank;
        this.wantsCours = wantsCours;
        this.wantsTd = wantsTd;
        this.wantsTp = wantsTp;

    }
}
