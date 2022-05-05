package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.CourseName;

import java.util.List;

public interface CourseNameRepository extends JpaRepository<CourseName, Integer> {
    CourseName findByName(String name);

    @Query("select cn from CourseName as cn " +
            "where cn.id = :id")
    CourseName findCourseNameById(@Param("id") int id);

    @Query("select cn from CourseName as cn " +
            "where cn.id not in(select distinct c.courseName.id from Course as c)")
    List<CourseName> findUnusedCoursesNames();

    @Transactional
    void deleteCourseNameByIdIn(List<Integer> ids);

    @Transactional
    void deleteCourseNameById(int id);

    @Query(value = "select " +
            "cn.name as name, " +
            "sg.name||' Семестр: '||c.semester as message " +
            "from course_name cn " +
            "join course c on c.course_name_id = cn.id " +
            "join courses_for_groups cfg on cfg.course_id = c.id " +
            "join student_group sg on sg.id = cfg.student_group_id " +
            "join specialization s on s.id = sg.specialization_id " +
            "where sg.active = true and s.active = true and s.degree_id = :degree_id " +
            "and floor(sg.creation_year + sg.study_years - 0.1) = :year " +
            "and s.faculty_id = :faculty_id " +
            "and (cn.name_eng = '' or cn.name_eng is null) " +
            "order by cn.name", nativeQuery = true)
    List<Object[]> findAllForGraduatesWithNoEnglishName(
            @Param("year") int year,
            @Param("faculty_id") int facultyId,
            @Param("degree_id") int degreeId
    );

    @Query(value = "select " +
            "distinct cn.name as name, " +
            "'Семестр: '||c.semester ||' (вибіркова)' as message " +
            "from course_name cn " +
            "join course c on c.course_name_id = cn.id " +
            "join selective_course sc on sc.course_id = c.id " +
            "join selective_courses_student_degrees scsd ON scsd.selective_course_id = sc.id " +
            "where scsd.active = true and sc.degree_id = :degree_id " +
            "and sc.study_year between :year - :shift_year and :year " +
            "and (cn.name_eng = '' or cn.name_eng is null) " +
            "order by cn.name", nativeQuery = true)
    List<Object[]> findAllSelectiveForGraduatesWithNoEnglishName(
            @Param("year") int year,
            @Param("shift_year") int shiftYear,
            @Param("degree_id") int degreeId
    );
}
