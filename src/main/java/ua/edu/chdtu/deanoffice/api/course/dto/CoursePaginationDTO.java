package ua.edu.chdtu.deanoffice.api.course.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CoursePaginationDTO {
    private List<CourseDTO> items;
    private int currentPage;
    private int totalPages;
}
