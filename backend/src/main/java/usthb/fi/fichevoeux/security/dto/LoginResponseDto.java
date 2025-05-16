package usthb.fi.fichevoeux.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";

    public LoginResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}