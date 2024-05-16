package ua.edu.chdtu.deanoffice.service.document.selectivecourses;

import org.apache.commons.collections4.SetUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.entity.FieldOfKnowledge;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.TrainingCycle;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.util.comparators.SelectiveCourseComparator;
import ua.edu.chdtu.deanoffice.util.comparators.StudentDegreeFullNameComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants.SELECTIVE_COURSES_NUMBER;
import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.addRowToTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.createParagraph;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.createR;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.createText;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.findTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceTextPlaceholdersInTemplate;

@Service
public class SelectiveCoursesDocumentService {

    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "SelectiveCoursesGroupsHeader.docx";
    private static final String TEMPLATE_GENERAL_TABLE = TEMPLATES_PATH + "SelectiveCoursesGeneralTable.docx";
    private static final String TEMPLATE_PROF_TABLE = TEMPLATES_PATH + "SelectiveCoursesProfessionalTable.docx";

    private static final String STUDENTS_WITH_RIGHT_COURSES_COUNT = "common";
    private static final String STUDENTS_WITH_WRONG_COURSES_COUNT = "wrong";
    private final DocumentIOService documentIOService;
    private DegreeService degreeService;
    private final SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService;

    public SelectiveCoursesDocumentService(DocumentIOService documentIOService,
                                           DegreeService degreeService,
                                           SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService) {
        this.documentIOService = documentIOService;
        this.degreeService = degreeService;
        this.selectiveCoursesStudentDegreesService = selectiveCoursesStudentDegreesService;
    }

