package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesYearParametersRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

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

    public List<SelectiveCoursesYearParameters> create(List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersList) throws OperationCannotBePerformedException {
        List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersAfterSave = new ArrayList<>();
        for (SelectiveCoursesYearParameters selectiveCoursesYearParameters : selectiveCoursesYearParametersList) {
            Date firstRoundStartDate = selectiveCoursesYearParameters.getFirstRoundStartDate();
            Date firstRoundEndDate = selectiveCoursesYearParameters.getFirstRoundEndDate();
            Date secondRoundEndDate = selectiveCoursesYearParameters.getSecondRoundEndDate();

            int firstRoundStartDateYear = getDateYear(firstRoundStartDate);
            int firstRoundEndDateYear = getDateYear(firstRoundEndDate);
            int secondRoundEndDateYear = getDateYear(secondRoundEndDate);

            selectiveCoursesYearParameters.setStudyYear(currentYearService.getYear());

            if (!(firstRoundStartDateYear == firstRoundEndDateYear && firstRoundStartDateYear == secondRoundEndDateYear))
                throw new OperationCannotBePerformedException("Роки в датах повинні бути однакові");
            else if (firstRoundStartDate.before(firstRoundEndDate) && firstRoundEndDate.before(secondRoundEndDate))
                selectiveCoursesYearParametersAfterSave.add(selectiveCoursesYearParametersRepository.save(selectiveCoursesYearParameters));
            else
                throw new OperationCannotBePerformedException("Дати повинні йти за правилом 'дата початку першого туру < дата закінчення першого туру і початку другого < дата закінчення другого туру'");
        }

        return selectiveCoursesYearParametersAfterSave;
    }

    private int getDateYear(Date targetDate) {
        Date date = targetDate;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Kiev"));
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return year;
    }
}
