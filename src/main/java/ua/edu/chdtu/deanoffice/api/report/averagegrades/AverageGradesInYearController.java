package ua.edu.chdtu.deanoffice.api.report.averagegrades;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.report.averagegrades.dto.StudentsAveragePointsForYearDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.report.averagegrades.AverageGradesInYearService;
import ua.edu.chdtu.deanoffice.service.report.averagegrades.StudentsAveragePointsForYearBean;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/report/averagegrades")
public class AverageGradesInYearController {

    private AverageGradesInYearService averageGradesInYearService;

    @Autowired
    public AverageGradesInYearController(AverageGradesInYearService averageGradesInYearService) {
        this.averageGradesInYearService = averageGradesInYearService;
    }

    @GetMapping
    public ResponseEntity getAverageGrades(
            @CurrentUser ApplicationUser user
    ) {
        List<StudentsAveragePointsForYearBean> averageGrades = averageGradesInYearService.getAverageGradesInYear();
        return ResponseEntity.ok(Mapper.map(averageGrades, StudentsAveragePointsForYearDTO.class));
    }
}
