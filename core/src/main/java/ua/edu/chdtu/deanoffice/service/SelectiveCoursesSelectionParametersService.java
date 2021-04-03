package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesSelectionParameters;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesSelectionParametersRepository;

@Service
public class SelectiveCoursesSelectionParametersService {
    private SelectiveCoursesSelectionParametersRepository selectiveCoursesSelectionParametersRepository;

    @Autowired
    public SelectiveCoursesSelectionParametersService(SelectiveCoursesSelectionParametersRepository selectiveCoursesSelectionParametersRepository) {
        this.selectiveCoursesSelectionParametersRepository = selectiveCoursesSelectionParametersRepository;
    }

    public SelectiveCoursesSelectionParameters create(SelectiveCoursesSelectionParameters selectiveCoursesSelectionParameters) {
        return selectiveCoursesSelectionParametersRepository.save(selectiveCoursesSelectionParameters);
    }
}
