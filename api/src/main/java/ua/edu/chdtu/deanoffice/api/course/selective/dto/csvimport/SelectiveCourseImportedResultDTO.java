package ua.edu.chdtu.deanoffice.api.course.selective.dto.csvimport;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectiveCourseImportedResultDTO {
    private int successfulImports;
    private List<String> importErrorsReport;
}
