package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import ua.edu.chdtu.deanoffice.api.teacher.TeacherDTO;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SelectiveCourseWriteDTO {

    @AssertTrue(message = "Не можна змінювати не чинну дисципліну")
    private boolean available;
    @NotNull(message = "Обов'язково має бути назва")
    private ExistingIdDTO course;
    @NotNull(message = "Обов'язково має бути ступінь")
    private ExistingIdDTO degree;
    private ExistingIdDTO teacher;
    @NotNull(message = "Обов'язково має бути рік")
    private Integer studyYear;
}
