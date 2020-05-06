package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.AcademicTitle;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TeacherBasicDTO {
    @NotNull(message = "Обов'язково повинно бути прізвище")
    @Size(min = 2, message = "Прізвище занадто коротке")
    private String surname;
    @NotNull(message = "Обов'язково повинно бути ім'я")
    @Size(min = 2, message = "Ім'я занадто коротке")
    private String name;
    private String patronimic;
    @NotNull(message = "Обов'язково повинна бути стать")
    private Sex sex;
    private boolean active;
    private AcademicTitle academicTitle;
}
