package ua.edu.chdtu.deanoffice.api.general.dto.validation;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Min;

@Getter
@Setter
public class NullAllowedExistingIdDTO {
    @Min(value = 1, message = "Неправильно вказаний ідентифікатор, ідентифікатор повинен бути більшим, ніж 0")
    private int id;
}
