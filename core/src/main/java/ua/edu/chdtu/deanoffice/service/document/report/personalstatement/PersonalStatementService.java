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
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.util.GradeUtil;

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
    private static final int NUMBER_OF_MANDATORY_ROWS_IN_FIRST_SEMESTER_TABLE = 10;
    private static final int NUMBER_OF_MANDATORY_ROWS_IN_TABLE = 20;
    @Autowired
    private StudentDegreeRepository studentDegreeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private DocumentIOService documentIOService;
    @Autowired
    private CourseForGroupService courseForGroupService;

    public File formDocument(Integer year, List<Integer> studentDegreeIds)
            throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        List<StudentDegree> studentDegrees = studentDegreeRepository.getAllByIds(studentDegreeIds);
        studentDegrees = studentDegrees.stream().filter(sd -> getStudentStudyYear(sd, year) >= 0).collect(Collectors.toList());
        YearGrades yearGrades = new YearGrades(getGradeMap(year, studentDegrees, SemesterType.FIRST), getGradeMap(year, studentDegrees, SemesterType.SECOND));
        generateTables(template, yearGrades, year);
        Set<StudentGroup> groups = yearGrades.getGradeMapForFirstSemester().keySet().stream().map(sd -> sd.getStudentGroup()).collect(Collectors.toSet());
        return documentIOService.saveDocumentToTemp(template, transliterate(generateFileName(groups)), FileFormatEnum.DOCX);
    }

    private String generateFileName(Set<StudentGroup> groups) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PersonalFile_");
        groups.forEach(groupId -> {
            stringBuilder.append(groupId.getName()).append("_");
        });
        return stringBuilder.toString();
    }

    private Map<StudentDegree, List<Grade>> getGradeMap(Integer year, List<StudentDegree> studentDegrees, SemesterType semesterType) {
        Map<StudentGroup, List<StudentDegree>> groupsWithStudents = studentDegrees.stream().collect(Collectors.groupingBy(sd -> sd.getStudentGroup()));
        Map<StudentGroup, List<Integer>> groupsWithStudentIds = groupsWithStudents.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().stream().map(BaseEntity::getId).collect(Collectors.toList())
        ));
        Set<StudentGroup> groups = groupsWithStudents.keySet();
        Map<StudentGroup, List<Integer>> courseIdsForGroups = groups.stream().collect(Collectors.toMap(
                group -> group,
                group -> courseRepository.getByGroupIdAndSemester(group.getId(),getSemesterByYearForGroup(year, group) + semesterType.getNumber() - 1)
                        .stream()
                        .map(BaseEntity::getId)
                        .collect(Collectors.toList())
        ));
        return gradeService.getGradeMapForStudents(groupsWithStudentIds, courseIdsForGroups);
    }

    private void generateTables(WordprocessingMLPackage template, YearGrades yearGrades, Integer year) {
        Tbl templateTable = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(0);
        List<StudentDegree> studentDegrees = new ArrayList<>(yearGrades.getGradeMapForFirstSemester().keySet());
        studentDegrees.sort(new GroupStudentDegreeComparator());
        for (StudentDegree studentDegree : studentDegrees) {
            Tbl table = XmlUtils.deepCopy(templateTable);
            fillFirstRow(table, studentDegree.getStudent());
            formSemesterInTable(table, yearGrades.getGradeMapForFirstSemester().get(studentDegree), year, SemesterType.FIRST, studentDegree);
            formSemesterInTable(table, yearGrades.getGradeMapForSecondSemester().get(studentDegree), year, SemesterType.SECOND, studentDegree);
            fillLastRow(table, studentDegree, year);
            template.getMainDocumentPart().addObject(table);
        }
        template.getMainDocumentPart().getContent().remove(0);
    }

    private void fillFirstRow(Tbl table, Student student) {
            List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
            replaceInRow(tableRows.get(0), getStudentDictionary(student));
    }

    private void formSemesterInTable(Tbl table, List<Grade> grades, Integer year, SemesterType semesterType, StudentDegree studentDegree) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        int currentIndex = 2, rowNumber = NUMBER_OF_MANDATORY_ROWS_IN_FIRST_SEMESTER_TABLE;
        if(semesterType == SemesterType.SECOND) {
           currentIndex = tableRows.size() - 2;
           rowNumber = NUMBER_OF_MANDATORY_ROWS_IN_TABLE;
        }
        Tr rowToCopy = tableRows.get(currentIndex);
        if (grades != null){
        fillRowByGrade(tableRows.get(currentIndex - 1), grades.get(0), year);
        for (Grade grade : grades.subList(1, grades.size())) {
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByGrade(newRow, grade, year);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        }
        else fillRowByEmpty(tableRows.get(currentIndex - 1), studentDegree, year);
        for(int i = currentIndex; i < rowNumber; i++){
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByLost(newRow);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        table.getContent().remove(currentIndex);
    }

    private Map<String, String> getGradeDictionaryForEmpty(StudentDegree studentDegree, Integer year){
        Map<String, String> result = new HashMap<>();
        result.put("sy","НАВЧАЛЬНИЙ РІК");
        String gradeNumberYear = "";
        gradeNumberYear = getYearName(getStudentStudyYear(studentDegree, year)).toUpperCase()+" "+year+"-"+(year+1);
        result.put("f", getSemesterName(getStudentStudyYear(studentDegree, year)*2).toUpperCase());
        result.put("s", getSemesterName((getStudentStudyYear(studentDegree, year)*2)+1).toUpperCase());
        result.put("n",gradeNumberYear);
        return result;
    }

    private Map<String, String> getGradeDictionary(Grade grade, Integer year) {
        Map<String, String> result = new HashMap<>();
        result.put("sy","НАВЧАЛЬНИЙ РІК");
        String gradeNumberYear = "";
        gradeNumberYear = getYearName(getStudentStudyYear(grade.getStudentDegree(), year)).toUpperCase() + " " + year + "-" + (year + 1);
        result.put("f", getSemesterName(getStudentStudyYear(grade.getStudentDegree(), year)*2).toUpperCase());
        result.put("s", getSemesterName((getStudentStudyYear(grade.getStudentDegree(), year)*2)+1).toUpperCase());
        result.put("n",gradeNumberYear);
        result.put("subj", grade.getCourse().getCourseName().getName());
        result.put("t",resolveTypeField(grade));
        result.put("h", grade.getCourse().getHours().toString());
        result.put("c", grade.getCourse().getCredits().toString());
        String gradeFieldValue = "";
        if (grade.getGrade() != null && GradeUtil.isEnoughToPass(grade.getPoints())) {
            gradeFieldValue = grade.getCourse().getKnowledgeControl().isGraded() ? grade.getGrade().toString() : "зарах";
        }
        result.put("g", gradeFieldValue);
        if (GradeUtil.isEnoughToPass(grade.getPoints())) {
            result.put("p", grade.getPoints().toString());
        }
        if (grade.getEcts() != null && GradeUtil.isEnoughToPass(grade.getPoints())) {
            result.put("e", grade.getEcts().toString());
        }
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(grade.getStudentDegree().getStudentGroup().getId(), grade.getCourse().getId());
        if (courseForGroup.getExamDate() != null && GradeUtil.isEnoughToPass(grade.getPoints())) {
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(courseForGroup.getExamDate());
            result.put("d", date);
        }
        return result;
    }

    private Map<String, String> getLostDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("subj", "");
        result.put("t", "");
        result.put("h", "");
        result.put("c", "");
        result.put("g", "");
        result.put("p", "");
        result.put("e", "");
        result.put("d", "");
        return result;
    }

    private void fillRowByEmpty(Tr row, StudentDegree studentDegree, Integer year){
        replaceInRow(row, getGradeDictionaryForEmpty(studentDegree, year));
    }

    private void fillRowByGrade(Tr row, Grade grade, Integer year) {
        replaceInRow(row, getGradeDictionary(grade, year));
    }

    private void fillRowByLost(Tr row) {
        replaceInRow(row, getLostDictionary());
    }

    private String resolveTypeField(Grade grade) {
        switch (grade.getCourse().getKnowledgeControl().getId()) {
            case Constants.COURSEWORK:
                return "(КР)";
            case Constants.COURSE_PROJECT:
                return "(КП)";
            case Constants.INTERNSHIP:
            case Constants.NON_GRADED_INTERNSHIP:
                return "(пр)";
            case Constants.EXAM:
                return "(і)";
            case Constants.CREDIT:
            case Constants.DIFFERENTIATED_CREDIT :
            case Constants.STATE_EXAM:
            case Constants.ATTESTATION:
            default:
                return "(з)";
        }
    }

    private Map<String, String> getStudentDictionary(Student student) {
        Map<String, String> result = new HashMap<>();
        result.put("stud", student.getFullNameUkr());
        return result;
    }

    private void fillLastRow(Tbl table, StudentDegree studentDegree, Integer year) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        replaceInRow(tableRows.get(tableRows.size()-1), getLastRowDictionary(year, studentDegree));
    }

    private Map<String, String> getLastRowDictionary(Integer year, StudentDegree studentDegree) {
        Map<String, String> result = new HashMap<>();
        result.put("nc", "Переведений на " + getYearName((getStudentStudyYear(studentDegree, year + 1))) + " курс. Наказ від «_____»________20___року №____");
        return result;
    }

    private Integer getSemesterByYearForGroup(Integer year, StudentGroup studentGroup) {
        return (year - studentGroup.getCreationYear() + studentGroup.getBeginYears() - 1) * 2 + 1;
    }

    public int getStudentStudyYear(StudentDegree studentDegree, int year) {
        return year - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears() - 1;
    }

    private String getYearName(Integer year){
        final String[] YEAR_NAMES = {"перший", "другий", "третій", "четвертий", "п'ятий", "шостий"};
        return YEAR_NAMES[year];
    }

    private String getSemesterName(Integer semester){
        final String[] SEMESTER_NAMES = {"перший", "другий", "третій", "четвертий", "п'ятий", "шостий", "сьомий", "восьмий",
                "дев'ятий", "десятий", "одинадцятий", "дванадцятий"};
        return SEMESTER_NAMES[semester];
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
