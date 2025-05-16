package usthb.fi.fichevoeux.module;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Semester number cannot be null")
    @Column(name = "SEMESTER", nullable = false)
    private int semester;

    @Column(name = "HAS_TD")
    private boolean hasTd = true;

    @Column(name = "HAS_TP")
    private boolean hasTp = true;
}