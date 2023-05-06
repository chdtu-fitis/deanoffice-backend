package ua.edu.chdtu.deanoffice.api.general.dto.validation;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ExistingIdDTO {
    @NotNull
    @Min(value = 1, message = "Неправильно вказаний ідентифікатор, ідентифікатор повинен бути більшим, ніж 0")
    private int id;
}
