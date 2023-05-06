package ua.edu.chdtu.deanoffice.service.course;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.util.List;

@Setter
@Getter
public class CoursePaginationBean {
    public CoursePaginationBean(int totalPages, int currentPage, List<Course> items) {
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.items = items;
    }

    private int totalPages;
    private int currentPage;
    private List<Course> items;
}
