package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import java.util.List;

public interface SelectiveCourseRepository extends JpaRepository<SelectiveCourse, Integer> {

    @Query("SELECT sc FROM SelectiveCourse sc WHERE sc.available = true AND sc.studyYear = :studyYear")
    List<SelectiveCourse> findAllAvailableByStudyYear(
            @Param("studyYear") Integer studyYear
    );
}
