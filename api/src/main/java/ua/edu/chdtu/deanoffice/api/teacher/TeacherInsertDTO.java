package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.NullAllowedExistingIdDTO;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TeacherInsertDTO extends TeacherBasicDTO {
    @Max(value = 0, message = "Неправильно вказаний ідентифікатор, ідентифікатор повинен бути 0")
    private int id;
    @Valid
    @NotNull(message = "Обов'язково повинна бути кафедра")
    private ExistingIdDTO department;
    @Valid
    @NotNull(message = "Обов'язково повинна бути посада")
    private ExistingIdDTO position;
    @Valid
    private NullAllowedExistingIdDTO scientificDegree;
}
