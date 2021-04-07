package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;

public interface SelectiveCoursesYearParametersRepository extends JpaRepository<SelectiveCoursesYearParameters, Integer> {

    @Query("SELECT scyp FROM SelectiveCoursesYearParameters AS scyp WHERE scyp.dateYear = :year")
    SelectiveCoursesYearParameters findByYear(@Param("year") Integer year);
}
