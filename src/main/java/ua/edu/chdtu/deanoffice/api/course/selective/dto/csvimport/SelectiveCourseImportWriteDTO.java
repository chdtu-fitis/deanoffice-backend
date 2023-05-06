package ua.edu.chdtu.deanoffice.api.course.selective.dto.csvimport;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.TrainingCycle;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
public class SelectiveCourseImportWriteDTO {
    private String teacher;
    @Min(1)
    private int degreeId;
    @NotNull(message = "Кафедра має бути вказаний")
    private String department;
    private String fieldOfKnowledge;
    @NotNull(message = "Цикл має бути вказаний")
    private TrainingCycle trainingCycle;
    private String description;
    @Max(2060)
    @Min(2021)
    private int studyYear;
    @Max(10)
    @Min(1)
    private int semester;
    @NotNull(message = "Назва дисципліни має бути вказана")
    @Size(min = 3, message = "Назва дисципліни занадто коротка")
    private String courseName;
}
