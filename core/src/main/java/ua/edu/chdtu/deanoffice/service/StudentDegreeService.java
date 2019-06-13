package ua.edu.chdtu.deanoffice.service;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentDegreeShortBean;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.util.SemesterUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final CurrentYearService currentYearService;
    private final GradeRepository gradeRepository;

    public StudentDegreeService(StudentDegreeRepository studentDegreeRepository, CurrentYearService currentYearService,
                                GradeRepository gradeRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.currentYearService = currentYearService;
        this.gradeRepository = gradeRepository;
    }

    public StudentDegree getById(Integer id) {
        return studentDegreeRepository.getById(id);
    }

    public List<StudentDegree> getByIds(List<Integer> ids) {
        return studentDegreeRepository.getAllByIds(ids);
    }

    public List<StudentDegree> getAllByActive(boolean active, int facultyId) {
        return studentDegreeRepository.findAllByActive(active, facultyId);
    }

    public StudentDegree getFirst(Integer studentId) {
        List<StudentDegree> studentDegrees = this.studentDegreeRepository.findAllByStudentId(studentId);
        return (studentDegrees.isEmpty()) ? null : studentDegrees.get(0);
    }

    public List<StudentDegree> getAllByGroupId(Integer groupId) {
        return this.studentDegreeRepository.findStudentDegreeByStudentGroupIdAndActive(groupId, true);
    }

    public List<StudentDegree> getAllActiveByStudent(Integer studentId) {
        return this.studentDegreeRepository.findAllActiveByStudentId(studentId);
    }

    private String checkGraduateFieldValuesAvailability(StudentDegree studentDegree) {
        String message = "";
        message += Strings.isNullOrEmpty(studentDegree.getDiplomaNumber()) ? "Номер диплома. " : "";
        message += (studentDegree.getDiplomaDate() == null) ? "Дата диплома. " : "";
        message += (studentDegree.getPreviousDiplomaDate() == null) ? "Попередня дата диплома. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getPreviousDiplomaNumber()) ? "Попередній номер диплома. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getPreviousDiplomaIssuedBy()) ? "Попередній диплом виданий. " : "";
        message += (studentDegree.getAdmissionDate() == null) ? "Дата вступу. " : "";
        message += (studentDegree.getProtocolDate() == null) ? "Дата протокола. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getProtocolNumber()) ? "Номер протокола. " : "";
        message += (studentDegree.getSupplementDate() == null) ? "Дата додатка. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getSupplementNumber()) ? "Номер диплома. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getThesisName()) ? "Тема дипломної роботи. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getThesisNameEng()) ? "Тема дипломної роботи англійською. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getStudent().getSurnameEng()) ? "Прізвище англійською мовою. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getStudent().getNameEng()) ? "Ім'я англійською мовою. " : "";
        message += Strings.isNullOrEmpty(studentDegree.getStudent().getPatronimicEng()) ? "По батькові англійською мовою. " : "";
        return message;
    }

    private String checkStudentGradesForSupplement(StudentDegree studentDegree) {
        List<Grade> grades = gradeRepository.getByCheckStudentGradesForSupplement(studentDegree.getId());
        if (grades == null)
            return "";
        final StringBuilder message = new StringBuilder();
        grades.forEach(grade -> message.append(grade.getCourse().getCourseName().getName() + ", " + grade.getCourse().getSemester() + "сем; "));
        return message.toString();
    }

    public Map<StudentDegree, String> checkAllGraduatesData(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<StudentDegree> studentDegrees = studentDegreeRepository.findAllGraduates(year, facultyId, degreeId);
        return studentDegrees
                .stream()
                .filter(sd -> !checkGraduateFieldValuesAvailability(sd).equals(""))
                .collect(Collectors.toMap(sd -> sd, this::checkGraduateFieldValuesAvailability, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Map<StudentDegree, String> checkAllGraduatesGrades(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<StudentDegree> studentDegrees = studentDegreeRepository.findAllGraduates(year, facultyId, degreeId);
        return studentDegrees
                .stream()
                .filter(sd -> !checkStudentGradesForSupplement(sd).equals(""))
                .collect(Collectors.toMap(sd -> sd, this::checkStudentGradesForSupplement));
    }

    public StudentDegree getByStudentIdAndSpecializationId(boolean active, Integer studentId, Integer specializationId) {
        return this.studentDegreeRepository.findByStudentIdAndSpecialityId(active, studentId, specializationId);
    }

    public StudentDegree save(StudentDegree studentDegree) {
        return this.studentDegreeRepository.save(studentDegree);
    }

    public void update(List<StudentDegree> studentDegree) {
        studentDegreeRepository.save(studentDegree);
    }

    @Transactional
    public void updateThesisName(int idStudentDegree, String thesisName, String thesisNameEng, String fullSupervisor) {
        studentDegreeRepository.updateThesis(idStudentDegree, thesisName, thesisNameEng, fullSupervisor);
    }

    @Transactional
    public void updateDiplomaNumber(int studentDegreeId, String diplomaSeriesAndNumber, boolean honor, Date diplomaDate, Date supplementDate) {
        studentDegreeRepository.updateDiplomaNumber(studentDegreeId, diplomaSeriesAndNumber, honor, diplomaDate, supplementDate);
    }

    public List<StudentDegree> getAllNotInImportData(List<Integer> ids, int facultyId, int degreeId, int specialityId) {
        return studentDegreeRepository.findAll(StudentDegreeSpecification.getAbsentStudentDegreeInImportData(ids, facultyId, degreeId, specialityId));
    }

    public StudentDegree getBySupplementNumber(String supplementNumber) {
        List<StudentDegree> studentDegrees = studentDegreeRepository.findBySupplementNumber(supplementNumber);
        return (studentDegrees.size() == 1) ? studentDegrees.get(0) : null;
    }

    @Transactional
    public void assignStudentsToGroup(List<StudentDegree> students, StudentGroup group) {
        studentDegreeRepository.assignStudentsToGroup(students, group);
    }

    @Transactional
    public void assignRecordBookNumbersToStudents(Map<Integer, String> studentDegreeIdsAndRecordBooksNumbers) {
        studentDegreeIdsAndRecordBooksNumbers.forEach(studentDegreeRepository::assignRecordBookNumbersToStudents);
    }

    public StudentDegree getByStudentFullNameAndGroupId(String fullName, int groupId) {
        List<StudentDegree> studentDegrees = this.studentDegreeRepository.findByFullNameAndGroupId(fullName, groupId);
        return (studentDegrees.size() > 1 || studentDegrees.size() == 0) ? null : studentDegrees.get(0);
    }

    public List<StudentDegree> getActiveByIdsAndFaculty(List<Integer> ids, int facultyId) {
        if (ids == null || ids.size() == 0)
            return new ArrayList<>();
        return studentDegreeRepository.findActiveByIdsAndFacultyId(ids, facultyId);
    }

    public int getCountAllActiveStudents(int specializationId, int studyYear, Payment payment, int degreeId) {
        return studentDegreeRepository.findCountAllActiveStudentsBySpecializationIdAndStudyYearAndPayment(specializationId, currentYearService.getYear(), studyYear, payment.toString(), degreeId);
    }

    public int getCountAllActiveDebtors(int specializationId, int studyYear, TuitionForm tuitionForm, Payment payment, int degreeId) {
        return studentDegreeRepository.findCountAllActiveDebtorsBySpecializationIdAndStudyYearAndTuitionFormAndPayment(specializationId, currentYearService.getYear(), studyYear, tuitionForm.toString(), payment.toString(), degreeId);
    }

    public int getCountAllActiveDebtorsWithLessThanThreeDebs(int specializationId, int studyYear, TuitionForm tuitionForm, Payment payment, int degreeId) {
        return studentDegreeRepository.findAllActiveDebtorsWithLessThanThreeDebs(specializationId, currentYearService.getYear(), studyYear, tuitionForm.toString(), payment.toString(), degreeId).length;
    }

    public int getCountAllActiveDebtorsWithThreeOrMoreDebts(int specializationId, int studyYear, TuitionForm tuitionForm, Payment payment, int degreeId) {
        return studentDegreeRepository.findAllActiveDebtorsWithThreeOrMoreDebts(specializationId, currentYearService.getYear(), studyYear, tuitionForm.toString(), payment.toString(), degreeId).length;
    }

    public Map<String, List<StudentDegreeShortBean>> getStudentsShortInfoGroupedByGroupNames(List<Integer> studentGroupIds) {
        List<Object[]> studentDegreesShortFields = studentDegreeRepository.getStudentDegreeShortFields(studentGroupIds);
        List<StudentDegreeShortBean> studentDegreeShortBeans = mapToStudentDegreeShortBeans(studentDegreesShortFields);
        Map<String, List<StudentDegreeShortBean>> groupNameAndListStudentDegreeShortBeansMap = new HashMap<>();

        for (StudentDegreeShortBean studentDegreeShortBean : studentDegreeShortBeans) {
            String groupName = studentDegreeShortBean.getGroupName();
            if (groupNameAndListStudentDegreeShortBeansMap.get(groupName) == null) {
                List<StudentDegreeShortBean> sDSBByGroupName = new ArrayList<>();
                sDSBByGroupName.add(studentDegreeShortBean);
                groupNameAndListStudentDegreeShortBeansMap.put(groupName, sDSBByGroupName);
            } else {
                List<StudentDegreeShortBean> sDSBByGroupName = groupNameAndListStudentDegreeShortBeansMap.get(groupName);
                sDSBByGroupName.add(studentDegreeShortBean);
            }
        }

        return groupNameAndListStudentDegreeShortBeansMap;
    }

    private List<StudentDegreeShortBean> mapToStudentDegreeShortBeans(List<Object[]> studentDegreesShortFields) {
        List<StudentDegreeShortBean> studentDegreeShortBeans = new ArrayList<>(studentDegreesShortFields.size());
        for (Object[] studentDegreeShortFields : studentDegreesShortFields) {
            studentDegreeShortBeans.add(new StudentDegreeShortBean((String) studentDegreeShortFields[0], (String) studentDegreeShortFields[1],
                    (String) studentDegreeShortFields[2], (String) studentDegreeShortFields[3],
                    (String) studentDegreeShortFields[4]));
        }
        return studentDegreeShortBeans;
    }

    public int getCountAllActiveDebtorsWithThreeOrMoreDebtsForCurrentSemester(int specializationId,
                                                                              int studyYear,
                                                                              TuitionForm tuitionForm,
                                                                              Payment payment,
                                                                              int degreeId) {
        int semester = SemesterUtil.getCurrentSemester();
        return studentDegreeRepository.findAllActiveDebtorsWithThreeOrMoreDebts(specializationId,
                currentYearService.getYear(), studyYear, tuitionForm.toString(), payment.toString(), degreeId, semester).length;
    }

    public int getCountAllActiveDebtorsWithLessThanThreeDebsForCurrentSemester(int specializationId,
                                                                               int studyYear,
                                                                               TuitionForm tuitionForm,
                                                                               Payment payment,
                                                                               int degreeId) {
        int semester = SemesterUtil.getCurrentSemester();
        return studentDegreeRepository.findAllActiveDebtorsWithLessThanThreeDebs(specializationId,
                currentYearService.getYear(), studyYear, tuitionForm.toString(), payment.toString(), degreeId, semester).length;
    }

    public int getCountAllActiveDebtorsForCurrentSemester(int specializationId, int studyYear, TuitionForm tuitionForm, Payment payment, int degreeId) {
        int semester = SemesterUtil.getCurrentSemester();
        return studentDegreeRepository.findCountAllActiveDebtorsBySpecializationIdAndStudyYearAndTuitionFormAndPayment(specializationId,
                currentYearService.getYear(), studyYear, tuitionForm.toString(), payment.toString(), degreeId, semester);
    }
}
