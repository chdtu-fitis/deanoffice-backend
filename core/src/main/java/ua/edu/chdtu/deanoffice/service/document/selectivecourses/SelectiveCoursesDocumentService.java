package ua.edu.chdtu.deanoffice.service.document.selectivecourses;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.util.comparators.SelectiveCourseComparator;
import ua.edu.chdtu.deanoffice.util.comparators.StudentDegreeFullNameComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
                    template.getMainDocumentPart().getContent()
                            .add(createParagraphText("ДИСЦИПЛІНИ ЦИКЛУ ПРОФЕСІЙНОЇ ПІДГОТОВКИ"));
                    Integer countCourses = SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TypeCycle.PROFESSIONAL.toString())[0]+
                                            SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TypeCycle.PROFESSIONAL.toString())[1];
                    fillProfessionalTablesByFaculty(template,firstSemester,secondSemester,studyYear,degreeId, countCourses);
                    break;
            }
        }

        if (degreeId == DegreeEnum.MASTER.getId()) {
            template.getMainDocumentPart().getContent()
                    .add(createParagraphText("ДИСЦИПЛІНИ ЦИКЛУ ПРОФЕСІЙНОЇ ПІДГОТОВКИ"));
            Integer countCourses = SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TypeCycle.PROFESSIONAL.toString())[0]+
                    SELECTIVE_COURSES_NUMBER.get(degreeId)[studentsYear-1].get(TypeCycle.PROFESSIONAL.toString())[1];
            fillProfessionalTablesByFaculty(template,firstSemester, secondSemester, studyYear,degreeId, countCourses);
            template.getMainDocumentPart().getContent()
                    .add(createParagraphText("ДИСЦИПЛІНИ ЦИКЛУ ЗАГАЛЬНОЇ ПІДГОТОВКИ"));
            fillGeneralTablesBySemester(template,firstSemester,studyYear,degreeId);
            fillGeneralTablesBySemester(template,secondSemester,studyYear,degreeId);
        }

        String fileName = "вибіркові-"+degreeService.getById(degreeId).getName().toLowerCase()+"-"+studentsYear+"курс-"+studyYear+"-"+(studyYear+1);

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

        Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses = selectiveCoursesStudentDegreesService.getStudentDegreesBySelectiveCourses(studyYear, semester, degreeId);

        Map<TypeCycle, List<SelectiveCourse>> selectiveByTypeCycle = studentDegreesBySelectiveCourses.keySet()
                .stream()
                .collect(Collectors.groupingBy(selectiveCourse -> selectiveCourse.getTrainingCycle()));

        for (SelectiveCourse selectiveCourse: selectiveByTypeCycle.get(TypeCycle.GENERAL)) {
            fillGeneralTablesBySelectiveCourses(template, studentDegreesBySelectiveCourses.get(selectiveCourse), selectiveCourse);
        }
    }

    private void fillProfessionalTablesByFaculty(WordprocessingMLPackage template,
                                                 int semesterFirst,
                                                 int semesterSecond,
                                                 int studyYear,
                                                 int degreeId, int coursesCount) throws Docx4JException {

        Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses = concatSelectiveCourseBothSemesters(selectiveCoursesStudentDegreesService.getStudentDegreesBySelectiveCourses(studyYear, semesterFirst, degreeId),
                                                                                                                selectiveCoursesStudentDegreesService.getStudentDegreesBySelectiveCourses(studyYear, semesterSecond, degreeId));

        Map<TypeCycle, List<SelectiveCourse>> selectiveByTypeCycle = studentDegreesBySelectiveCourses.keySet()
                .stream()
                .collect(Collectors.groupingBy(selectiveCourse -> selectiveCourse.getTrainingCycle()));

        List<String> selectiveGroupNames = selectiveByTypeCycle.get(TypeCycle.PROFESSIONAL).stream()
                                                        .map(SelectiveCourse::getGroupName)
                                                        .distinct()
                                                        .collect(Collectors.toList());

        Map<SelectiveCourse,Map<Speciality,List<StudentDegree>>> studentDegreesBySpeciality = new HashMap<>();
        selectiveByTypeCycle.get(TypeCycle.PROFESSIONAL).forEach(
                selectiveCourse -> {
                    studentDegreesBySpeciality.put(selectiveCourse,
                            studentDegreesBySelectiveCourses.get(selectiveCourse).stream().collect(Collectors.groupingBy(studentDegree -> studentDegree.getSpecialization().getSpeciality())));
                }
        );

        Map<String,List<SelectiveCourse>> selectiveByGroupName = new HashMap<>();
        for (String groupName: selectiveGroupNames) {
            List<SelectiveCourse> selectiveCourses = studentDegreesBySelectiveCourses.keySet()
                                                        .stream()
                                                        .filter(sc -> sc.getGroupName().equalsIgnoreCase(groupName))
                                                        .collect(Collectors.toList());
            selectiveByGroupName.put(groupName, selectiveCourses);
        }

        Map<SelectiveCourse, StudentDegrees> selectiveCourseStudentDegreesMap = reorganizationByCommonStudentDegree(studentDegreesBySelectiveCourses, studentDegreesBySpeciality, selectiveByGroupName, coursesCount);

        List<SelectiveCoursesStudentDegreesGroup> selectiveCoursesStudentDegreesGroups = divisionByGroup(studentDegreesBySelectiveCourses, selectiveCourseStudentDegreesMap, selectiveByGroupName,coursesCount);

        for (SelectiveCoursesStudentDegreesGroup scsdGroup:selectiveCoursesStudentDegreesGroups) {

            if (scsdGroup.getCommonSelectiveCourses().size() == coursesCount) {

                template.getMainDocumentPart()
                        .getContent()
                        .addAll(fillProfessionalTableTemplate(scsdGroup.getCommonStudentDegrees(),scsdGroup.getCommonSelectiveCourses())
                                .getMainDocumentPart()
                                .getContent());
            } else {
                for (SelectiveCourse selectiveCourse: scsdGroup.getCommonSelectiveCourses()) {
                    fillGeneralTablesBySelectiveCourses(template, scsdGroup.getCommonStudentDegrees(), selectiveCourse);
                }

            }

        }

        List<Integer> allPrintedIds = new ArrayList<>();

        selectiveCoursesStudentDegreesGroups.forEach(
                selectiveCoursesStudentDegreesGroup -> {
                    allPrintedIds.addAll(
                    selectiveCoursesStudentDegreesGroup.getCommonSelectiveCourses().stream().map(selectiveCourse -> selectiveCourse.getId()).collect(Collectors.toList())
                    );
                }
        );

        for (SelectiveCourse selectiveCourse : selectiveByTypeCycle.get(TypeCycle.PROFESSIONAL)) {
            if (!allPrintedIds.contains(selectiveCourse.getId())) {
                fillGeneralTablesBySelectiveCourses(template, studentDegreesBySelectiveCourses.get(selectiveCourse), selectiveCourse);
            }
        }

    }

    private Map<SelectiveCourse, StudentDegrees> reorganizationByCommonStudentDegree(Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses,
                                                                                     Map<SelectiveCourse,Map<Speciality,List<StudentDegree>>> studentDegreesBySpeciality,
                                                                                     Map<String, List<SelectiveCourse>> selectiveByGroupName, int coursesCount) {
        Map<SelectiveCourse, StudentDegrees> result = new HashMap<>();
        for(String groupName: selectiveByGroupName.keySet()){
            List<Integer> allStudentDegreeIds = new ArrayList<>();
            List<SelectiveCourse> selectiveCourses = selectiveByGroupName.get(groupName);

            if (selectiveCourses.size() == coursesCount) { //&& !containSpecialities(studentDegreesBySpeciality, selectiveCourses, specialitiesCodes)

                selectiveByGroupName.get(groupName).forEach(
                        selectiveCourse -> allStudentDegreeIds.addAll(studentDegreesBySelectiveCourses.get(selectiveCourse).stream().map(sDegree -> sDegree.getId()).collect(Collectors.toList()))
                        );

                List<Integer> common = allStudentDegreeIds.stream().distinct().collect(Collectors.toList());

                selectiveByGroupName.get(groupName).forEach(selectiveCourse ->
                        common.retainAll(studentDegreesBySelectiveCourses.get(selectiveCourse).stream().map(sDegree -> sDegree.getId()).collect(Collectors.toList()))
                );

                for (SelectiveCourse selectiveCourse : selectiveByGroupName.get(groupName)) {
                    List<StudentDegree> commonStudentDegree = studentDegreesBySelectiveCourses.get(selectiveCourse).stream().filter(studentDegree -> common.contains(studentDegree.getId())).collect(Collectors.toList());
                    List<StudentDegree> differentStudentDegree = studentDegreesBySelectiveCourses.get(selectiveCourse).stream().filter(studentDegree -> !common.contains(studentDegree.getId())).collect(Collectors.toList());
                    result.put(selectiveCourse, new StudentDegrees(commonStudentDegree, differentStudentDegree));
                }
            }
        }

        return result;
    }

    private List<SelectiveCoursesStudentDegreesGroup> divisionByGroup(Map<SelectiveCourse, List<StudentDegree>> generalStudentDegreesBySelectiveCourses,
                                                                      Map<SelectiveCourse, StudentDegrees> selectiveCourseStudentDegrees,
                                                                      Map<String, List<SelectiveCourse>> selectiveByGroupName,
                                                                      int countCourses) {
        List<SelectiveCoursesStudentDegreesGroup> result = new ArrayList<>();

        for(String groupName: selectiveByGroupName.keySet()){
            if (selectiveByGroupName.get(groupName).size()==countCourses) {
                SelectiveCoursesStudentDegreesGroup selectiveCoursesStudentDegreesGroup = new SelectiveCoursesStudentDegreesGroup();
                selectiveCoursesStudentDegreesGroup.setCommonSelectiveCourses(selectiveByGroupName.get(groupName));

                SelectiveCourse firstCourseFromList = selectiveByGroupName.get(groupName).get(0);
                selectiveCoursesStudentDegreesGroup.setCommonStudentDegrees(selectiveCourseStudentDegrees.get(firstCourseFromList).getCommonStudentDegrees());
                result.add(selectiveCoursesStudentDegreesGroup);
            }

        }

        Map<SelectiveCourse, Map<Specialization,List<StudentDegree>>> selectiveCoursesDiffDegrees = new HashMap<>();

        selectiveCourseStudentDegrees.keySet().forEach(selectiveCourse -> {
                    if (selectiveCourseStudentDegrees.get(selectiveCourse).getDifferentStudentDegrees().size() > 0) {
                        Map<Specialization, List<StudentDegree>> studentsBySpec = selectiveCourseStudentDegrees.get(selectiveCourse).getDifferentStudentDegrees()
                                .stream()
                                .collect(Collectors.groupingBy(studentDegree -> studentDegree.getSpecialization()));
                        selectiveCoursesDiffDegrees.put(selectiveCourse, studentsBySpec);
                    }
                }
        );

        Set<Specialization> specializations = new HashSet<>();
        selectiveCoursesDiffDegrees.keySet().forEach(selectiveCourse -> {
            specializations.addAll(selectiveCoursesDiffDegrees.get(selectiveCourse).keySet());
        });

        List<Integer> addedCoursesIds = new ArrayList<>();

        result.forEach(
                selectiveCoursesStudentDegreesGroup -> {
                    addedCoursesIds.addAll(
                            selectiveCoursesStudentDegreesGroup.getCommonSelectiveCourses().stream().map(selectiveCourse -> selectiveCourse.getId()).collect(Collectors.toList())
                    );
                }
        );

        generalStudentDegreesBySelectiveCourses.keySet().forEach(selectiveCourse -> {
            if (!addedCoursesIds.contains(selectiveCourse.getId())) {
                Map<Specialization, List<StudentDegree>> studentsBySpec = generalStudentDegreesBySelectiveCourses.get(selectiveCourse)
                        .stream()
                        .collect(Collectors.groupingBy(studentDegree -> studentDegree.getSpecialization()));
                studentsBySpec.keySet().forEach(specialization -> {
                    if (specializations.contains(specialization))
                        selectiveCoursesDiffDegrees.put(selectiveCourse, studentsBySpec);//this put by with all specialization
                    //TODO: maybe put student degree for one specialization
                });
            }
        });

        result.addAll(getSelectiveCoursesGroups(selectiveCoursesDiffDegrees, specializations));

        return result;
    }

    private List<SelectiveCoursesStudentDegreesGroup> getSelectiveCoursesGroups(Map<SelectiveCourse, Map<Specialization,List<StudentDegree>>> selectiveCoursesDiffDegrees, Set<Specialization> specializations) {
        List<SelectiveCoursesStudentDegreesGroup> commonSelectiveCourses = new ArrayList<>();

        specializations.forEach(specialization -> {
            SelectiveCoursesStudentDegreesGroup selectiveCoursesStudentDegreesGroup = new SelectiveCoursesStudentDegreesGroup();

            selectiveCoursesDiffDegrees.keySet().forEach(selectiveCourse -> {
                List<StudentDegree> studentDegrees = selectiveCoursesDiffDegrees.get(selectiveCourse).get(specialization);
                if (studentDegrees!= null && studentDegrees.size() > 0) {
                    selectiveCoursesStudentDegreesGroup.getCommonStudentDegrees().addAll(studentDegrees);
                    selectiveCoursesStudentDegreesGroup.getCommonSelectiveCourses().add(selectiveCourse);
                }
            });
            List<StudentDegree> students = selectiveCoursesStudentDegreesGroup.getCommonStudentDegrees().stream().distinct().collect(Collectors.toList());
            selectiveCoursesStudentDegreesGroup.setCommonStudentDegrees(students);
            commonSelectiveCourses.add(selectiveCoursesStudentDegreesGroup);
        });

        return commonSelectiveCourses;
    }

    private boolean containSpecialities(Map<SelectiveCourse,Map<Speciality,List<StudentDegree>>> studentDegreesBySpeciality, List<SelectiveCourse> selectiveCourses, String[] codes) {
        for(SelectiveCourse selectiveCourse: selectiveCourses) {
            for(Speciality speciality:studentDegreesBySpeciality.get(selectiveCourse).keySet()){
                if (speciality.getCode().equalsIgnoreCase(codes[0]) || speciality.getCode().equalsIgnoreCase(codes[1]))
                    return true;
            }
        }
        return false;
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

    class SelectiveCoursesStudentDegreesGroup {
        private List<SelectiveCourse> commonSelectiveCourses = new ArrayList<>();
        private List<StudentDegree> commonStudentDegrees = new ArrayList<>();

        public List<SelectiveCourse> getCommonSelectiveCourses() {
            return commonSelectiveCourses;
        }

        public void setCommonSelectiveCourses(List<SelectiveCourse> commonSelectiveCourses) {
            this.commonSelectiveCourses = new ArrayList<>(commonSelectiveCourses);
        }

        public List<StudentDegree> getCommonStudentDegrees() {
            return commonStudentDegrees;
        }

        public void setCommonStudentDegrees(List<StudentDegree> commonStudentDegrees) {
            this.commonStudentDegrees = new ArrayList<>(commonStudentDegrees);
        }
    }

    class StudentDegrees {
        private List<StudentDegree> commonStudentDegrees = new ArrayList<>();
        private List<StudentDegree> differentStudentDegrees = new ArrayList<>();

        public StudentDegrees(List<StudentDegree> commonStudentDegrees, List<StudentDegree> differentStudentDegrees) {
            this.commonStudentDegrees = new ArrayList<>(commonStudentDegrees);
            this.differentStudentDegrees = new ArrayList<>(differentStudentDegrees);
        }

        public List<StudentDegree> getCommonStudentDegrees() {
            return commonStudentDegrees;
        }

        public void setCommonStudentDegrees(List<StudentDegree> commonStudentDegrees) {
            this.commonStudentDegrees = new ArrayList<>(commonStudentDegrees);
        }

        public List<StudentDegree> getDifferentStudentDegrees() {
            return differentStudentDegrees;
        }

        public void setDifferentStudentDegrees(List<StudentDegree> differentStudentDegrees) {
            this.differentStudentDegrees = new ArrayList<>(differentStudentDegrees);
        }
    }
}
