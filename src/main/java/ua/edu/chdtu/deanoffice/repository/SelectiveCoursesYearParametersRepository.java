package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;

import java.util.List;

public interface SelectiveCoursesYearParametersRepository extends JpaRepository<SelectiveCoursesYearParameters, Integer> {

    @Query("SELECT scyp FROM SelectiveCoursesYearParameters AS scyp WHERE scyp.studyYear = :year")
    List<SelectiveCoursesYearParameters> findAllByYear(@Param("year") Integer year);
}
