package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;

@Getter
@Setter
public class TeacherInsertDTO extends TeacherBasicWriteDTO {
    @Max(value = 0, message = "Неправильно вказаний ідентифікатор, ідентифікатор повинен бути 0!")
    private int id;
}
