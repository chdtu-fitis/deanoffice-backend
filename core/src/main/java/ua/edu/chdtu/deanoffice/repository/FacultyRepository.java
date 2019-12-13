package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    @Query(value = "select f.id from student_degree sd " +
            "inner join student_group sg on sg.id = sd.student_group_id " +
            "inner join specialization s ON s.id = sg.specialization_id " +
            "inner join faculty f ON f.id = s.faculty_id " +
            "WHERE sd.id = :studentDegreeId", nativeQuery = true)
    Integer findIdByStudent(@Param("studentDegreeId") Integer studentDegreeId);

    @Query(value = "select f.id from student_expel se " +
            "inner join student_degree sd on sd.id=se.student_degree_id " +
            "inner join student_group sg on sg.id = sd.student_group_id " +
            "inner join specialization s ON s.id = sg.specialization_id " +
            "inner join faculty f ON f.id = s.faculty_id " +
            "WHERE se.id = :studentExpelId", nativeQuery = true)
    Integer findIdByStudentExpel(@Param("studentExpelId") Integer studentExpelId);

    @Query(value = "select f.id from student_group sg " +
            "inner join specialization s ON s.id = sg.specialization_id " +
            "inner join faculty f ON f.id = s.faculty_id " +
            "WHERE sg.id = :studentGroupId", nativeQuery = true)
    Integer findIdByGroup(@Param("studentGroupId") Integer studentGroupId);

    @Query(value = "select d.faculty_id from department d " +
            "WHERE d.id = :departmentId", nativeQuery = true)
    Integer findIdByDepartment(@Param("departmentId") Integer departmentId);

    @Query(value = "select d.faculty_id from teacher t " +
            "join department d on d.id = t.department_id " +
            "WHERE t.id = :teacherId", nativeQuery = true)
    Integer findIdByTeacher(@Param("teacherId") Integer teacherId);

    @Query(value = "select d.faculty_id from teacher t " +
            "join department d on d.id = t.department_id " +
            "WHERE t.id IN :teacherIds", nativeQuery = true)
    List<Integer> findIdByTeachers(@Param("teacherIds") List<Integer> teacherIds);

    @Query("select f from Faculty f where upper(f.name)=upper(:name)")
    Faculty findByName(@Param("name") String name);

    Faculty findById(@Param("id") Integer id);

    @Query("select f from Faculty f where f.active = true order by name")
    List<Faculty> findAllActive();
}
