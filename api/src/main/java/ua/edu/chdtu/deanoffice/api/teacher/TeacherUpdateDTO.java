package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.NullAllowedExistingIdDTO;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TeacherUpdateDTO extends TeacherBasicDTO {
    @Min(value = 1, message = "Неправильно вказаний ідентифікатор, ідентифікатор повинен бути більшим, ніж 0")
    private int id;
    @NotNull(message = "Обов'язково повинна бути кафедра")
    private ExistingIdDTO department;
    @NotNull(message = "Обов'язково повинна бути посада")
    private ExistingIdDTO position;
    private NullAllowedExistingIdDTO scientificDegree;
}
