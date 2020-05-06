package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Min;

@Getter
@Setter
public class TeacherUpdateDTO extends TeacherBasicWriteDTO {
    @Min(value = 1, message = "Неправильно вказаний ідентифікатор, ідентифікатор повинен бути більшим, ніж 0")
    private int id;
}
