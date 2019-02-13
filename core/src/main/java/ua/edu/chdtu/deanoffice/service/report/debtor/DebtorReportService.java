package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.repository.SpecializationRepository;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DebtorReportService {
    private static final int NUMBER_OF_YEARS = 6;

    private SpecializationRepository specializationRepository;
    private StudentDegreeService studentDegreeService;
    private StudentGroupService studentGroupService;

    @Autowired
    public DebtorReportService(SpecializationRepository specializationRepository,
                               StudentGroupService studentGroupService,
                               StudentDegreeService studentDegreeService) {
        this.specializationRepository = specializationRepository;
        this.studentGroupService = studentGroupService;
        this.studentDegreeService = studentDegreeService;
    }

    public Map<String, SpecializationDebtorsBean> calculateDebtorsReportData(Faculty faculty) {
        Map<String, SpecializationDebtorsBean> debtorsReport = new TreeMap<>();
        List<Specialization> specializations = specializationRepository.findAllByActive(true, faculty.getId());
        for (Specialization specialization : specializations) {
            if (specialization.getName().equals("")) {
                continue;
            }
            Map<Integer, SpecializationDebtorsYearBean> specializationDebtorsYearBeanMap = new TreeMap<>();

            for (int year = 1; year <= NUMBER_OF_YEARS; year++) {
                int budgetStudents = studentDegreeService.getCountAllActiveStudents(specialization.getId(), getCorrectYear(year), Payment.BUDGET, getDegreeIdByYear(year));
                int contractStudents = studentDegreeService.getCountAllActiveStudents(specialization.getId(), getCorrectYear(year), Payment.CONTRACT, getDegreeIdByYear(year));

                if (budgetStudents + contractStudents == 0) {
                    continue;
                }

                int budgetDebtors = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                int contractDebtors = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));
                double debtorsPercent = (budgetDebtors + contractDebtors) / (budgetStudents * 1.0 + contractStudents) * 100;
                int lessThanThreeDebtsForBudgetDebtors = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                int lessThanThreeDebtsForContractDebtors = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));
                int threeOrMoreDebtsForBudgetDebtors = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                int threeOrMoreDebtsForContractDebtors = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));

                SpecializationDebtorsYearBean specializationDebtorsYearBean
                    = new SpecializationDebtorsYearBean(budgetStudents, contractStudents, budgetDebtors,
                                                        contractDebtors, debtorsPercent, lessThanThreeDebtsForBudgetDebtors,
                                                        lessThanThreeDebtsForContractDebtors, threeOrMoreDebtsForBudgetDebtors,
                                                        threeOrMoreDebtsForContractDebtors);

                specializationDebtorsYearBeanMap.put(year, specializationDebtorsYearBean);
            }

            if (specializationDebtorsYearBeanMap.size() == 0) {
                continue;
            }

            String specializationName = specialization.getName();

            if (debtorsReport.get(specializationName) != null) {
                debtorsReport.get(specializationName).getSpecializationDebtorsYearBeanMap().putAll(specializationDebtorsYearBeanMap);
            } else {
                SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
                specializationDebtorsBean.setSpecializationDebtorsYearBeanMap(specializationDebtorsYearBeanMap);
                debtorsReport.put(specializationName, specializationDebtorsBean);
            }
        }

        calculateAllDataOfSpecialization(debtorsReport);
        calculateAllDataOfFacultyForEachYear(debtorsReport, faculty);

        return debtorsReport;
    }

    private void calculateAllDataOfSpecialization(Map<String, SpecializationDebtorsBean> debtorsReport) {
        for (Map.Entry<String, SpecializationDebtorsBean> specialization: debtorsReport.entrySet()) {

            Map<Integer, SpecializationDebtorsYearBean> specializationDebtorsYearBeanMap = specialization.getValue().getSpecializationDebtorsYearBeanMap();

            int allBudgetStudent = 0;
            int allContractStudent = 0;
            int allBudgetDebtors = 0;
            int allContractDebtors = 0;
            int allBudgetDebtorsWithLessThanThreeDebts = 0;
            int allContractDebtorsWithLessThanThreeDebts = 0;
            int allBudgetDebtorsWithThreeOrMoreDebts = 0;
            int allContractDebtorsWithThreeOrMoreDebts = 0;

            for (Map.Entry<Integer, SpecializationDebtorsYearBean> entry: specializationDebtorsYearBeanMap.entrySet()) {
                SpecializationDebtorsYearBean specializationDebtorsYearBean = entry.getValue();
                allBudgetStudent += specializationDebtorsYearBean.getBudgetStudents();
                allContractStudent += specializationDebtorsYearBean.getContractStudents();
                allBudgetDebtors += specializationDebtorsYearBean.getBudgetDebtors();
                allContractDebtors += specializationDebtorsYearBean.getContractDebtors();
                allBudgetDebtorsWithLessThanThreeDebts += specializationDebtorsYearBean.getLessThanThreeDebtsForBudgetDebtors();
                allContractDebtorsWithLessThanThreeDebts += specializationDebtorsYearBean.getLessThanThreeDebtsForContractDebtors();
                allBudgetDebtorsWithThreeOrMoreDebts += specializationDebtorsYearBean.getThreeOrMoreDebtsForBudgetDebtors();
                allContractDebtorsWithThreeOrMoreDebts += specializationDebtorsYearBean.getThreeOrMoreDebtsForContractDebtors();
            }

            SpecializationDebtorsYearBean specializationDebtorsForAllYearsBean
                    = new SpecializationDebtorsYearBean(allBudgetStudent, allContractStudent, allBudgetDebtors,
                    allContractDebtors, (allBudgetDebtors + allContractDebtors) * 1.0 / (allBudgetStudent + allContractStudent) * 100,
                    allBudgetDebtorsWithLessThanThreeDebts, allContractDebtorsWithLessThanThreeDebts,
                    allBudgetDebtorsWithThreeOrMoreDebts, allContractDebtorsWithThreeOrMoreDebts);

            debtorsReport.get(specialization.getKey()).getSpecializationDebtorsYearBeanMap().put(NUMBER_OF_YEARS + 1, specializationDebtorsForAllYearsBean);
        }
    }

    private void calculateAllDataOfFacultyForEachYear(Map<String, SpecializationDebtorsBean> debtorsReport, Faculty faculty) {
        Map<Integer, SpecializationDebtorsYearBean> allSpecializationDebtorsYearBeanMap = new TreeMap<>();

        for (int year = 1; year <= NUMBER_OF_YEARS; year++) {
            int allBudgetStudentOfCurrentFaculty = 0;
            int allContractStudentOfCurrentFaculty = 0;
            int allBudgetDebtorsOfCurrentFaculty = 0;
            int allContractDebtorsOfCurrentFaculty = 0;
            int allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFaculty = 0;
            int allContractDebtorsWithLessThanThreeDebtsOfCurrentFaculty = 0;
            int allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFaculty = 0;
            int allContractDebtorsWithThreeOrMoreDebtsOfCurrentFaculty = 0;

            for (Map.Entry<String, SpecializationDebtorsBean> entry: debtorsReport.entrySet()) {
                SpecializationDebtorsYearBean specializationDebtorsYearBean = entry.getValue().getSpecializationDebtorsYearBeanMap().get(year);
                if (specializationDebtorsYearBean == null) {
                    continue;
                }
                allBudgetStudentOfCurrentFaculty += specializationDebtorsYearBean.getBudgetStudents();
                allContractStudentOfCurrentFaculty += specializationDebtorsYearBean.getContractStudents();
                allBudgetDebtorsOfCurrentFaculty += specializationDebtorsYearBean.getBudgetDebtors();
                allContractDebtorsOfCurrentFaculty += specializationDebtorsYearBean.getContractDebtors();
                allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFaculty += specializationDebtorsYearBean.getLessThanThreeDebtsForBudgetDebtors();
                allContractDebtorsWithLessThanThreeDebtsOfCurrentFaculty += specializationDebtorsYearBean.getLessThanThreeDebtsForContractDebtors();
                allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFaculty += specializationDebtorsYearBean.getThreeOrMoreDebtsForBudgetDebtors();
                allContractDebtorsWithThreeOrMoreDebtsOfCurrentFaculty += specializationDebtorsYearBean.getThreeOrMoreDebtsForContractDebtors();
            }
            SpecializationDebtorsYearBean specializationDebtorsForFacultyYearBean
                    = new SpecializationDebtorsYearBean(allBudgetStudentOfCurrentFaculty, allContractStudentOfCurrentFaculty,
                    allBudgetDebtorsOfCurrentFaculty, allContractDebtorsOfCurrentFaculty,
                    (allBudgetDebtorsOfCurrentFaculty + allContractDebtorsOfCurrentFaculty) * 1.0 / (allBudgetStudentOfCurrentFaculty + allContractStudentOfCurrentFaculty) * 100,
                    allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFaculty,
                    allContractDebtorsWithLessThanThreeDebtsOfCurrentFaculty,
                    allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFaculty,
                    allContractDebtorsWithThreeOrMoreDebtsOfCurrentFaculty);
            allSpecializationDebtorsYearBeanMap.put(year, specializationDebtorsForFacultyYearBean);
        }
        calculateAllDataOfFaculty(allSpecializationDebtorsYearBeanMap, debtorsReport, faculty);
    }

    private void calculateAllDataOfFaculty(Map<Integer, SpecializationDebtorsYearBean> allSpecializationDebtorsYearBeanMap,
                                           Map<String, SpecializationDebtorsBean> debtorsReport, Faculty faculty) {
        int allBudgetStudentOfFaculty = 0;
        int allContractStudentOfFaculty = 0;
        int allBudgetDebtorsOfFaculty = 0;
        int allContractDebtorsOfFaculty = 0;
        int allBudgetDebtorsWithLessThanThreeDebtsOfFaculty = 0;
        int allContractDebtorsWithLessThanThreeDebtsOfFaculty = 0;
        int allBudgetDebtorsWithThreeOrMoreDebtsOfFaculty = 0;
        int allContractDebtorsWithThreeOrMoreDebtsOfFaculty = 0;

        for (Map.Entry<Integer, SpecializationDebtorsYearBean> entry: allSpecializationDebtorsYearBeanMap.entrySet()) {
            allBudgetStudentOfFaculty += entry.getValue().getBudgetStudents();
            allContractStudentOfFaculty += entry.getValue().getContractStudents();
            allBudgetDebtorsOfFaculty += entry.getValue().getBudgetDebtors();
            allContractDebtorsOfFaculty += entry.getValue().getContractDebtors();
            allBudgetDebtorsWithLessThanThreeDebtsOfFaculty += entry.getValue().getLessThanThreeDebtsForBudgetDebtors();
            allContractDebtorsWithLessThanThreeDebtsOfFaculty += entry.getValue().getLessThanThreeDebtsForContractDebtors();
            allBudgetDebtorsWithThreeOrMoreDebtsOfFaculty += entry.getValue().getThreeOrMoreDebtsForBudgetDebtors();
            allContractDebtorsWithThreeOrMoreDebtsOfFaculty += entry.getValue().getThreeOrMoreDebtsForContractDebtors();
        }

        SpecializationDebtorsYearBean facultyDebtorsForAllYearsBean
                = new SpecializationDebtorsYearBean(allBudgetStudentOfFaculty, allContractStudentOfFaculty,
                allBudgetDebtorsOfFaculty, allContractDebtorsOfFaculty,
                (allBudgetDebtorsOfFaculty + allContractDebtorsOfFaculty) * 1.0 / (allBudgetStudentOfFaculty + allContractStudentOfFaculty) * 100,
                allBudgetDebtorsWithLessThanThreeDebtsOfFaculty,
                allContractDebtorsWithLessThanThreeDebtsOfFaculty,
                allBudgetDebtorsWithThreeOrMoreDebtsOfFaculty,
                allContractDebtorsWithThreeOrMoreDebtsOfFaculty);

        allSpecializationDebtorsYearBeanMap.put(NUMBER_OF_YEARS + 1, facultyDebtorsForAllYearsBean);

        SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
        specializationDebtorsBean.setSpecializationDebtorsYearBeanMap(allSpecializationDebtorsYearBeanMap);
        debtorsReport.put(faculty.getName(), specializationDebtorsBean);
    }

    private int getCorrectYear(int year) {
        if (year < 5) {
            return year;
        }
        return year - 4;
    }

    private int getDegreeIdByYear(int year) {
        if (year < 5) {
            return DegreeEnum.BACHELOR.getId();
        }
        return DegreeEnum.MASTER.getId();
    }
}
