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
                int budgetStudentsCount = studentDegreeService.getCountAllActiveStudents(specialization.getId(), getCorrectYear(year), Payment.BUDGET, getDegreeIdByYear(year));
                int contractStudentsCount = studentDegreeService.getCountAllActiveStudents(specialization.getId(), getCorrectYear(year), Payment.CONTRACT, getDegreeIdByYear(year));

                if (budgetStudentsCount + contractStudentsCount == 0) {
                    continue;
                }

                int budgetDebtorsCount = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                int contractDebtorsCount = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));
                double debtorsPercent = (budgetDebtorsCount + contractDebtorsCount) / (budgetStudentsCount * 1.0 + contractStudentsCount) * 100;
                int lessThanThreeDebtsForBudgetDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                int lessThanThreeDebtsForContractDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));
                int threeOrMoreDebtsForBudgetDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                int threeOrMoreDebtsForContractDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));

                SpecializationDebtorsYearBean specializationDebtorsYearBean
                    = new SpecializationDebtorsYearBean(budgetStudentsCount, contractStudentsCount, budgetDebtorsCount,
                                                        contractDebtorsCount, debtorsPercent, lessThanThreeDebtsForBudgetDebtorsCount,
                                                        lessThanThreeDebtsForContractDebtorsCount, threeOrMoreDebtsForBudgetDebtorsCount,
                                                        threeOrMoreDebtsForContractDebtorsCount);

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
            debtorsReport.get(specialization.getKey()).getSpecializationDebtorsYearBeanMap().put(NUMBER_OF_YEARS + 1, calculateAllDataOfSpecializationOrFaculty(specializationDebtorsYearBeanMap));
        }
    }

    private void calculateAllDataOfFacultyForEachYear(Map<String, SpecializationDebtorsBean> debtorsReport, Faculty faculty) {
        Map<Integer, SpecializationDebtorsYearBean> allSpecializationDebtorsYearBeanMap = new TreeMap<>();

        for (int year = 1; year <= NUMBER_OF_YEARS; year++) {
            int allBudgetStudentOfCurrentFacultyCount = 0;
            int allContractStudentOfCurrentFacultyCount = 0;
            int allBudgetDebtorsOfCurrentFacultyCount = 0;
            int allContractDebtorsOfCurrentFacultyCount = 0;
            int allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount = 0;
            int allContractDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount = 0;
            int allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount = 0;
            int allContractDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount = 0;

            for (Map.Entry<String, SpecializationDebtorsBean> entry: debtorsReport.entrySet()) {
                SpecializationDebtorsYearBean specializationDebtorsYearBean = entry.getValue().getSpecializationDebtorsYearBeanMap().get(year);
                if (specializationDebtorsYearBean == null) {
                    continue;
                }
                allBudgetStudentOfCurrentFacultyCount += specializationDebtorsYearBean.getBudgetStudents();
                allContractStudentOfCurrentFacultyCount += specializationDebtorsYearBean.getContractStudents();
                allBudgetDebtorsOfCurrentFacultyCount += specializationDebtorsYearBean.getBudgetDebtors();
                allContractDebtorsOfCurrentFacultyCount += specializationDebtorsYearBean.getContractDebtors();
                allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount += specializationDebtorsYearBean.getLessThanThreeDebtsForBudgetDebtors();
                allContractDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount += specializationDebtorsYearBean.getLessThanThreeDebtsForContractDebtors();
                allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount += specializationDebtorsYearBean.getThreeOrMoreDebtsForBudgetDebtors();
                allContractDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount += specializationDebtorsYearBean.getThreeOrMoreDebtsForContractDebtors();
            }
            SpecializationDebtorsYearBean specializationDebtorsForFacultyYearBean
                    = new SpecializationDebtorsYearBean(allBudgetStudentOfCurrentFacultyCount, allContractStudentOfCurrentFacultyCount,
                    allBudgetDebtorsOfCurrentFacultyCount, allContractDebtorsOfCurrentFacultyCount,
                    (allBudgetDebtorsOfCurrentFacultyCount + allContractDebtorsOfCurrentFacultyCount) * 1.0 / (allBudgetStudentOfCurrentFacultyCount + allContractStudentOfCurrentFacultyCount) * 100,
                    allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount,
                    allContractDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount,
                    allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount,
                    allContractDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount);
            allSpecializationDebtorsYearBeanMap.put(year, specializationDebtorsForFacultyYearBean);
        }
        calculateAllDataOfFaculty(allSpecializationDebtorsYearBeanMap, debtorsReport, faculty);
    }

    private void calculateAllDataOfFaculty(Map<Integer, SpecializationDebtorsYearBean> allSpecializationDebtorsYearBeanMap,
                                           Map<String, SpecializationDebtorsBean> debtorsReport, Faculty faculty) {
        allSpecializationDebtorsYearBeanMap.put(NUMBER_OF_YEARS + 1, calculateAllDataOfSpecializationOrFaculty(allSpecializationDebtorsYearBeanMap));

        SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
        specializationDebtorsBean.setSpecializationDebtorsYearBeanMap(allSpecializationDebtorsYearBeanMap);
        debtorsReport.put(faculty.getName(), specializationDebtorsBean);
    }

    private SpecializationDebtorsYearBean calculateAllDataOfSpecializationOrFaculty(Map<Integer, SpecializationDebtorsYearBean> specializationDebtorsYearBeanMap) {
        int allBudgetStudentCount = 0;
        int allContractStudentCount = 0;
        int allBudgetDebtorsCount = 0;
        int allContractDebtorsCount = 0;
        int allBudgetDebtorsWithLessThanThreeDebtsCount = 0;
        int allContractDebtorsWithLessThanThreeDebtsCount = 0;
        int allBudgetDebtorsWithThreeOrMoreDebtsCount = 0;
        int allContractDebtorsWithThreeOrMoreDebtsCount = 0;

        for (Map.Entry<Integer, SpecializationDebtorsYearBean> entry: specializationDebtorsYearBeanMap.entrySet()) {
            SpecializationDebtorsYearBean specializationDebtorsYearBean = entry.getValue();
            allBudgetStudentCount += specializationDebtorsYearBean.getBudgetStudents();
            allContractStudentCount += specializationDebtorsYearBean.getContractStudents();
            allBudgetDebtorsCount += specializationDebtorsYearBean.getBudgetDebtors();
            allContractDebtorsCount += specializationDebtorsYearBean.getContractDebtors();
            allBudgetDebtorsWithLessThanThreeDebtsCount += specializationDebtorsYearBean.getLessThanThreeDebtsForBudgetDebtors();
            allContractDebtorsWithLessThanThreeDebtsCount += specializationDebtorsYearBean.getLessThanThreeDebtsForContractDebtors();
            allBudgetDebtorsWithThreeOrMoreDebtsCount += specializationDebtorsYearBean.getThreeOrMoreDebtsForBudgetDebtors();
            allContractDebtorsWithThreeOrMoreDebtsCount += specializationDebtorsYearBean.getThreeOrMoreDebtsForContractDebtors();
        }

        SpecializationDebtorsYearBean specializationDebtorsYearBean
                = new SpecializationDebtorsYearBean(allBudgetStudentCount, allContractStudentCount, allBudgetDebtorsCount,
                allContractDebtorsCount, (allBudgetDebtorsCount + allContractDebtorsCount) * 1.0 / (allBudgetStudentCount + allContractStudentCount) * 100,
                allBudgetDebtorsWithLessThanThreeDebtsCount, allContractDebtorsWithLessThanThreeDebtsCount,
                allBudgetDebtorsWithThreeOrMoreDebtsCount, allContractDebtorsWithThreeOrMoreDebtsCount);

        return specializationDebtorsYearBean;
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
