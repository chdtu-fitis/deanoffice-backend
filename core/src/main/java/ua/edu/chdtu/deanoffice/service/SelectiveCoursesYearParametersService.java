package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesSelectionParametersRepository;

@Service
public class SelectiveCoursesYearParametersService {
    private SelectiveCoursesSelectionParametersRepository selectiveCoursesSelectionParametersRepository;

    @Autowired
    public SelectiveCoursesYearParametersService(SelectiveCoursesSelectionParametersRepository selectiveCoursesSelectionParametersRepository) {
        this.selectiveCoursesSelectionParametersRepository = selectiveCoursesSelectionParametersRepository;
    }

    public SelectiveCoursesYearParameters create(SelectiveCoursesYearParameters selectiveCoursesYearParameters) {
        return selectiveCoursesSelectionParametersRepository.save(selectiveCoursesYearParameters);
    }
}
