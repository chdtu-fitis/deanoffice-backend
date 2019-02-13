package ua.edu.chdtu.deanoffice.service.document.report.groupgrade;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.service.CourseService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;
import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.*;
import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class SummaryForGroupService {
    private static final String TEMPLATE = TEMPLATES_PATH + "GradesTable.docx";
    private static final int MAXIMUM_STUDENTS_IN_TABLE = 15;

    @Autowired
    private DocumentIOService documentIOService;

    @Autowired
    private StudentGroupService studentGroupService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private CourseService courseService;


    SummaryForGroupService() {
    }

    public File formDocument(Integer groupId)
            throws Docx4JException, IOException {
        List<StudentSummaryForGroup> studentsSummaries = new ArrayList<>();
        StudentGroup group = studentGroupService.getById(groupId);
        List<Course> courses = courseService.getCoursesByGroupId(groupId);
        fillStudentsSummaries(studentsSummaries, group, courses);

        List<List<StudentSummaryForGroup>> dividedStudentSummaries = divideStudentSummaries(studentsSummaries);

        WordprocessingMLPackage template = formTables(dividedStudentSummaries, group);

        String fileName = LanguageUtil.transliterate(group.getName());
        return documentIOService.saveDocumentToTemp(template, fileName, FileFormatEnum.DOCX);
    }

    private void fillStudentsSummaries(List<StudentSummaryForGroup> studentsSummaries,
                                       StudentGroup group, List<Course> courses) {
        List<StudentDegree> studentDegrees = new ArrayList<>(group.getStudentDegrees());
        studentDegrees.sort((sd1, sd2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(sd1.getStudent().getSurname(), sd2.getStudent().getSurname());
        });
        studentDegrees.forEach((studentDegree) -> {
            List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegree.getId());
            List<Grade> gradesToAdd1 = new ArrayList<>();
            List<Grade> gradesToAdd2 = new ArrayList<>();
            List<Grade> gradesToAdd3 = new ArrayList<>();
            List<Grade> gradesToAdd4 = new ArrayList<>();
            boolean courseIsRepeated;
            for (Course course : courses) {
                courseIsRepeated = false;
                for (List<Grade> gradesSublist : grades) {
                    for (Grade grade : gradesSublist)
                        if (course.equals(grade.getCourse())) courseIsRepeated = true;
                }
                if(!courseIsRepeated) {
                    Grade grade = new Grade();
                    grade.setCourse(course);
                    switch (grade.getCourse().getKnowledgeControl().getId()) {
                        case Constants.COURSEWORK:
                        case Constants.COURSE_PROJECT: {
                            gradesToAdd2.add(grade);
                            break;
                        }
                        case Constants.INTERNSHIP:
                        case Constants.NON_GRADED_INTERNSHIP: {
                            gradesToAdd3.add(grade);
                            break;
                        }
                        case Constants.EXAM:
                        case Constants.CREDIT:
                        case Constants.DIFFERENTIATED_CREDIT : {
                            gradesToAdd1.add(grade);
                            break;
                        }
                        case Constants.STATE_EXAM:
                        case Constants.ATTESTATION:
                        default:
                            gradesToAdd4.add(grade);
                            break;
                    }
                }
            }
            gradesToAdd1.addAll(grades.remove(0));
            grades.add(0, gradesToAdd1);
            gradesToAdd2.addAll(grades.remove(1));
            grades.add(1, gradesToAdd2);
            gradesToAdd3.addAll(grades.remove(2));
            grades.add(2, gradesToAdd3);
            gradesToAdd4.addAll(grades.remove(3));
            grades.add(3, gradesToAdd4);
                    studentsSummaries.add(new StudentSummaryForGroup(studentDegree, grades));
                }
        );
    }

    private WordprocessingMLPackage formTables(List<List<StudentSummaryForGroup>> dividedStudentSummaries, StudentGroup studentGroup)
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE);

        addGroupName(template, studentGroup);

        for (int i = 0; i < dividedStudentSummaries.size() - 1; i++) {
            copyTable(template, 0);
        }

        for (int i = 0; i < dividedStudentSummaries.size(); i++) {
            Tbl table = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(i);
            prepareTable(table, dividedStudentSummaries.get(i), studentGroup);
        }
        replacePlaceholdersWithBlank(template);
        return template;
    }

    private void addGroupName(WordprocessingMLPackage template, StudentGroup studentGroup) {
        replaceValuesInTextPlaceholders(Arrays.asList(getTextsPlaceholdersFromContentAccessor(template.getMainDocumentPart()).get(0)),
                getGroupNameDictionary(studentGroup));
        template.getMainDocumentPart().getContent().get(0);
    }


    private List<List<StudentSummaryForGroup>> divideStudentSummaries(List<StudentSummaryForGroup> studentSummaries) {
        List<List<StudentSummaryForGroup>> result = new ArrayList<>();
        int partSize = MAXIMUM_STUDENTS_IN_TABLE;
        for (int i = 0; i < studentSummaries.size(); i += partSize) {
            result.add(studentSummaries.subList(i,
                    Math.min(i + partSize, studentSummaries.size())));
        }
        return result;
    }

    private void prepareTable(Tbl table, List<StudentSummaryForGroup> studentSummaries, StudentGroup studentGroup) {
        prepareTableStructure(table, studentSummaries);
        fillData(table, studentSummaries, studentGroup);
    }

    private void prepareTableStructure(Tbl table, List<StudentSummaryForGroup> studentSummaries) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        SummaryForGroupTableDetails summaryForGroupTableDetails = new SummaryForGroupTableDetails(studentSummaries);
        tableRows.forEach((tableRow) -> {
            for (int i = 0; i < studentSummaries.size() - 1; i++) {
                cloneLastCellInRow(tableRow);
            }
        });
        copyRowNTimes(table, summaryForGroupTableDetails.getRowWithDiplomaGradePosition(),
                summaryForGroupTableDetails.getRowWithGeneralGradesEnds() - summaryForGroupTableDetails.getRowWithDiplomaGradePosition());
        copyRowNTimes(table, summaryForGroupTableDetails.getRowWithCourseWorksStarts(),
                summaryForGroupTableDetails.getRowWithCourseWorksEnds() - summaryForGroupTableDetails.getRowWithCourseWorksStarts());
        copyRowNTimes(table, summaryForGroupTableDetails.getRowWithPracticesStarts(), summaryForGroupTableDetails.getRowWithPracticesEnds()
                - summaryForGroupTableDetails.getRowWithPracticesStarts());
    }

    private void fillData(Tbl table, List<StudentSummaryForGroup> studentSummaries, StudentGroup studentGroup) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        SummaryForGroupTableDetails summaryForGroupTableDetails = new SummaryForGroupTableDetails(studentSummaries);
        List<Grade> generalGrades = new ArrayList<>(summaryForGroupTableDetails.getListWithMaxNumberOfGrades(0));
        List<Grade> courseWorks = new ArrayList<>(summaryForGroupTableDetails.getListWithMaxNumberOfGrades(1));
        List<Grade> practices = new ArrayList<>(summaryForGroupTableDetails.getListWithMaxNumberOfGrades(2));
        List<Grade> diplomaGrades = new ArrayList<>(summaryForGroupTableDetails.getListWithMaxNumberOfGrades(3));
        Tr rowWithStudentNames = tableRows.get(summaryForGroupTableDetails.getRowWithNamesPosition());
        Tr rowWithTotalGrade = tableRows.get(summaryForGroupTableDetails.getRowWithTotalGradesPosition());
        Tr rowWithAverageGrade = tableRows.get(summaryForGroupTableDetails.getRowWithAverageGradePosition());
        Tr rowWithDiplomaGrade = tableRows.get(summaryForGroupTableDetails.getRowWithDiplomaGradePosition());
        List<Tr> rowsWithGeneralGrades = new ArrayList<>(
                tableRows.subList(summaryForGroupTableDetails.getRowWithGeneralGradesStarts(), summaryForGroupTableDetails.getRowWithGeneralGradesEnds() + 1)
        );
        List<Tr> rowsWithCourseWorks = new ArrayList<>(
                tableRows.subList(summaryForGroupTableDetails.getRowWithCourseWorksStarts(), summaryForGroupTableDetails.getRowWithCourseWorksEnds() + 1)
        );
        List<Tr> rowsWithPractices = new ArrayList<>(
                tableRows.subList(summaryForGroupTableDetails.getRowWithPracticesStarts(), summaryForGroupTableDetails.getRowWithPracticesEnds() + 1));
        Tr rowWithHours = tableRows.get(summaryForGroupTableDetails.getRowWithHoursPosition());
        Tr rowWithCredit = tableRows.get(summaryForGroupTableDetails.getRowWithCreditsPosition());

        int cellNumberGradesStartWith = 2;

        replaceInCell(rowWithDiplomaGrade, 0, getNumberedDictionary(generalGrades.size() + practices.size() + courseWorks.size() + 1));
        if (diplomaGrades.size() > 0) {
            replaceInCell(rowWithDiplomaGrade, 1, getCourseDictionary(diplomaGrades.get(0), studentGroup));
        }

        for (int i = 0; i < generalGrades.size(); i++) {
            replaceInCell(rowsWithGeneralGrades.get(i), 0, getNumberedDictionary(i + 1));
        }

        for (int i = 0; i < generalGrades.size(); i++) {
            replaceInCell(rowsWithGeneralGrades.get(i), 1, getCourseDictionary(generalGrades.get(i), studentGroup));
        }

        for (int i = 0; i < courseWorks.size(); i++) {
            replaceInCell(rowsWithCourseWorks.get(i), 0, getNumberedDictionary(generalGrades.size() + i + 1));
        }

        for (int i = 0; i < courseWorks.size(); i++) {
            replaceInCell(rowsWithCourseWorks.get(i), 1, getCourseDictionary(courseWorks.get(i), studentGroup));
        }

        for (int i = 0; i < practices.size(); i++) {
            replaceInCell(rowsWithPractices.get(i), 0, getNumberedDictionary(generalGrades.size() + courseWorks.size() + i + 1));
        }

        for (int i = 0; i < practices.size(); i++) {
            replaceInCell(rowsWithPractices.get(i), 1, getCourseDictionary(practices.get(i), studentGroup));
        }


        for (int studentNumber = 0; studentNumber < studentSummaries.size(); studentNumber++) {
            StudentSummaryForGroup studentSummary = studentSummaries.get(studentNumber);
            generalGrades = new ArrayList<>(studentSummaries.get(studentNumber).getGrades().get(0));
            courseWorks = new ArrayList<>(studentSummaries.get(studentNumber).getGrades().get(1));
            practices = new ArrayList<>(studentSummaries.get(studentNumber).getGrades().get(2));
            diplomaGrades = new ArrayList<>(studentSummaries.get(studentNumber).getGrades().get(3));

            replaceInCell(rowWithStudentNames, cellNumberGradesStartWith + studentNumber, getStudentInitialsDictionary(studentSummary));

            if (diplomaGrades.size() > 0) {
                replaceInCell(rowWithDiplomaGrade, cellNumberGradesStartWith + studentNumber, getGradeDictionary(diplomaGrades.get(0)));
            }

            for (int i = 0; i < generalGrades.size(); i++) {
                replaceInCell(rowsWithGeneralGrades.get(i), cellNumberGradesStartWith + studentNumber, getGradeDictionary(generalGrades.get(i)));
            }

            for (int i = 0; i < courseWorks.size(); i++) {
                replaceInCell(rowsWithCourseWorks.get(i), cellNumberGradesStartWith + studentNumber, getGradeDictionary(courseWorks.get(i)));
            }

            for (int i = 0; i < practices.size(); i++) {
                replaceInCell(rowsWithPractices.get(i), cellNumberGradesStartWith + studentNumber, getGradeDictionary(practices.get(i)));
            }
            replaceInCell(rowWithTotalGrade, cellNumberGradesStartWith + studentNumber, getTotalGradeDictionary(studentSummary));
            replaceInCell(rowWithAverageGrade, cellNumberGradesStartWith + studentNumber, getAverageGradeDictionary(studentSummary));
            replaceInCell(rowWithHours, cellNumberGradesStartWith + studentNumber, getHoursDictionary(studentSummary));
            replaceInCell(rowWithCredit, cellNumberGradesStartWith + studentNumber, getCreditsDictionary(studentSummary));
        }

    }

    private Map<String, String> getCourseDictionary(Grade grade, StudentGroup studentGroup) {
        Course course = grade.getCourse();
        HashMap<String, String> result = new HashMap<>();
        String courseString = course.getCourseName().getName();
        if (grade.getCourse() instanceof CombinedCourse) {
            if (((CombinedCourse) grade.getCourse()).getNumberOfSemesters() > 1&&((CombinedCourse) grade.getCourse()).isCombined()) {
                courseString += " (БС)";
            }
        }

        if (!grade.getCourse().getKnowledgeControl().isGraded()) {
            courseString += " (Зал.)";
        }

        result.put("s", courseString);
        result.put("p", String.format("%s %d %.1f", getPeriod(grade, studentGroup), course.getHours(), course.getCredits()));
        return result;
    }

    private String getPeriod(Grade grade, StudentGroup studentGroup) {
            int studyYear = studentGroup.getCreationYear();
            int numberOfSemesters = 1;
            Integer semester = grade.getCourse().getSemester();
            if (grade.getCourse() instanceof CombinedCourse) {
                numberOfSemesters = ((CombinedCourse) grade.getCourse()).getNumberOfSemesters();
            }

            int finishingSemester = semester + numberOfSemesters - 1;
            int startingYear = studyYear + (semester - 1) / 2;
            int finishingYear = studyYear + (finishingSemester / 2);
            if (finishingSemester % 2 != 0) {
                finishingYear++;
            }
            return startingYear + "-" + finishingYear;
    }

    private HashMap<String, String> getGroupNameDictionary(StudentGroup studentGroup) {
        HashMap<String, String> result = new HashMap<>();
        result.put("Group-name", studentGroup.getName());
        return result;
    }

    private HashMap<String, String> getTotalGradeDictionary(StudentSummaryForGroup studentSummaryForGroup) {
        HashMap<String, String> result = new HashMap<>();
        result.put("tg", String.format("%.0f", studentSummaryForGroup.getTotalGrade()));
        return result;
    }

    private HashMap<String, String> getAverageGradeDictionary(StudentSummaryForGroup studentSummaryForGroup) {
        HashMap<String, String> result = new HashMap<>();
        result.put("ag", String.format("%.2f", studentSummaryForGroup.getAverageGrade()));
        return result;
    }


    private HashMap<String, String> getGradeDictionary(Grade grade) {
        HashMap<String, String> result = new HashMap<>();
        if (grade.getPoints() != null) {
            result.put("g", grade.getPoints() + "");
        } else result.put("g", "");

        return result;
    }

    private HashMap<String, String> getStudentInitialsDictionary(StudentSummaryForGroup studentSummaryForGroup) {
        HashMap<String, String> result = new HashMap<>();
        result.put("student-name", studentSummaryForGroup.getStudent().getInitialsUkr());
        return result;
    }

    private HashMap<String, String> getNumberedDictionary(int number) {
        HashMap<String, String> result = new HashMap<>();
        result.put("N", number + "");
        return result;
    }

    private HashMap<String, String> getHoursDictionary(StudentSummaryForGroup studentSummaryForGroup) {
        HashMap<String, String> result = new HashMap<>();
        result.put("h", studentSummaryForGroup.getTotalHours() + "");
        return result;
    }

    private HashMap<String, String> getCreditsDictionary(StudentSummaryForGroup studentSummaryForGroup) {
        HashMap<String, String> result = new HashMap<>();
        result.put("c", String.format("%.1f", studentSummaryForGroup.getTotalCredits()));
        return result;
    }

    private void copyRowNTimes(Tbl table, int fromIndex, int n) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Tr startRow = tableRows.get(fromIndex);
        for (int i = 0; i < n; i++) {
            Tr newRow = XmlUtils.deepCopy(startRow);
            table.getContent().add(fromIndex, newRow);
        }
    }
}