    public File formDocument(int studyYear, int studentsYear, int degreeId) throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);

        int firstSemester =  (studentsYear * 2) - 1;
        int secondSemester = (studentsYear * 2);

        fillDocumentHeader(template, studyYear, studentsYear, degreeId);

        if (degreeId == DegreeEnum.BACHELOR.getId()) {
            switch (studentsYear) {
                case 2:
                    template.getMainDocumentPart().getContent()
                            .add(createParagraphText("ДИСЦИПЛІНИ ЦИКЛУ ЗАГАЛЬНОЇ ПІДГОТОВКИ"));
                    fillGeneralTablesBySemester(template,firstSemester,studyYear,degreeId);
                    fillGeneralTablesBySemester(template,secondSemester,studyYear,degreeId);
                    break;
                case 3:
                case 4:
                        template.getMainDocumentPart().getContent()
                                .add(createParagraphText("ДИСЦИПЛІНИ ЦИКЛУ ПРОФЕСІЙНОЇ ПІДГОТОВКИ"));
                    Integer countCourses = SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TrainingCycle.PROFESSIONAL.toString())[0]+
                                            SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TrainingCycle.PROFESSIONAL.toString())[1];
                    fillProfessionalTablesByFieldOfKnowladge(template,firstSemester,secondSemester,studyYear,degreeId, countCourses);
                    break;
            }
        }

        if (degreeId == DegreeEnum.MASTER.getId()) {
            template.getMainDocumentPart().getContent()
                    .add(createParagraphText("ДИСЦИПЛІНИ ЦИКЛУ ПРОФЕСІЙНОЇ ПІДГОТОВКИ"));
            Integer countCourses = SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TrainingCycle.PROFESSIONAL.toString())[0]+
                    SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TrainingCycle.PROFESSIONAL.toString())[1];
            fillProfessionalTablesByFieldOfKnowladge(template,firstSemester, secondSemester, studyYear,degreeId, countCourses);
            template.getMainDocumentPart().getContent()
                    .add(createParagraphText("ДИСЦИПЛІНИ ЦИКЛУ ЗАГАЛЬНОЇ ПІДГОТОВКИ"));
            fillGeneralTablesBySemester(template,firstSemester,studyYear,degreeId);
            fillGeneralTablesBySemester(template,secondSemester,studyYear,degreeId);
        }

        String fileName = "selective-order-"+degreeService.getById(degreeId).getNameEng().toLowerCase()+"-"+studentsYear+"k-"+studyYear+"-"+(studyYear+1);
        return documentIOService.saveDocumentToTemp(template, fileName, FileFormatEnum.DOCX);
    }

    private void fillDocumentHeader(WordprocessingMLPackage template, int studyYear, int studentYear, int degreeId) {
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("appNum", String.valueOf(studentYear));
        commonDict.put("studentYear", String.valueOf(studentYear));
        commonDict.put("yearFrom", String.valueOf(studyYear));
        commonDict.put("yearTo", String.valueOf(studyYear+1));
        commonDict.put("degree", degreeService.getById(degreeId).getName().toLowerCase());
        replaceTextPlaceholdersInTemplate(template, commonDict);
    }

    private void fillGeneralTablesBySemester(WordprocessingMLPackage template,
                                             int semester,
                                             int studyYear,
                                             int degreeId
                                             ) throws Docx4JException {
        template.getMainDocumentPart()
                .getContent()
                .add(createParagraphText(semester+" СЕМЕСТР"));

        Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses = selectiveCoursesStudentDegreesService.getStudentDegreesGroupedBySelectiveCourses(studyYear, semester, degreeId);

        Map<TrainingCycle, List<SelectiveCourse>> selectiveByTypeCycle = studentDegreesBySelectiveCourses.keySet()
                .stream()
                .collect(Collectors.groupingBy(selectiveCourse -> selectiveCourse.getTrainingCycle()));

        for (SelectiveCourse selectiveCourse: selectiveByTypeCycle.get(TrainingCycle.GENERAL)) {
            fillGeneralTablesBySelectiveCourses(template, studentDegreesBySelectiveCourses.get(selectiveCourse), selectiveCourse);
        }
    }

    private void fillProfessionalTablesByFieldOfKnowladge(WordprocessingMLPackage template,
                                                          int semesterFirst,
                                                          int semesterSecond,
                                                          int studyYear,
                                                          int degreeId, int coursesCount) throws Docx4JException {
        Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses = concatSelectiveCourseBothSemesters(selectiveCoursesStudentDegreesService.getStudentDegreesGroupedBySelectiveCourses(studyYear, semesterFirst, degreeId),
                selectiveCoursesStudentDegreesService.getStudentDegreesGroupedBySelectiveCourses(studyYear, semesterSecond, degreeId));
        Map<FieldOfKnowledge, List<SelectiveCourse>> selectiveByFK = studentDegreesBySelectiveCourses.keySet()
                                                                                .stream()
                                                                                .collect(Collectors.groupingBy(selectiveCourse -> selectiveCourse.getFieldOfKnowledge()));
        Map<FieldOfKnowledge, List<SelectiveCourse>> allSpecialityStudentsCourses = new HashMap<>();
        Map<FieldOfKnowledge, List<SelectiveCourse>> partSpecialityStudentsCourses = new HashMap<>();

        List<FieldOfKnowledge> fieldsOfKnowledge = new ArrayList<>(selectiveByFK.keySet());
        fieldsOfKnowledge.sort(Comparator.comparing(FieldOfKnowledge::getCode));
        for (FieldOfKnowledge fk :fieldsOfKnowledge){
            if (selectiveByFK.get(fk).size() == coursesCount) {
                allSpecialityStudentsCourses.put(fk, selectiveByFK.get(fk));
            } else {
                partSpecialityStudentsCourses.put(fk, selectiveByFK.get(fk));
            }
        }

        StringBuilder wrongs = new StringBuilder();

        for (FieldOfKnowledge fk :allSpecialityStudentsCourses.keySet()){
            List<SelectiveCourse> selectiveCourses = allSpecialityStudentsCourses.get(fk);
            List<StudentDegree> wrongStudentsDegrees = getCommonAndWrongStudentsDegreesForSelectiveCourses(selectiveCourses, studentDegreesBySelectiveCourses).get(STUDENTS_WITH_WRONG_COURSES_COUNT);
            List<StudentDegree> commonStudentDegrees = getCommonAndWrongStudentsDegreesForSelectiveCourses(selectiveCourses, studentDegreesBySelectiveCourses).get(STUDENTS_WITH_RIGHT_COURSES_COUNT);
            List<SelectiveCourse> commonSelectiveCourses = new ArrayList<>(allSpecialityStudentsCourses.get(fk));
            template.getMainDocumentPart()
                        .getContent()
                        .addAll(fillProfessionalTableTemplate(commonStudentDegrees,commonSelectiveCourses)
                                .getMainDocumentPart()
                                .getContent());
            String wrongString = wrongStudentsDegrees
                    .stream()
                    .map(sd -> String.format("група: %s, cтудент: %s; ", sd.getStudentGroup().getName(), sd.getStudent().getSurname() + " " + sd.getStudent().getName() + " " + sd.getStudent().getPatronimic()))
                    .collect(Collectors.joining("; "));
            wrongs.append(wrongString);
        }

        for (FieldOfKnowledge fk :partSpecialityStudentsCourses.keySet()){
            template.getMainDocumentPart().getContent()
                    .add(createParagraphText("Галузь " + fk.getCode() + " " + fk.getName()));
            List<SelectiveCourse> selectiveCourses = partSpecialityStudentsCourses.get(fk);
            selectiveCourses.sort((o1, o2) -> o1.getCourse().getSemester().compareTo(o2.getCourse().getSemester()));
            for (SelectiveCourse selectiveCourse: selectiveCourses) {
                List<StudentDegree> studentDegrees = studentDegreesBySelectiveCourses.get(selectiveCourse);
                fillGeneralTablesBySelectiveCourses(template, studentDegrees, selectiveCourse);
            }
        }

        template.getMainDocumentPart().getContent()
                .add(createParagraphText("Помилки: \n" + wrongs.toString() ));

    }

    private Map<String, List<StudentDegree>> getCommonAndWrongStudentsDegreesForSelectiveCourses(List<SelectiveCourse> selectiveCourses, Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses) {
        Set<Set<Integer>> studentDegreeIds = new HashSet<>();

        for (SelectiveCourse sc : selectiveCourses) {
            studentDegreeIds.add(studentDegreesBySelectiveCourses.get(sc).stream().map(StudentDegree::getId).collect(Collectors.toSet()));
        }

        Set<Integer> diff = new HashSet<Integer>();
        for (Set<Integer> ids1 : studentDegreeIds) {
            for (Set<Integer> ids2 : studentDegreeIds) {
                diff.addAll(SetUtils.difference(ids1, ids2));
            }
        }

        Set<StudentDegree> commonStudentDegree = new HashSet<>();
        for (SelectiveCourse sc : selectiveCourses) {
            commonStudentDegree.addAll(studentDegreesBySelectiveCourses.get(sc).stream().filter(studentDegree ->!diff.contains(studentDegree.getId())).collect(Collectors.toList()));
        }

        Map<String, List<StudentDegree>> commonAndWrongStudentDegrees = new HashMap<>();
        commonAndWrongStudentDegrees.put(STUDENTS_WITH_RIGHT_COURSES_COUNT, new ArrayList<>(commonStudentDegree));

        Set<StudentDegree> wrongStudentDegree = new HashSet<>();
        for (SelectiveCourse sc : selectiveCourses) {
            wrongStudentDegree.addAll(studentDegreesBySelectiveCourses.get(sc).stream().filter(studentDegree ->diff.contains(studentDegree.getId())).collect(Collectors.toList()));
        }
        commonAndWrongStudentDegrees.put(STUDENTS_WITH_WRONG_COURSES_COUNT, new ArrayList<>(wrongStudentDegree));

        return commonAndWrongStudentDegrees;
    }

    private WordprocessingMLPackage fillProfessionalTableTemplate(List<StudentDegree> studentDegrees, List<SelectiveCourse> selectiveCourses) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PROF_TABLE);

        fillProfessionalTable(template, studentDegrees, selectiveCourses);

        return template;
    }

    private void fillProfessionalTable(WordprocessingMLPackage template, List<StudentDegree> studentDegrees, List<SelectiveCourse> selectiveCourses) {
        Tbl tempTable = findTable(template, "#n1");
        if (tempTable == null) {
            return;
        }
        List<Object> studentDegreeTableRows = getAllElementsFromObject(tempTable, Tr.class);
        Tr templateRow = (Tr) studentDegreeTableRows.get(0);
        int rowToAddIndex = 1;
        studentDegrees.sort(new StudentDegreeFullNameComparator());
        studentDegrees.sort((s1, s2) -> s1.getStudentGroup().getName().compareTo(s2.getStudentGroup().getName()));
        for (StudentDegree studentDegree: studentDegrees) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("n1", rowToAddIndex + ".");
            replacements.put("studentName", studentDegree.getStudent().getFullNameUkr());
            replacements.put("group", studentDegree.getStudentGroup().getName());
            replacements.put("faculty", studentDegree.getSpecialization().getFaculty().getAbbr());

            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);

        String specialityName = studentDegrees.stream()
                .map(studentDegree -> studentDegree.getSpecialization().getSpeciality())
                .distinct()
                .map(speciality -> speciality.getCode() + " " + speciality.getName())
                .collect(Collectors.joining(", "));

        fillProfTableHeader(template,selectiveCourses,specialityName);
    }

    private void fillProfTableHeader(WordprocessingMLPackage template, List<SelectiveCourse> selectiveCourses, String specialityName) {
        Tbl tempTable = findTable(template, "#course");
        if (tempTable == null) {
            return;
        }
        List<Object> selectiveCoursesTableRows = getAllElementsFromObject(tempTable, Tr.class);
        Tr templateRow = (Tr) selectiveCoursesTableRows.get(3);
        int rowToAddIndex = 4;
        selectiveCourses.sort(new SelectiveCourseComparator());
        selectiveCourses.sort((c1, c2) -> c1.getCourse().getSemester().compareTo(c2.getCourse().getSemester()));
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("course", selectiveCourse.getCourse().getCourseName().getName());
            replacements.put("semester", selectiveCourse.getCourse().getSemester().toString());

            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);

        SelectiveCourse selectiveCourse = selectiveCourses.get(0);//todo special StudentDegree
        Map<String, String> replacements = new HashMap<>();
        String groupName = selectiveCourse.getGroupName();
        replacements.put("speciality", specialityName);
        replacements.put("groupName", groupName);

        replaceTextPlaceholdersInTemplate(template, replacements);
    }

    public Map<SelectiveCourse, List<StudentDegree>> concatSelectiveCourseBothSemesters(Map<SelectiveCourse, List<StudentDegree>> selectiveFirstSemester,
                                                                                        Map<SelectiveCourse, List<StudentDegree>> selectiveSecondSemester){
        return Stream.of(selectiveFirstSemester, selectiveSecondSemester)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new ArrayList<>(e.getValue()),
                        (left, right) -> {left.addAll(right); return left;}
                ));
    }

    public WordprocessingMLPackage fillGeneralTablesBySelectiveCourses(WordprocessingMLPackage template,
                                                                       List<StudentDegree> studentDegrees, SelectiveCourse selectiveCourse) throws Docx4JException {
        template.getMainDocumentPart()
                    .getContent()
                    .addAll(fillGeneralTableTemplate(studentDegrees,selectiveCourse)
                            .getMainDocumentPart()
                            .getContent());
        return template;
    }

    public P createParagraphText(String s) {
        P paragraph = createParagraph();
        R run = createR();
        Text text = createText(s);
        run.getContent().add(text);
        paragraph.getContent().add(run);

        return paragraph;
    }

    private WordprocessingMLPackage fillGeneralTableTemplate(
            List<StudentDegree> studentDegrees,
            SelectiveCourse selectiveCourse
    ) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_GENERAL_TABLE);
        fillGeneralTable(template, studentDegrees);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("courseName", selectiveCourse.getCourse().getCourseName().getName());
        commonDict.put("groupName", selectiveCourse.getGroupName());
        commonDict.put("semester", selectiveCourse.getCourse().getSemester().toString());
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillGeneralTable(WordprocessingMLPackage template, List<StudentDegree> studentDegrees) {
        Tbl tempTable = findTable(template, "#n");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);
        Tr templateRow = (Tr) gradeTableRows.get(0);
        int rowToAddIndex = 1;
        studentDegrees.sort(new StudentDegreeFullNameComparator());
        studentDegrees.sort((s1, s2) -> s1.getStudentGroup().getName().compareTo(s2.getStudentGroup().getName()));
        for (StudentDegree studentDegree : studentDegrees) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("n", rowToAddIndex + ".");
            replacements.put("studentName", studentDegree.getStudent().getFullNameUkr());
            replacements.put("group",studentDegree.getStudentGroup().getName());
            replacements.put("facultyAbr",studentDegree.getStudentGroup().getSpecialization().getFaculty().getAbbr());

            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }

}
