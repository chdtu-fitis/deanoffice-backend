package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SelectiveCoursesYearParametersWriteDTO {
    @NotNull(message = "Обов'язково повинна бути дата початку першого туру")
    private Date firstRoundStartDate;
    @NotNull(message = "Обов'язково повинна бути дата кінця першого туру")
    private Date firstRoundEndDate;
    @NotNull(message = "Обов'язково повинна бути дата початку другого туру")
    private Date secondRoundStartDate;
    @NotNull(message = "Обов'язково повинна бути дата кінця другого туру")
    private Date secondRoundEndDate;
    @Min(value = 1, message = "Мінімальна кількість бакалаврів на цикл загальної підготовки повинна бути додатним числом")
    private int bachelorGeneralMinStudentsCount;
    @Min(value = 1, message = "Мінімальна кількість бакалаврів на предмети циклу професійної підготовки повинна бути додатним числом")
    private int bachelorProfessionalMinStudentsCount;
    @Min(value = 1, message = "Мінімальна кількість магістрів на предмети циклу загальної підготовки повинна бути додатним числом")
    private int masterGeneralMinStudentsCount;
    @Min(value = 1, message = "Мінімальна кількість магістрів на предмети циклу професійної підготовки повинна бути додатним числом")
    private int masterProfessionalMinStudentsCount;
    @Min(value = 1, message = "Мінімальна кількість докторів філософії на предмети циклу загальної підготовки повинна бути додатним числом")
    private int phdGeneralMinStudentsCount;
    @Min(value = 1, message = "Мінімальна кількість докторів філософії на предмети циклу професійної підготовки повинна бути додатним числом")
    private int phdProfessionalMinStudentsCount;
    @Min(value = 1, message = "Максимальна кількість студентів повинна бути додатним числом")
    private int maxStudentsCount;
}
