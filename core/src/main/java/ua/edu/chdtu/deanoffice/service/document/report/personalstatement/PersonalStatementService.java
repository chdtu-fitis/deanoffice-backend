package ua.edu.chdtu.deanoffice.service.document.report.personalstatement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class PersonalStatementService {

    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "PersonalStatement.docx";

    @Autowired
    private GradeService gradeService;

    @Autowired
    private StudentDegreeService studentDegreeService;

    @Autowired
    private StudentGroupService studentGroupService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DocumentIOService documentIOService;

    @Autowired
    private CourseForGroupService courseForGroupService;

    public File formDocument(Integer year, List<Integer> groupIds)
            throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        YearGrades yearGrades = new YearGrades(getGradeMap(year, groupIds, SemesterType.FIRST), getGradeMap(year, groupIds, SemesterType.SECOND));
        generateTables(template, yearGrades);
        return documentIOService.saveDocumentToTemp(template, transliterate(generateFileName(groupIds)), FileFormatEnum.DOCX);
    }

    private String generateFileName(List<Integer> groupIds) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PersonalStatement_");
        groupIds.forEach(groupId -> {
            stringBuilder.append(studentGroupService.getById(groupId).getName()).append("_");
        });
        return stringBuilder.toString();
    }

    private Map<StudentDegree, List<Grade>> getGradeMap(Integer year, List<Integer> groupIds, SemesterType semesterType) {
        List<Integer> studentDegreeIds =
                groupIds.stream().map(groupId -> studentDegreeService.getAllByGroupId(groupId)).
                        collect(Collectors.toList()).stream().flatMap(Collection::stream).collect(Collectors.toList())
                        .stream().map(BaseEntity::getId).sorted().collect(Collectors.toList());

        List<Integer> courseIds = groupIds.stream().map(groupId ->
                courseRepository.getByGroupIdAndSemester(groupId,
                        getSemesterByYearForGroup(year, studentGroupService.getById(groupId)) + semesterType.getNumber() - 1))
                .collect(Collectors.toList())
                .stream().flatMap(Collection::stream)
                .collect(Collectors.toList())
                .stream().map(BaseEntity::getId)
                .collect(Collectors.toList());

        if (courseIds.size() > 0) {
            return gradeService.getGradeMapForStudents(studentDegreeIds, courseIds);
        } else return new HashMap<>();
    }


    private void generateTables(WordprocessingMLPackage template, YearGrades yearGrades) {
        Tbl templateTable = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(0);
        List<StudentDegree> studentDegrees = new ArrayList<>(yearGrades.getGradeMapForFirstSemester().keySet());
        studentDegrees.sort(new GroupStudentDegreeComparator());
        for (StudentDegree studentDegree : studentDegrees) {
            Tbl table = XmlUtils.deepCopy(templateTable);
            fillFirstRow(table, studentDegree.getStudent());
            formFirstSemesterInTable(table, yearGrades.getGradeMapForFirstSemester().get(studentDegree));
            formSecondSemesterInTable(table, yearGrades.getGradeMapForFirstSemester().get(studentDegree));
            template.getMainDocumentPart().addObject(table);
        }
        template.getMainDocumentPart().getContent().remove(0);
    }

    private void fillFirstRow(Tbl table, Student student) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        replaceInRow(tableRows.get(0), getStudentDictionary(student));
    }

    private void formFirstSemesterInTable(Tbl table, List<Grade> grades) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Tr rowToCopy = tableRows.get(2);
        int currentIndex = 2;
        for (Grade grade : grades) {
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByGrade(newRow, grade);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        table.getContent().remove(currentIndex);
        table.getContent().remove(2);
        table.getContent().remove(1);
    }

    private void formSecondSemesterInTable(Tbl table, List<Grade> grades) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        int currentIndex = tableRows.size() - 1;
        Tr rowToCopy = tableRows.get(currentIndex);
        fillRowByGrade(tableRows.get(currentIndex - 1), grades.get(0));
        for (Grade grade : grades.subList(0, grades.size() - 2)) {
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByGrade(newRow, grade);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        table.getContent().remove(currentIndex);
    }

    private void fillRowByGrade(Tr row, Grade grade) {
        replaceInRow(row, getGradeDictionary(grade));
    }


    private Map<String, String> getGradeDictionary(Grade grade) {
        Map<String, String> result = new HashMap<>();
        result.put("subj", grade.getCourse().getCourseName().getName());
        result.put("h", resolveHoursField(grade));
        result.put("c", grade.getCourse().getCredits().toString());
        String gradeFieldValue = "";
        if (grade.getGrade() != null) {
            gradeFieldValue = grade.getCourse().getKnowledgeControl().isGraded() ? grade.getGrade().toString() : "зарах";
        }
        result.put("g", gradeFieldValue);
        if (grade.getPoints() != null) {
            result.put("p", grade.getPoints().toString());
        }
        if (grade.getEcts() != null) {
            result.put("e", grade.getEcts().toString());
        }
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(grade.getStudentDegree().getStudentGroup().getId(), grade.getCourse().getId());
        if (courseForGroup.getExamDate() != null) {
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(courseForGroup.getExamDate());
            result.put("d", date);
        }
        return result;
    }

    private String resolveHoursField(Grade grade) {
        String result = "";
        String hours = grade.getCourse().getHours().toString();
        String practiceSignature = "пр";
        switch (grade.getCourse().getKnowledgeControl().getId()) {
            case 1:
                result = hours;
                break;
            case 2:
                result = hours;
                break;
            case 3:
                result = "КР";
                break;
            case 4:
                result = "КП";
                break;
            case 5:
                result = hours;
                break;
            case 6:
                result = hours;
                break;
            case 7:
                result = hours;
                break;
            case 8:
                result = practiceSignature;
                break;
            case 9:
                result = practiceSignature;
                break;

        }
        return result;
    }

    private Map<String, String> getStudentDictionary(Student student) {
        Map<String, String> result = new HashMap<>();
        result.put("stud", student.getFullNameUkr());
        return result;
    }


    private Integer getSemesterByYearForGroup(Integer year, StudentGroup studentGroup) {
        return (year - studentGroup.getCreationYear()) * 2 + 1;
    }

    @Getter
    private enum SemesterType {
        FIRST(1),
        SECOND(2);

        private int number;

        SemesterType(int number) {
            this.number = number;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class YearGrades {
        private Map<StudentDegree, List<Grade>> gradeMapForFirstSemester;
        private Map<StudentDegree, List<Grade>> gradeMapForSecondSemester;
    }
}
