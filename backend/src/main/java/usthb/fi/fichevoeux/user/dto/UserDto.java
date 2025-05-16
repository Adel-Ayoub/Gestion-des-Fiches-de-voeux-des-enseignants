package usthb.fi.fichevoeux.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usthb.fi.fichevoeux.user.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private long id;
    private String email;
    private String name;
    private Role role;

    public UserDto(usthb.fi.fichevoeux.user.User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
    }
}