package ua.edu.chdtu.deanoffice.service.stipend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ExtraPoints;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.util.SemesterUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StipendService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final CurrentYearService currentYearService;
    private final StudentDegreeService studentDegreeService;

    @Autowired
    public StipendService(StudentDegreeRepository studentDegreeRepository,
                          StudentDegreeService studentDegreeService,
                          CurrentYearService currentYearService ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentDegreeService = studentDegreeService;
        this.currentYearService = currentYearService;
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
                BigDecimal.ZERO/*averageGrade*/,
                (String)item[13]/*courseName*/,
                (String)item[14]/*knowledgeControlName*/,
                (Integer)item[15]/*semester*/
        )));
        return debtorStudentDegreesBeans;
    }

    public List<DebtorStudentDegreesBean> getNoDebtStudentDegrees(int facultyId, Set<Integer> debtorStudentDegreeIds) {
        if (debtorStudentDegreeIds.size() == 0) {
            debtorStudentDegreeIds = new HashSet();
            debtorStudentDegreeIds.add(0);
        }
        int currentYear = currentYearService.getYear();
        List<Object[]> rawData = studentDegreeRepository.findNoDebtStudentDegreesRaw(facultyId, debtorStudentDegreeIds, SemesterUtil.getCurrentSemester(), currentYear);
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
                (BigDecimal)item[12]/*averageGrade*/,
                (Integer)item[13]/*extraPoints*/
        )));
        return debtorStudentDegreesBeans;
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
}
