package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface StudentDegreeRepository extends JpaRepository<StudentDegree, Integer>, JpaSpecificationExecutor<StudentDegree> {
    @Query("SELECT sd from StudentDegree sd " +
            "where sd.active = :active " +
            "and sd.specialization.faculty.id = :facultyId " +
            "order by sd.student.surname, sd.student.name, sd.student.patronimic, sd.specialization.name")
    List<StudentDegree> findAllByActive(
            @Param("active") boolean active,
            @Param("facultyId") Integer facultyId
    );

    @Query("SELECT sd from StudentDegree sd " +
            "where sd.id in :student_degree_ids")
    List<StudentDegree> getAllByIds(@Param("student_degree_ids") List<Integer> studentDegreeIds);

    StudentDegree getById(Integer id);

    @Query("SELECT sd FROM StudentDegree sd " +
            "where sd.student.id = :student_id")
    List<StudentDegree> findAllByStudentId(@Param("student_id") Integer studentId);

    @Query("SELECT sd FROM StudentDegree sd " +
            "where sd.student.id = :student_id and sd.active = true")
    List<StudentDegree> findAllActiveByStudentId(@Param("student_id") Integer studentId);

    @Query("select sd from StudentDegree sd " +
            "where sd.studentGroup.id = :groupId and sd.active = :active " +
            "order by sd.student.surname, sd.student.name, sd.student.patronimic")
    List<StudentDegree> findStudentDegreeByStudentGroupIdAndActive(
            @Param("groupId") Integer groupId,
            @Param("active") boolean active
    );

    @Query(value = "SELECT * FROM student_degree sd " +
            "INNER JOIN specialization s ON s.id = sd.specialization_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN student st ON st.id=sd.student_id " +
            "WHERE sg.active = TRUE and sd.active=true AND s.degree_id = :degree_id " +
            "AND floor(sg.creation_year + sg.study_years - 0.1) = :year " +
            "AND s.faculty_id = :faculty_id " +
            "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentDegree> findAllGraduates(
            @Param("year") int year,
            @Param("faculty_id") int facultyId,
            @Param("degree_id") int degreeId
    );

    @Query(value = "select sd.id from student_degree sd " +
            "inner join specialization s ON s.id = sd.specialization_id " +
            "WHERE sd.id  in :studentDegreeIds and s.faculty_id <> :facultyId", nativeQuery = true)
    List <Integer> findIdByIdsAndFacultyId(@Param("studentDegreeIds") List<Integer> studentDegreeIds, @Param("facultyId") int facultyId);

    @Query("SELECT sd from StudentDegree sd " +
            "where sd.active = :active " +
            "and sd.student.id = :studentId and sd.specialization.id = :specializationId ")
    StudentDegree findByStudentIdAndSpecialityId(
            @Param("active") boolean active,
            @Param("studentId") Integer studentId,
            @Param("specializationId") Integer specializationId
    );

    @Override
    List<StudentDegree> findAll(Specification<StudentDegree> spec);

    @Query("select sd from StudentDegree sd " +
            "where concat(sd.student.surname, ' ', sd.student.name, ' ', sd.student.patronimic) = :full_name " +
            "and sd.studentGroup.id = :group_id " +
            "and sd.active = true")
    List<StudentDegree> findByFullNameAndGroupId(@Param("full_name") String fullName, @Param("group_id") int groupId);

    @Query(value = "SELECT student_degree.id, student.surname, student.name, student.patronimic,\n" +
            "student_group.name, speciality.code, speciality.name, specialization.name,\n" +
            "course_name.name, knowledge_control.name, course.semester, \n" +
            "grade.points, grade.grade, grade.ects\n" +
            "FROM student\n" +
            "INNER JOIN student_degree ON student_degree.student_id = student.id\n" +
            "INNER JOIN specialization ON student_degree.specialization_id = specialization.id\n" +
            "INNER JOIN speciality ON specialization.speciality_id = speciality.id\n" +
            "INNER JOIN student_group ON student_degree.student_group_id = student_group.id\n" +
            "INNER JOIN courses_for_groups ON courses_for_groups.student_group_id = student_group.id\n" +
            "INNER JOIN course ON courses_for_groups.course_id = course.id\n" +
            "INNER JOIN course_name ON course.course_name_id = course_name.id\n" +
            "INNER JOIN knowledge_control ON course.kc_id = knowledge_control.id\n" +
            "LEFT JOIN  grade ON grade.student_degree_id = student_degree.id AND grade.course_id = course.id\n" +
            "WHERE student_group.id = 421 and student_degree.active=true and student_degree.payment='BUDGET' and (grade.points is null OR grade.points<60)\n" +
            "order by student.surname, student.name, student.patronimic, student.birth_date, semester, course_name.name", nativeQuery = true)
    List<Grade> findDebtorStudentDegrees(@Param("degreeId") int degreeId);

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET sd.thesisName = :thesisName, sd.thesisNameEng = :thesisNameEng, sd.thesisSupervisor = :thesisSupervisor WHERE sd.id = :idStudentDegree")
    void updateThesis(
            @Param("idStudentDegree") int idStudentDegree,
            @Param("thesisName") String thesisName,
            @Param("thesisNameEng") String thesisNameEng,
            @Param("thesisSupervisor") String thesisSupervisor);

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET sd.studentGroup = :group WHERE sd IN (:studentDegrees)")
    void assignStudentsToGroup(@Param("studentDegrees")List<StudentDegree> studentDegrees, @Param("group")StudentGroup group);

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET sd.recordBookNumber = :recordBookNumber WHERE sd.id = :studentDegreeId")
    void assignRecordBookNumbersToStudents(@Param("studentDegreeId") Integer studentDegreeId, @Param("recordBookNumber") String recordBookNumber);
}
