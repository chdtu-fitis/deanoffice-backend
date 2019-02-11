package ua.edu.chdtu.deanoffice.service;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final CurrentYearService currentYearService;

    public StudentDegreeService(StudentDegreeRepository studentDegreeRepository, CurrentYearService currentYearService) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.currentYearService = currentYearService;
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

    public Map<StudentDegree, String> checkAllGraduates(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<StudentDegree> studentDegrees = studentDegreeRepository.findAllGraduates(year, facultyId, degreeId);
        return studentDegrees
                .stream()
                .filter(sd -> !checkGraduateFieldValuesAvailability(sd).equals(""))
                .collect(Collectors.toMap(sd -> sd, this::checkGraduateFieldValuesAvailability, (e1, e2) -> e1, LinkedHashMap::new));
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
    public void updateThesisName(int idStudentDegree, String thesisName, String thesisNameEng, String fullSupervisor){
        studentDegreeRepository.updateThesis(idStudentDegree,thesisName, thesisNameEng, fullSupervisor);
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

    public StudentDegree getByStudentFullNameAndGroupId(String fullName, int groupId){
        List<StudentDegree> studentDegrees = this.studentDegreeRepository.findByFullNameAndGroupId(fullName, groupId);
        return (studentDegrees.size() > 1 || studentDegrees.size() == 0) ? null : studentDegrees.get(0);
    }

    public List<StudentDegree> getActiveByIdsAndFaculty(List<Integer> ids, int facultyId){
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

    public int getCountAllActiveDebtorsWithThreeOrMoreDebts(int specializationId, int studyYear,  TuitionForm tuitionForm, Payment payment, int degreeId) {
        return studentDegreeRepository.findAllActiveDebtorsWithThreeOrMoreDebts(specializationId, currentYearService.getYear(), studyYear, tuitionForm.toString(), payment.toString(), degreeId).length;
    }
}
