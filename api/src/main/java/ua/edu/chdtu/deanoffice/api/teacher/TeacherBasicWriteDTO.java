package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TeacherBasicWriteDTO extends TeacherBasicDTO {
    @NotNull(message = "Обов'язково повинна бути кафедра")
    private int departmentId;
    @NotNull(message = "Обов'язково повинна бути посада")
    private int positionId;
    private int scientificDegreeId;
}
