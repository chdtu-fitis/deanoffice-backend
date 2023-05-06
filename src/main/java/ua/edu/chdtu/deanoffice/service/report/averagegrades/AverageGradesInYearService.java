package ua.edu.chdtu.deanoffice.service.report.averagegrades;


import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AverageGradesInYearService {
    private CurrentYearService currentYearService;
    private final GradeRepository gradeRepository;
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentDegreeService studentDegreeService;

    public AverageGradesInYearService(CurrentYearService currentYearService, GradeRepository gradeRepository,
                                      StudentDegreeRepository studentDegreeRepository,
                                      StudentDegreeService studentDegreeService) {
        this.currentYearService = currentYearService;
        this.gradeRepository = gradeRepository;
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentDegreeService = studentDegreeService;
    }

    public List<StudentsAveragePointsForYearBean> getAverageGradesInYear() {
        int currentYear = currentYearService.getYear();
        List<Object[]> rawData = studentDegreeRepository.findAverageGradesForYear(currentYear);

        List<StudentsAveragePointsForYearBean> studentsAveragePointsForYearBeans = new ArrayList<>(rawData.size());
        rawData.forEach(item -> {
                    BigDecimal averageGrade;
                    if (item[11] == null) {
                        averageGrade = BigDecimal.ZERO;
                    } else {
                        averageGrade = (BigDecimal) item[11];
                    }
                    studentsAveragePointsForYearBeans.add(new StudentsAveragePointsForYearBean(
                            (Integer) item[0]/*degreeId*/,
                            (String) item[1]/*surname*/,
                            (String) item[2]/*name*/,
                            (String) item[3]/*patronimic*/,
                            (String) item[4]/*degreeName*/,
                            (String) item[5]/*groupName*/,
                            (Integer) item[6]/*year*/,
                            (String) item[7]/*tuitionTerm*/,
                            (String) item[8]/*specialityCode*/,
                            (String) item[9]/*specialityName*/,
                            (String) item[10]/*specializationName*/,
                            averageGrade));/*AverageGrade*/
                }
        );

        return studentsAveragePointsForYearBeans;
    }
}