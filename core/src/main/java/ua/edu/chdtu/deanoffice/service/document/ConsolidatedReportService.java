package ua.edu.chdtu.deanoffice.service.document;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class ConsolidatedReportService {

    public File formConsolidatedReport(Map<Course, List<StudentGroup>> courseToStudentGroups, ApplicationUser user) {
        Document document = new Document(PageSize.A4);

        return null;
    }
}
