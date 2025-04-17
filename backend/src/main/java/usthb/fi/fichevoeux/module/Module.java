package usthb.fi.fichevoeux.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MODULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODULE_ID")
    private Long id;

    @Column(name = "MODULE_NAME", nullable = false)
    private String moduleName;

    @Column(name = "LEVEL")
    private String level;

}
