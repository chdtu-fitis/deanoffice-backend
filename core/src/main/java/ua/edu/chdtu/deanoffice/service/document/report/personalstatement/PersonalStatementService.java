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
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
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
    private StudentDegreeRepository studentDegreeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private StudentDegreeService studentDegreeService;
    @Autowired
    private StudentGroupService studentGroupService;
    @Autowired
    private DocumentIOService documentIOService;
    @Autowired
    private CourseForGroupService courseForGroupService;
    Integer yearForName;
    public File formDocument(Integer year, List<Integer> studentDegreeIds)
            throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        YearGrades yearGrades = new YearGrades(getGradeMap(year, studentDegreeIds, SemesterType.FIRST), getGradeMap(year, studentDegreeIds, SemesterType.SECOND));

        generateTables(template, yearGrades,year);
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

    private Map<StudentDegree, List<Grade>> getGradeMap(Integer year, List<Integer> studentDegreeIds, SemesterType semesterType) {
        List<StudentDegree> studentDegrees = studentDegreeRepository.getAllByIds(studentDegreeIds);
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
            formFirstSemesterInTable(table, yearGrades.getGradeMapForFirstSemester().get(studentDegree),year);
            formSecondSemesterInTable(table, yearGrades.getGradeMapForSecondSemester().get(studentDegree),year);
            fillLastRow(table,year);
            template.getMainDocumentPart().addObject(table);
        }
        template.getMainDocumentPart().getContent().remove(0);
    }

    private void fillFirstRow(Tbl table, Student student) {
            List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
            replaceInRow(tableRows.get(0), getStudentDictionary(student));
    }


    private void formFirstSemesterInTable(Tbl table, List<Grade> grades, Integer year) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        //int clearRowIndex=2;
        int currentIndex = 2;
        Tr rowToCopy = tableRows.get(currentIndex);
        fillRowByGrade(tableRows.get(currentIndex - 1), grades.get(0), year);
        for (Grade grade : grades.subList(1, grades.size())) {
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByGrade(newRow, grade, year);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        for(int i=currentIndex;i<10;i++){
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByLost(newRow);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        table.getContent().remove(currentIndex);
        //table.getContent().remove(2);
    }

    private void formSecondSemesterInTable(Tbl table, List<Grade> grades, Integer year) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        int currentIndex = tableRows.size() - 2;
        Tr rowToCopy = tableRows.get(currentIndex);
        fillRowByGrade(tableRows.get(currentIndex - 1), grades.get(0), year);
        for (Grade grade : grades.subList(1, grades.size())) {
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByGrade(newRow, grade, year);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        for(int i=currentIndex;i<20;i++){
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByLost(newRow);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        table.getContent().remove(currentIndex);
    }


    private Map<String, String> getGradeDictionary(Grade grade, Integer year) {
        Map<String, String> result = new HashMap<>();
        result.put("sy","НАВЧАЛЬНИЙ РІК");
        String gradeNumberYear="";
        int year2=year+1;
        gradeNumberYear=getYearName(0).toUpperCase()+" "+year+"-"+year2;
        result.put("f","ПЕРШИЙ");
        result.put("s","ДРУГИЙ");
        result.put("n",gradeNumberYear);//потрібна умова для перевірки (ДРУГИЙ,ТРЕТІЙ....)
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
    private Map<String, String> getLostDictionary() {
        Map<String, String> result = new HashMap<>();
        //result.put("sy","НАВЧАЛЬНИЙ РІК");
        //String gradeNumberYear="";
        //gradeNumberYear="______ "+"20__-"+"20__";
       // result.put("f","ПЕРШИЙ");
        //result.put("s","ДРУГИЙ");
       // result.put("n",gradeNumberYear);//потрібна умова для перевірки (ДРУГИЙ,ТРЕТІЙ....)
        result.put("subj", "");
        result.put("h", "");
        result.put("c","");
        result.put("g", "");
        result.put("p","");
        result.put("e", "");
        result.put("d", "");
        return result;
    }

    private void fillRowByGrade(Tr row, Grade grade, Integer year) {
        replaceInRow(row, getGradeDictionary(grade, year));
    }
    private void fillRowByLost(Tr row) {
        replaceInRow(row, getLostDictionary());
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
    private void fillLastRow(Tbl table, Integer year) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        replaceInRow(tableRows.get(tableRows.size()-1), getLastRowDictionary(year));
    }
    private Map<String, String> getLastRowDictionary(Integer year) {
        Map<String, String> result = new HashMap<>();
        result.put("nc", "Переведений на "+ getYearName(1) +" курс. Наказ від «_____»________20___року №____");
        return result;
    }

    private Integer getSemesterByYearForGroup(Integer year, StudentGroup studentGroup) {
        yearForName = year - studentGroup.getCreationYear() + studentGroup.getBeginYears()-1;
        return (year - studentGroup.getCreationYear() + studentGroup.getBeginYears() - 1) * 2 + 1;
    }

    private String getYearName(Integer finder){
        if (yearForName+finder>5) finder=0;
        String[] yearNames = {"перший", "другий","третій","четвертий","п'ятий","шостий"};
        return yearNames[yearForName+finder];
//        switch (yearForName) {
//            case 1: yearName = "";
//            break;
//
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
