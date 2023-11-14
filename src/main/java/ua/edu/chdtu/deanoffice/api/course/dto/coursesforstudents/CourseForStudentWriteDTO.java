package ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.CourseType;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CourseForStudentWriteDTO {
    @NotNull(message = "Обов'язково повинно бути задано предмет")
    private Integer courseId;
    private Integer teacherId;
    @NotNull(message = "Обов'язково повинно бути задано тип предмету")
    private CourseType courseType;
}
