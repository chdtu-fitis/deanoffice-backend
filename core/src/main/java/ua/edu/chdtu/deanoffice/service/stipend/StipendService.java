package ua.edu.chdtu.deanoffice.service.stipend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.ExtraPoints;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StipendService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentGroupRepository studentGroupRepository;

    @Autowired
    public StipendService(StudentDegreeRepository studentDegreeRepository, StudentGroupRepository studentGroupRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public void getDebtorStudents() {

    }

    public List<DebtorStudentDegreesBean> getDebtorStudentDegrees(int facultyId) {
        List<Object[]> rawData = studentDegreeRepository.findDebtorStudentDegreesRaw(facultyId);
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
        List<Object[]> rawData = studentDegreeRepository.findNoDebtStudentDegreesRaw(facultyId, debtorStudentDegreeIds);
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

    @Transactional
    public void updateExtraPoints(Integer studentDegreeId, Integer points){
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        Integer semester = (2018 - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears())* 2 - 1;
        studentDegreeRepository.updateExtraPoints(studentDegreeId, points, semester);
    }

    public List<ExtraPoints> getExtraPoints(Integer studentDegreeId){
        return studentDegreeRepository.getExtraPointsByStudentDegreeId(studentDegreeId);
    }

    public ExtraPoints saveExtraPoints(ExtraPoints extraPoints){
        return studentDegreeRepository.save(extraPoints);
    }
}
