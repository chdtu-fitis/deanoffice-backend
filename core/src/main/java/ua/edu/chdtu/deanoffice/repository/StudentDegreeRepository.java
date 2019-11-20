package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
            "ORDER BY sg.tuition_form DESC, sg.name, st.surname, st.name, st.patronimic", nativeQuery = true)
    List<StudentDegree> findAllGraduates(
            @Param("year") int year,
            @Param("faculty_id") int facultyId,
            @Param("degree_id") int degreeId
    );

    @Query(value = "select sd.id from student_degree sd " +
            "inner join specialization s ON s.id = sd.specialization_id " +
            "WHERE sd.id in :studentDegreeIds and s.faculty_id <> :facultyId", nativeQuery = true)
    List<Integer> findIdsByIdsAndFacultyId(@Param("studentDegreeIds") List<Integer> studentDegreeIds, @Param("facultyId") int facultyId);

    @Query(value = "select sd from StudentDegree sd " +
            "WHERE sd.id in :studentDegreeIds and sd.specialization.faculty.id = :facultyId")
    List<StudentDegree> findActiveByIdsAndFacultyId(@Param("studentDegreeIds") List<Integer> studentDegreeIds, @Param("facultyId") int facultyId);

    @Query("SELECT sd from StudentDegree sd " +
            "where sd.active = :active " +
            "and sd.student.id = :studentId and sd.specialization.id = :specializationId ")
    StudentDegree findByStudentIdAndSpecialityId(
            @Param("active") boolean active,
            @Param("studentId") Integer studentId,
            @Param("specializationId") Integer specializationId
    );

    @Query(value = "SELECT count(sd.id) FROM student_degree sd " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN specialization s ON sd.specialization_id = s.id " +
            "WHERE sd.payment = :payment AND sd.active = true and sg.tuition_form = 'FULL_TIME'" +
            "AND sd.specialization_id = :specializationId AND (:currentYear - sg.creation_year + sg.begin_years) = :studyYear " +
            "AND s.degree_id = :degreeId", nativeQuery = true)
    int findCountAllActiveFulltimeStudentsBySpecializationIdAndStudyYearAndPayment(
            @Param("specializationId") int specializationId,
            @Param("currentYear") int currentYear,
            @Param("studyYear") int studyYear,
            @Param("payment") String payment,
            @Param("degreeId") int degreeId
    );

    //TODO неперевірений
    @Query(value = "SELECT count(DISTINCT sd.id) FROM student_degree sd " +
            "INNER JOIN grade g ON sd.id = g.student_degree_id " +
            "INNER JOIN courses_for_groups cfg ON sd.student_group_id = cfg.student_group_id AND g.course_id = cfg.course_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN specialization s ON sd.specialization_id = s.id " +
            "WHERE (g.points is null OR g.points < 60) AND sd.payment = :payment AND sd.active = true " +
            "AND sd.specialization_id = :specializationId AND (:currentYear - sg.creation_year + sg.begin_years) = :studyYear " +
            "AND sg.tuition_form = :tuitionForm " +
            "AND s.degree_id = :degreeId", nativeQuery = true)
    int findCountAllActiveDebtorsBySpecializationIdAndStudyYearAndTuitionFormAndPayment(
            @Param("specializationId") int specializationId,
            @Param("currentYear") int currentYear,
            @Param("studyYear") int studyYear,
            @Param("tuitionForm") String tuitionForm,
            @Param("payment") String payment,
            @Param("degreeId") int degreeId
    );

    //TODO неперевірений
    @Query(value = "SELECT count(sd.id) FROM student_degree sd " +
            "INNER JOIN grade g ON sd.id = g.student_degree_id " +
            "INNER JOIN courses_for_groups cfg ON sd.student_group_id = cfg.student_group_id AND g.course_id = cfg.course_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN specialization s ON sd.specialization_id = s.id " +
            "WHERE (g.points is null OR points < 60) AND sd.payment = :payment AND sd.active = true " +
            "AND sd.specialization_id = :specializationId " +
            "AND (:currentYear - sg.creation_year + sg.begin_years) = :studyYear " +
            "AND sg.tuition_form = :tuitionForm " +
            "AND s.degree_id = :degreeId " +
            "GROUP BY sd.id HAVING count (sd.id) < 3", nativeQuery = true)
    int[] findAllActiveDebtorsWithLessThanThreeDebs(
            @Param("specializationId") int specializationId,
            @Param("currentYear") int currentYear,
            @Param("studyYear") int studyYear,
            @Param("tuitionForm") String tuitionForm,
            @Param("payment") String payment,
            @Param("degreeId") int degreeId
    );

    //TODO неперевірений
    @Query(value = "SELECT count(sd.id) FROM student_degree sd " +
            "INNER JOIN grade g ON sd.id = g.student_degree_id " +
            "INNER JOIN courses_for_groups cfg ON sd.student_group_id = cfg.student_group_id AND g.course_id = cfg.course_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN specialization s ON sd.specialization_id = s.id " +
            "WHERE (g.points is null OR points < 60) AND sd.payment = :payment AND sd.active = true " +
            "AND sd.specialization_id = :specializationId " +
            "AND (:currentYear - sg.creation_year + sg.begin_years) = :studyYear " +
            "AND s.degree_id = :degreeId " +
            "AND sg.tuition_form = :tuitionForm " +
            "GROUP BY sd.id HAVING count (sd.id) > 2", nativeQuery = true)
    int[] findAllActiveDebtorsWithThreeOrMoreDebts(
            @Param("specializationId") int specializationId,
            @Param("currentYear") int currentYear,
            @Param("studyYear") int studyYear,
            @Param("tuitionForm") String tuitionForm,
            @Param("payment") String payment,
            @Param("degreeId") int degreeId
    );

    @Query("SELECT sd FROM StudentDegree sd " +
            "WHERE sd.active = true " +
            "AND sd.supplementNumber = :supplementNumper")
    List<StudentDegree> findBySupplementNumber(@Param("supplementNumper") String supplementNumper);

    @Override
    List<StudentDegree> findAll(Specification<StudentDegree> spec);

    @Query("select sd from StudentDegree sd " +
            "where concat(sd.student.surname, ' ', sd.student.name, ' ', sd.student.patronimic) = :full_name " +
            "and sd.studentGroup.id = :group_id " +
            "and sd.active = true")
    List<StudentDegree> findByFullNameAndGroupId(@Param("full_name") String fullName, @Param("group_id") int groupId);

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
    void assignStudentsToGroup(@Param("studentDegrees") List<StudentDegree> studentDegrees, @Param("group") StudentGroup group);

    @Modifying
    @Query(value = "UPDATE student_degree sd " +
            "SET active = false WHERE sd.id IN (:ids)", nativeQuery = true)
    void setStudentDegreesInactive(@Param("ids") List<Integer> ids);

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET sd.recordBookNumber = :recordBookNumber WHERE sd.id = :studentDegreeId")
    void assignRecordBookNumbersToStudents(@Param("studentDegreeId") Integer studentDegreeId, @Param("recordBookNumber") String recordBookNumber);

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET sd.diplomaNumber = :diplomaNumber, sd.diplomaWithHonours = :diplomaWithHonours," +
            " sd.diplomaDate = :diplomaDate," +
            " sd.supplementDate = :supplementDate " +
            "WHERE sd.id = :studentDegreeId")
    void updateDiplomaNumber(
            @Param("studentDegreeId") int studentDegreeId,
            @Param("diplomaNumber") String diplomaNumber,
            @Param("diplomaWithHonours") boolean diplomaWithHonours,
            @Param("diplomaDate") Date diplomaDate,
            @Param("supplementDate") Date supplementDate);

    @Query(value =
            "SELECT student_degree.id, " +
                    "       student.surname, " +
                    "       student.name, " +
                    "       student.patronimic, " +
                    "       degree.name                                                    as degreeName, " +
                    "       student_group.name                                             as groupName, " +
                    "       :currentYear - student_group.creation_year + student_group.begin_years as year, " +
                    "       student_group.tuition_term                                     as tuitionTerm, " +
                    "       speciality.code                                                as specialityCode, " +
                    "       speciality.name                                                as specialityName, " +
                    "       specialization.name                                            as specializationName, " +
                    "       department.abbr                                                as departmentAbbreviation, " +
                    "       grade.points                                                   as averageGrade, " +
                    "       course_name.name                                               as courseName, " +
                    "       knowledge_control.name                                         as knowledgeControlName, " +
                    "       course.semester " +
                    "FROM student " +
                    "       INNER JOIN student_degree ON student_degree.student_id = student.id " +
                    "       INNER JOIN specialization ON student_degree.specialization_id = specialization.id " +
                    "       INNER JOIN speciality ON specialization.speciality_id = speciality.id " +
                    "       INNER JOIN degree ON specialization.degree_id = degree.id " +
                    "       INNER JOIN student_group ON student_degree.student_group_id = student_group.id " +
                    "       INNER JOIN courses_for_groups ON courses_for_groups.student_group_id = student_group.id " +
                    "       INNER JOIN course ON courses_for_groups.course_id = course.id " +
                    "       INNER JOIN course_name ON course.course_name_id = course_name.id " +
                    "       INNER JOIN knowledge_control ON course.kc_id = knowledge_control.id " +
                    "       INNER JOIN department ON specialization.department_id = department.id " +
                    "       INNER JOIN grade ON grade.student_degree_id = student_degree.id AND grade.course_id = course.id " +
                    "WHERE specialization.faculty_id = :facultyId " +
                    "  AND student_degree.active = true " +
                    "  AND student_group.tuition_form = 'FULL_TIME' " +
                    "  AND student_degree.payment = 'BUDGET' " +
                    "  AND (grade.points IS NULL OR grade.points < 60 OR (grade.on_time = false AND course.semester = (:currentYear - student_group.creation_year + student_group.begin_years) * 2 - 2 + :semester)) " +
                    "  AND grade.academic_difference = false " +
                    "  AND course.semester <= (:currentYear - student_group.creation_year + student_group.begin_years) * 2 - 2 + :semester " +
                    "ORDER BY degree.id, speciality.code, student_group.name, student.surname, student.name, student.patronimic, student.birth_date, semester, course_name.name", nativeQuery = true)
    List<Object[]> findDebtorStudentDegreesRaw(
            @Param("facultyId") int facultyId,
            @Param("semester") int semester,
            @Param("currentYear") int currentYear);


    @Query(value = "SELECT student_degree.id, \n" +
            "student.surname, \n" +
            "student.name, \n" +
            "student.patronimic, \n" +
            "degree.name as degreeName, \n" +
            "student_group.name as groupName, \n" +
            ":currentYear - student_group.creation_year + student_group.begin_years as year, \n" +
            "student_group.tuition_term as tuitionTerm, \n" +
            "speciality.code as specialityCode, \n" +
            "speciality.name as specialityName, \n" +
            "specialization.name as specializationName, \n" +
            "avg(grade.points) as averageGrade \n" +
            "FROM student \n" +
            "INNER JOIN student_degree ON student_degree.student_id = student.id \n" +
            "INNER JOIN specialization ON student_degree.specialization_id = specialization.id\n" +
            "INNER JOIN speciality ON specialization.speciality_id = speciality.id \n" +
            "INNER JOIN degree ON specialization.degree_id = degree.id \n" +
            "INNER JOIN student_group ON student_degree.student_group_id = student_group.id\n" +
            "INNER JOIN courses_for_groups ON courses_for_groups.student_group_id = student_group.id \n" +
            "INNER JOIN course ON courses_for_groups.course_id = course.id \n" +
            "INNER JOIN course_name ON course.course_name_id = course_name.id \n" +
            "INNER JOIN knowledge_control ON course.kc_id = knowledge_control.id \n" +
            "INNER JOIN grade ON grade.student_degree_id = student_degree.id AND grade.course_id = course.id\n" +
            "WHERE (:currentYear-creation_year + begin_years)*2-1=course.semester or (:currentYear-creation_year + begin_years)*2=course.semester \n" +
            "AND student_group.active=TRUE AND knowledge_control.graded=TRUE \n" +
            "GROUP BY student_degree.id, student.surname, student.name, student.patronimic,\n" +
            "degreeName, groupName, year, tuitionTerm, specialityCode, specialityName, specializationName \n" +
            "ORDER BY degreeName, specialityCode, groupName, student.surname, student.name, student.patronimic\n", nativeQuery = true)
    List<Object[]> findAverageGradesForYear(
            @Param("currentYear") int currentYear
    );

    @Query(value =
            "SELECT student_degree.id, student.surname, student.name, student.patronimic, " +
                    "       degree.name as degreeName, student_group.name as groupName, " +
                    "       :currentYear - student_group.creation_year + student_group.begin_years as year, " +
                    "       student_group.tuition_term as tuitionTerm, speciality.code as specialityCode, " +
                    "       speciality.name as specialityName, specialization.name as specializationName, " +
                    "       department.abbr as departmentAbbreviation, avg(grade.points) as averageGrade, " +
                    "       extra_points.points as extraPoints " +
                    "FROM student " +
                    "       INNER JOIN student_degree ON student_degree.student_id = student.id " +
                    "       INNER JOIN specialization ON student_degree.specialization_id = specialization.id " +
                    "       INNER JOIN speciality ON specialization.speciality_id = speciality.id " +
                    "       INNER JOIN degree ON specialization.degree_id = degree.id " +
                    "       INNER JOIN student_group ON student_degree.student_group_id = student_group.id " +
                    "       INNER JOIN courses_for_groups ON courses_for_groups.student_group_id = student_group.id " +
                    "       INNER JOIN course ON courses_for_groups.course_id = course.id " +
                    "       INNER JOIN course_name ON course.course_name_id = course_name.id " +
                    "       INNER JOIN knowledge_control ON course.kc_id = knowledge_control.id " +
                    "       INNER JOIN department ON specialization.department_id = department.id " +
                    "       INNER JOIN grade ON grade.student_degree_id = student_degree.id AND grade.course_id = course.id " +
                    "       FULL JOIN extra_points ON extra_points.student_degree_id = student_degree.id AND extra_points.semester = (:currentYear - student_group.creation_year + student_group.begin_years) * 2 - 2 + :semester " +
                    "WHERE specialization.faculty_id = :facultyId " +
                    "  AND student_degree.active = true " +
                    "  AND student_group.tuition_form = 'FULL_TIME' " +
                    "  AND student_degree.payment = 'BUDGET' " +
                    "  AND student_degree.id NOT IN (:debtorStudentDegreeIds) " +
                    "  AND course.semester = (:currentYear - student_group.creation_year + student_group.begin_years) * 2 - 2 + :semester " +
                    "  AND knowledge_control.graded = true " +
                    "  AND grade.academic_difference = false " +
                    "GROUP BY student_degree.id, student.surname, student.name, student.patronimic, " +
                    "degreeName, groupName, year, tuitionTerm, specialityCode, specialityName, specializationName, departmentAbbreviation, extraPoints " +
                    "ORDER BY degreeName, specialityCode, groupName, student.surname, student.name, student.patronimic", nativeQuery = true)
    List<Object[]> findNoDebtStudentDegreesRaw(
            @Param("facultyId") int facultyId,
            @Param("debtorStudentDegreeIds") Set<Integer> debtorStudentDegreeIds,
            @Param("semester") int semester,
            @Param("currentYear") int currentYear);

    @Query(value = "SELECT count(sd.id) FROM student_degree sd WHERE sd.id IN (:ids) AND sd.active = false", nativeQuery = true)
    int countInactiveStudentDegreesByIds(@Param("ids") List<Integer> ids);

    @Query(value = "SELECT s.surname, s.name AS studentName, s.patronimic, sd.diploma_number, sg.name AS groupName " +
            "FROM student_degree sd " +
            "INNER JOIN student s ON sd.student_id = s.id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "WHERE sg.id IN (:studentGroupIds) AND sd.active = true " +
            "ORDER BY sg.name, s.surname, s.name, s.patronimic", nativeQuery = true)
    List<Object[]> getStudentDegreeShortFields(@Param("studentGroupIds") List<Integer> studentGroupIds);

    @Query(value = "select ep from ExtraPoints ep " +
            "where ep.studentDegree.id = :studentDegreeId " +
            "and ep.semester = :semester ")
    ExtraPoints getExtraPointsByStudentDegreeId(
            @Param("studentDegreeId") Integer studentDegreeId,
            @Param("semester") Integer semester
    );

    @Query(value = "select (:currYear - sd.studentGroup.creationYear + sd.studentGroup.beginYears)*2-2+:semester from StudentDegree sd " +
            "where sd.id = :studentDegreeId ")
    Integer getSemester(@Param("currYear") Integer currYear,
                        @Param("studentDegreeId") Integer studentDegreeId,
                        @Param("semester") int semester);

    ExtraPoints save(ExtraPoints extraPoints);

    @Query(value = "SELECT count(sd.id) FROM student_degree sd " +
            "INNER JOIN grade g ON sd.id = g.student_degree_id " +
            "INNER JOIN courses_for_groups cfg ON sd.student_group_id = cfg.student_group_id AND g.course_id = cfg.course_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN specialization s ON sd.specialization_id = s.id " +
            "INNER JOIN course on g.course_id = course.id " +
            "WHERE (g.points is null OR points < 60) AND sd.payment = :payment AND sd.active = true " +
            "AND sd.specialization_id = :specializationId " +
            "AND (:currentYear - sg.creation_year + sg.begin_years) = :studyYear " +
            "AND s.degree_id = :degreeId " +
            "AND sg.tuition_form = :tuitionForm " +
            "AND course.semester = (((:studyYear - 1) * 2) + :semester) " +
            "GROUP BY sd.id HAVING count (sd.id) > 2", nativeQuery = true)
    int[] findAllActiveDebtorsWithThreeOrMoreDebts(
            @Param("specializationId") int specializationId,
            @Param("currentYear") int currentYear,
            @Param("studyYear") int studyYear,
            @Param("tuitionForm") String tuitionForm,
            @Param("payment") String payment,
            @Param("degreeId") int degreeId,
            @Param("semester") int semester
    );

    @Query(value = "SELECT count(sd.id) FROM student_degree sd " +
            "INNER JOIN grade g ON sd.id = g.student_degree_id " +
            "INNER JOIN courses_for_groups cfg ON sd.student_group_id = cfg.student_group_id AND g.course_id = cfg.course_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN specialization s ON sd.specialization_id = s.id " +
            "INNER JOIN course on cfg.course_id = course.id " +
            "WHERE (g.points is null OR points < 60) AND sd.payment = :payment AND sd.active = true " +
            "AND sd.specialization_id = :specializationId " +
            "AND (:currentYear - sg.creation_year + sg.begin_years) = :studyYear " +
            "AND sg.tuition_form = :tuitionForm " +
            "AND s.degree_id = :degreeId " +
            "AND course.semester = (((:studyYear - 1) * 2) + :semester) " +
            "GROUP BY sd.id HAVING count (sd.id) < 3", nativeQuery = true)
    int[] findAllActiveDebtorsWithLessThanThreeDebs(
            @Param("specializationId") int specializationId,
            @Param("currentYear") int currentYear,
            @Param("studyYear") int studyYear,
            @Param("tuitionForm") String tuitionForm,
            @Param("payment") String payment,
            @Param("degreeId") int degreeId,
            @Param("semester") int semester
    );

    @Query(value = "SELECT count(DISTINCT sd.id) FROM student_degree sd " +
            "INNER JOIN grade g ON sd.id = g.student_degree_id " +
            "INNER JOIN courses_for_groups cfg ON sd.student_group_id = cfg.student_group_id AND g.course_id = cfg.course_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN specialization s ON sd.specialization_id = s.id " +
            "INNER JOIN course on cfg.course_id = course.id " +
            "WHERE (g.points is null OR g.points < 60) AND sd.payment = :payment AND sd.active = true " +
            "AND sd.specialization_id = :specializationId AND (:currentYear - sg.creation_year + sg.begin_years) = :studyYear " +
            "AND course.semester = (((:studyYear - 1) * 2) + :semester) " +
            "AND sg.tuition_form = :tuitionForm " +
            "AND s.degree_id = :degreeId", nativeQuery = true)
    int findCountAllActiveDebtorsBySpecializationIdAndStudyYearAndTuitionFormAndPayment(
            @Param("specializationId") int specializationId,
            @Param("currentYear") int currentYear,
            @Param("studyYear") int studyYear,
            @Param("tuitionForm") String tuitionForm,
            @Param("payment") String payment,
            @Param("degreeId") int degreeId,
            @Param("semester") int semester
    );
}
