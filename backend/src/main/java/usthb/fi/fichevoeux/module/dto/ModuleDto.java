package usthb.fi.fichevoeux.module.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usthb.fi.fichevoeux.module.Module;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {

    private Long id;
    private String moduleName;
    private String level;
    private int semester;
    private boolean hasTd;
    private boolean hasTp;

    public ModuleDto(Module module) {
        if (module != null) {
            this.id = module.getId();
            this.moduleName = module.getModuleName();
            this.level = module.getLevel();
            this.semester = module.getSemester();
            this.hasTd = module.isHasTd();
            this.hasTp = module.isHasTp();
        }
    }
}