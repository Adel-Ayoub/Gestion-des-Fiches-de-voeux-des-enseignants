package usthb.fi.fichevoeux.fichedevoeux.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FicheSubmissionRequestDto {

    @NotBlank(message = "Academic year cannot be blank")
    private String academicYear;

    private Integer wantsSupplementaryHoursS1;
    private Integer wantsSupplementaryHoursS2;
    private Integer proposedPfe;
    private String comments;

    @NotNull(message = "Semester 1 choices must be provided.")
    @Size(min = 3, max = 3, message = "Exactly 3 choices must be submitted for Semester 1.")
    @Valid
    private List<FicheChoiceDto> semester1Choices;

    @NotNull(message = "Semester 2 choices must be provided.")
    @Size(min = 3, max = 3, message = "Exactly 3 choices must be submitted for Semester 2.")
    @Valid
    private List<FicheChoiceDto> semester2Choices;
}