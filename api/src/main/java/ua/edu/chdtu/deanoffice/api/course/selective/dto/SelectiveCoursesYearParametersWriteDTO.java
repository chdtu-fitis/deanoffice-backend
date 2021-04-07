package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SelectiveCoursesYearParametersWriteDTO {
    @NotNull(message = "Обов'язково повинна бути дата початку першого туру")
    private Date firstRoundStartDate;
    @NotNull(message = "Обов'язково повинна бути дата кінця першого туру")
    private Date firstRoundEndDate;
    @NotNull(message = "Обов'язково повинна бути дата кінця другого туру")
    private Date secondRoundEndDate;
    @NotNull(message = "Обов'язково повинна бути вказана мінімальна кількість студентів")
    @Min(value = 1, message = "Мінімальна кількість студентів повинна бути додатним числом")
    private int minStudentsCount;
}
