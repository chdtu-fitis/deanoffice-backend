package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;

@Repository
public interface SelectiveCoursesSelectionParametersRepository extends JpaRepository<SelectiveCoursesYearParameters, Integer> {
}
