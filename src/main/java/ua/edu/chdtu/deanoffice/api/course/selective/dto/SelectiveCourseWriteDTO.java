package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import ua.edu.chdtu.deanoffice.api.teacher.TeacherDTO;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    @NotNull(message = "Обов'язково має бути кафедра")
    private ExistingIdDTO department;
    private List<Integer> fieldsOfKnowledge;
    @NotNull(message = "Обов'язково має бути цикл підготовки")
    private String trainingCycle;
    @NotNull(message = "Обов'язково має бути опис")
    private String description;
    @NotNull(message = "Обов'язково має бути рік")
    private Integer studyYear;
    private String groupName;
}
