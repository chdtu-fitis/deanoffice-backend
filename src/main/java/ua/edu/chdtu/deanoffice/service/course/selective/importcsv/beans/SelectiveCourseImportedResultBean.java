package ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectiveCourseImportedResultBean {
    private int successfulImports;
    private List<String> importErrorsReport;
}
