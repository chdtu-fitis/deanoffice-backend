package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesYearParametersRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import static ua.edu.chdtu.deanoffice.util.DateUtil.getYearInt;
import static ua.edu.chdtu.deanoffice.util.DateUtil.setDateHours;

@Service
public class SelectiveCoursesYearParametersService {
    private SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository;
    private CurrentYearService currentYearService;

    @Autowired
    public SelectiveCoursesYearParametersService(SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository,
                                                 CurrentYearService currentYearService) {
        this.selectiveCoursesYearParametersRepository = selectiveCoursesYearParametersRepository;
        this.currentYearService = currentYearService;
    }

    public List<SelectiveCoursesYearParameters> getSelectiveCoursesYearParametersByYear(int year) {
        return selectiveCoursesYearParametersRepository.findAllByYear(year);
    }

    @Transactional
    public List<SelectiveCoursesYearParameters> create(List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersList) throws OperationCannotBePerformedException {
        List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersAfterSave = new ArrayList<>();
        for (SelectiveCoursesYearParameters selectiveCoursesYearParameters : selectiveCoursesYearParametersList) {
            Date firstRoundStartDate = setDateHours(selectiveCoursesYearParameters.getFirstRoundStartDate(), 0, 1);
            Date firstRoundEndDate = setDateHours(selectiveCoursesYearParameters.getFirstRoundEndDate(), 23, 59);
            Date secondRoundStartDate = setDateHours(selectiveCoursesYearParameters.getSecondRoundStartDate(), 0, 1);
            Date secondRoundEndDate = setDateHours(selectiveCoursesYearParameters.getSecondRoundEndDate(), 23, 59);

            int firstRoundStartDateYear = getYearInt(firstRoundStartDate);
            int firstRoundEndDateYear = getYearInt(firstRoundEndDate);
            int secondRoundStartDateYear = getYearInt(secondRoundStartDate);
            int secondRoundEndDateYear = getYearInt(secondRoundEndDate);

            selectiveCoursesYearParameters.setStudyYear(currentYearService.getYear());
            selectiveCoursesYearParameters.setFirstRoundStartDate(firstRoundStartDate);
            selectiveCoursesYearParameters.setFirstRoundEndDate(firstRoundEndDate);
            selectiveCoursesYearParameters.setSecondRoundStartDate(secondRoundStartDate);
            selectiveCoursesYearParameters.setSecondRoundEndDate(secondRoundEndDate);

            if (!(firstRoundStartDateYear == firstRoundEndDateYear
                    && firstRoundStartDateYear == secondRoundStartDateYear
                    && firstRoundStartDateYear == secondRoundEndDateYear))
                throw new OperationCannotBePerformedException("Роки в датах повинні бути однакові");
            else if (firstRoundStartDate.before(firstRoundEndDate)
                    && firstRoundEndDate.before(secondRoundStartDate)
                    && secondRoundStartDate.before(secondRoundEndDate))
                selectiveCoursesYearParametersAfterSave.add(selectiveCoursesYearParametersRepository.save(selectiveCoursesYearParameters));
            else
                throw new OperationCannotBePerformedException("Дати повинні йти за правилом 'дата початку першого туру < дата закінчення першого туру і початку другого < дата закінчення другого туру'");
        }

        return selectiveCoursesYearParametersAfterSave;
    }
}
