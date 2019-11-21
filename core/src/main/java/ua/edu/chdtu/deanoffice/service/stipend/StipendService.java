package ua.edu.chdtu.deanoffice.service.stipend;

import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
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
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceTextPlaceholdersInTemplate;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

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
//        LinkedHashMap<Integer, StudentInfoForStipendDTO> debtorStudentDegreesDTOsMap = new LinkedHashMap<>();
//        debtorStudentDegrees.forEach(dsd -> {
//            StudentInfoForStipendDTO studentInfoForStipendDTO = debtorStudentDegreesDTOsMap.get(dsd.getId());
//            if (studentInfoForStipendDTO == null) {
//                studentInfoForStipendDTO = Mapper.strictMap(dsd, StudentInfoForStipendDTO.class);
//            }
//            CourseForStipendDTO courseForStipendDto = new CourseForStipendDTO(
//                    dsd.getCourseName(), dsd.getKnowledgeControlName(), dsd.getSemester()
//            );
//            studentInfoForStipendDTO.getDebtCourses().add(courseForStipendDto);
//            debtorStudentDegreesDTOsMap.put(studentInfoForStipendDTO.getId(), studentInfoForStipendDTO);
//        });
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
//        List<StudentInfoForStipendDTO> noDebtsStudentDegreesDTOs = Mapper.map(noDebtsStudentDegrees, StudentInfoForStipendDTO.class);
        noDebtsStudentDegrees.addAll(new ArrayList(debtorStudentDegreesMap.values()));
        noDebtsStudentDegrees.sort(Comparator
                .comparing(StudentInfoForStipend::getDegreeName)
                .thenComparing(StudentInfoForStipend::getYear)
                .thenComparing(StudentInfoForStipend::getSpecialityCode)
                .thenComparing(StudentInfoForStipend::getSpecializationName)
                .thenComparing(StudentInfoForStipend::getGroupName)
                //.thenComparing(StudentInfoForStipendDTO::getExtraPoints)
                .thenComparing(Collections.reverseOrder(Comparator.comparing(StudentInfoForStipend::getFinalGrade)))
                .thenComparing(StudentInfoForStipend::getSurname)
                .thenComparing(StudentInfoForStipend::getName)
                .thenComparing(StudentInfoForStipend::getPatronimic)
        );
        return noDebtsStudentDegrees;
        //return ResponseEntity.ok(noDebtsStudentDegreesDTOs);
    }

    public List<DebtorStudentDegreesBean> getDebtorStudentDegrees(int facultyId) {
        int currentYear = currentYearService.getYear();
        List<Object[]> rawData = studentDegreeRepository.findDebtorStudentDegreesRaw(facultyId, SemesterUtil.getCurrentSemester(), currentYear);
        List<DebtorStudentDegreesBean> debtorStudentDegreesBeans = new ArrayList<>(rawData.size());
        rawData.forEach(item -> debtorStudentDegreesBeans.add(new DebtorStudentDegreesBean(
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
                // 0,
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
                //(Double)item[12]/*averageGrade*/,
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

    public File formDocument(StudentInfoForStipend studentInfoForStipend) throws Exception {

        WordprocessingMLPackage resultTemplate = formDocument(TEMPLATE, studentInfoForStipend);
        String fileName = transliterate("Rating");
        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
    }

    private WordprocessingMLPackage formDocument(String templateFilepath,StudentInfoForStipend studentInfoForStipend)
            throws Exception {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);
        VariablePrepare.prepare(template);
        replaceTextPlaceholdersInTemplate(template, fillStipendData(studentInfoForStipend));
        return template;
    }

    private HashMap<String, String> fillStipendData(StudentInfoForStipend studentInfoForStipend){
        Double bigDecimalPoints = studentInfoForStipend.getAverageGrade().doubleValue()*0.9;
        Double finalGrade = studentInfoForStipend.getFinalGrade();

        HashMap<String, String> result = new HashMap();
        result.put("course", studentInfoForStipend.getSpecializationName());
        result.put("name", studentInfoForStipend.getName()+studentInfoForStipend.getSurname()+studentInfoForStipend.getPatronimic());
        result.put("groupName", studentInfoForStipend.getGroupName());
        result.put("degreeName", studentInfoForStipend.getDegreeName());
        result.put("studyType", studentInfoForStipend.getTuitionTerm());
        result.put("points", studentInfoForStipend.getAverageGrade().toString());
        result.put("percentPoints", bigDecimalPoints.toString());
        result.put("extraPoints", studentInfoForStipend.getExtraPoints().toString());
        result.put("resultPoints", finalGrade.toString() );
        return result;
    }

}
