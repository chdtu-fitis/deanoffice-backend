package ua.edu.chdtu.deanoffice.service.stipend;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ExtraPoints;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import ua.edu.chdtu.deanoffice.util.SemesterUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;

@Service
public class StipendService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final CurrentYearService currentYearService;
    private final StudentDegreeService studentDegreeService;

    private static final String TEMPLATE = TEMPLATES_PATH + "FacultyStipendList.docx";

    @Autowired
    private DocumentIOService documentIOService;

    @Autowired
    public StipendService(StudentDegreeRepository studentDegreeRepository,
                          StudentDegreeService studentDegreeService,
                          CurrentYearService currentYearService ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentDegreeService = studentDegreeService;
        this.currentYearService = currentYearService;
    }

    public List<StudentInfoForStipend> getStipendData(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<DebtorStudentDegreesBean> debtorStudentDegrees = getDebtorStudentDegrees(FacultyUtil.getUserFacultyIdInt());
        LinkedHashMap<Integer, StudentInfoForStipend> debtorStudentDegreesMap = new LinkedHashMap<>();

        debtorStudentDegrees.forEach(dsd -> {
            StudentInfoForStipend studentInfoForStipend = debtorStudentDegreesMap.get(dsd.getId());
            if (studentInfoForStipend == null) {
                studentInfoForStipend = modelMapper.map(dsd, StudentInfoForStipend.class);
            }
            CourseForStipendBean courseForStipend = new CourseForStipendBean(
                    dsd.getCourseName(), dsd.getKnowledgeControlName(), dsd.getSemester()
            );
            studentInfoForStipend.getDebtCourses().add(courseForStipend);
            debtorStudentDegreesMap.put(studentInfoForStipend.getId(), studentInfoForStipend);
        });

        List<StudentInfoForStipend> noDebtsStudentDegrees = getNoDebtStudentDegrees(FacultyUtil.getUserFacultyIdInt(), debtorStudentDegreesMap.keySet());
        noDebtsStudentDegrees.addAll(new ArrayList(debtorStudentDegreesMap.values()));
        return noDebtsStudentDegrees;
    }

    public List<DebtorStudentDegreesBean> getDebtorStudentDegrees(int facultyId) {
        int currentYear = currentYearService.getYear();
        List<Object[]> regularCoursesRawData = studentDegreeRepository.findDebtorStudentDegreesRaw(facultyId, SemesterUtil.getCurrentSemester(), currentYear);
        List<Object[]> selectiveCoursesRawData = studentDegreeRepository.findDebtorStudentsWithSelectiveCourseRaw(facultyId, SemesterUtil.getCurrentSemester(), currentYear);
        regularCoursesRawData.addAll(selectiveCoursesRawData);
        List<DebtorStudentDegreesBean> debtorStudentDegreesBeans = new ArrayList<>(regularCoursesRawData.size());
        regularCoursesRawData.forEach(item -> debtorStudentDegreesBeans.add(new DebtorStudentDegreesBean(
                (Integer)item[0]/*degreeId*/,
                (String)item[1]/*surname*/,
                (String)item[2]/*name*/,
                (String)item[3]/*patronimic*/,
                (String)item[4]/*degreeName*/,
                (String)item[5]/*groupName*/,
                (Integer)item[6]/*year*/,
                (String)item[7]/*tuitionTerm*/,
                (String)item[8]/*specialityCode*/,
                (String)item[9]/*specialityName*/,
                (String)item[10]/*specializationName*/,
                (String)item[11]/*departmentAbbreviation*/,
                BigDecimal.ZERO/*averageGrade*/,
                (String)item[13]/*courseName*/,
                (String)item[14]/*knowledgeControlName*/,
                (Integer)item[15]/*semester*/
        )));
        return debtorStudentDegreesBeans;
    }

    public List<StudentInfoForStipend> getNoDebtStudentDegrees(int facultyId, Set<Integer> debtorStudentDegreeIds) {
        if (debtorStudentDegreeIds.size() == 0) {
            debtorStudentDegreeIds = new HashSet();
            debtorStudentDegreeIds.add(0);
        }
        int currentYear = currentYearService.getYear();
        List<Object[]> rawData = studentDegreeRepository.findNoDebtStudentDegreesRaw(facultyId, debtorStudentDegreeIds, SemesterUtil.getCurrentSemester(), currentYear);
        List<StudentInfoForStipend> StudentInfoForStipend = new ArrayList<>(rawData.size());
        rawData.forEach(item -> StudentInfoForStipend.add(new StudentInfoForStipend(
                (Integer)item[0]/*degreeId*/,
                (String)item[1]/*surname*/,
                (String)item[2]/*name*/,
                (String)item[3]/*patronimic*/,
                (String)item[4]/*degreeName*/,
                (String)item[5]/*groupName*/,
                (Integer)item[6]/*year*/,
                (String)item[7]/*tuitionTerm*/,
                (String)item[8]/*specialityCode*/,
                (String)item[9]/*specialityName*/,
                (String)item[10]/*specializationName*/,
                (String)item[11]/*departmentAbbreviation*/,
                (BigDecimal)item[12]/*averageGrade*/,
                (Integer)item[13]/*extraPoints*/
        )));
        return StudentInfoForStipend;
    }

    public ExtraPoints getExtraPoints(Integer studentDegreeId, Integer semester){
        return studentDegreeRepository.getExtraPointsByStudentDegreeId(studentDegreeId, semester);
    }

    public void putExtraPoints(Integer studentDegreeId, Integer semester, Integer points){
        ExtraPoints extraPointsFromDB = getExtraPoints(studentDegreeId,semester);
        if (extraPointsFromDB == null){
            StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
            ExtraPoints newExtraPoints = create(studentDegree, semester , points);
            saveExtraPoints(newExtraPoints);
        } else{
            if(extraPointsFromDB.getPoints()!= points){
                extraPointsFromDB.setPoints(points);
                saveExtraPoints(extraPointsFromDB);
            }
        }
    }

    private ExtraPoints create(StudentDegree studentDegree, Integer semester, Integer points){
        ExtraPoints extraPoints = new ExtraPoints();
        extraPoints.setStudentDegree(studentDegree);
        extraPoints.setSemester(semester);
        extraPoints.setPoints(points);
        return extraPoints;
    }

    public Integer getStudentSemester(Integer studentDegreeId){
        Integer currYear = currentYearService.getYear();
        return studentDegreeRepository.getSemester(currYear, studentDegreeId, SemesterUtil.getCurrentSemester());
    }

    public ExtraPoints saveExtraPoints(ExtraPoints extraPoints){
        return studentDegreeRepository.save(extraPoints);
    }

    public Map<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> getStudentInfoGroupedBySpeciality(List<StudentInfoForStipend> studentInfoForStipend) {
        Map<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> studInfoGroupedBySpeciality = studentInfoForStipend.stream()
                .collect(Collectors.groupingBy(StudentInfoForStipend::getSingleSpecializationStipendDataBean, LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> entry : studInfoGroupedBySpeciality.entrySet()) {
            entry.getValue().sort(Comparator
                    .comparing(StudentInfoForStipend::getDegreeName)
                    .thenComparing(Collections.reverseOrder(Comparator.comparing(StudentInfoForStipend::getFinalGrade)))
                    .thenComparing(StudentInfoForStipend::getSurname)
                    .thenComparing(StudentInfoForStipend::getName)
                    .thenComparing(StudentInfoForStipend::getPatronimic)
            );
            Set<String> studentGroups = new HashSet<>();
            for (StudentInfoForStipend studentInfoForGroups : entry.getValue()) {
                studentGroups.add(studentInfoForGroups.getGroupName());
            }
            String groupNames = "";
            for (String name : studentGroups) {
                groupNames += name + " ";
            }
            entry.getKey().setGroupsName(groupNames);
        }
        return studInfoGroupedBySpeciality;
    }

    public File formDocument() throws Exception {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE);
        List<StudentInfoForStipend> stipendData = getStipendData();
        Map<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> studInfoGroupedBySpeciality = getStudentInfoGroupedBySpeciality(stipendData);
        generateTables(template, studInfoGroupedBySpeciality);

        for (int i = 1; i >= 0; i--){
            template.getMainDocumentPart().getContent().remove(i);
        }

        return documentIOService.saveDocumentToTemp(template, "stipend", FileFormatEnum.DOCX);
    }

    private Map<String, String> fillStipendData(StudentInfoForStipend studentInfoForStipend, int studentNumber){
        double normalizedAverageGrade = studentInfoForStipend.getAverageGrade().doubleValue()*0.9;
        Double finalGrade = studentInfoForStipend.getFinalGrade();
        Map<String, String> result = new HashMap();
        result.put("№", String.valueOf(studentNumber));
        result.put("name", studentInfoForStipend.getSurname()+ " " + studentInfoForStipend.getName() + " " + studentInfoForStipend.getPatronimic());
        result.put("gName", studentInfoForStipend.getGroupName());
        result.put("pts", String.format("%.2f", studentInfoForStipend.getAverageGrade()));
        result.put("pcPts", String.format("%.2f", normalizedAverageGrade));
        result.put("exPts", studentInfoForStipend.getExtraPoints()!= null ? studentInfoForStipend.getExtraPoints().toString():"-" );
        result.put("resPts", String.format("%.2f", finalGrade));
        return result;
    }

    private void generateTables(WordprocessingMLPackage template, Map<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> studentInfoForStipend) {
        Tbl templateTable = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(0);

        for (Map.Entry<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> entry : studentInfoForStipend.entrySet()) {
            Tbl table = XmlUtils.deepCopy(templateTable);
            fillFirstRow(table, entry.getKey());
            fillStudentData(table, entry.getValue());
            template.getMainDocumentPart().addObject(table);
        }
    }

    private void fillFirstRow(Tbl table, SingleSpecialityStipendDataBean stipendData) {

        String tuitionTerm = stipendData.getTuitionTerm();
        String ukTuitionTerm = tuitionTerm.equals("SHORTENED") ? "Скорочена" : "" ;

        Map<String, String> result = new HashMap<>();
        result.put("Term", ukTuitionTerm);
        result.put("speciality", stipendData.getSpecialityCode() + " " + stipendData.getSpecialityName());
        result.put("dName", stipendData.getDegreeName() + " " + stipendData.getGroupsName() );
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        replaceInRow(tableRows.get(0), result);
    }

    private void fillStudentData(Tbl table, List<StudentInfoForStipend> studentInfoForStipend) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        int studentNumber = 1;
        int currentIndex = 2;
        Tr rowToCopy = tableRows.get(currentIndex);
        for (StudentInfoForStipend studInfo : studentInfoForStipend) {
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            replaceInRow(newRow, fillStipendData(studInfo, studentNumber));
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
            studentNumber++;
        }
        table.getContent().remove(currentIndex);
    }
}
