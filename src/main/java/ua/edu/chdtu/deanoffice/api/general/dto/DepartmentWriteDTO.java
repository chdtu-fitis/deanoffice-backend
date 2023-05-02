package ua.edu.chdtu.deanoffice.api.general.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DepartmentWriteDTO {
    @NotNull(message = "Обов'язково повинна бути назва")
    @Size(min = 5, message = "Назва занадто коротка")
    private String name;
    @AssertTrue(message = "Не можна змінювати не чинну кафедру")
    private boolean active;
    @NotNull(message = "Обов'язково повинна бути скорочена назва")
    private String abbr;
}
