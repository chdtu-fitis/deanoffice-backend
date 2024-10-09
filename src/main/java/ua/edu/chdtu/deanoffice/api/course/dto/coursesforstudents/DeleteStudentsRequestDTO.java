package ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DeleteStudentsRequestDTO {
    private List<Integer> courseIds;
    private List<Integer> studentDegreeIds;
}
