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
                               StudentDegreeService studentDegreeService,
                               StudentGroupService studentGroupService) {
        this.specializationRepository = specializationRepository;
        this.studentDegreeService = studentDegreeService;
        this.studentGroupService = studentGroupService;
    }

    public Map<String, SpecializationDebtorsBean> calculateDebtorsReportData(Faculty faculty, int semester) throws Exception {

        if (semester > 2 || semester < 0)
            throw new Exception("Incorrect semester!");

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
                int lessThanThreeDebtsForBudgetDebtorsCount, lessThanThreeDebtsForContractDebtorsCount,
                        threeOrMoreDebtsForBudgetDebtorsCount, threeOrMoreDebtsForContractDebtorsCount,
                        budgetDebtorsCount, contractDebtorsCount;
                if (semester != 0) {
                    contractDebtorsCount = studentDegreeService.getCountAllActiveDebtorsForCurrentSemester(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year), semester);
                    budgetDebtorsCount = studentDegreeService.getCountAllActiveDebtorsForCurrentSemester(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year), semester);
                    lessThanThreeDebtsForBudgetDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebsForCurrentSemester(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year), semester);
                    lessThanThreeDebtsForContractDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebsForCurrentSemester(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year), semester);
                    threeOrMoreDebtsForBudgetDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebtsForCurrentSemester(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year), semester);
                    threeOrMoreDebtsForContractDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebtsForCurrentSemester(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year), semester);
                } else {
                    contractDebtorsCount = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));
                    budgetDebtorsCount = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                    lessThanThreeDebtsForBudgetDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                    lessThanThreeDebtsForContractDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));
                    threeOrMoreDebtsForBudgetDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.BUDGET, getDegreeIdByYear(year));
                    threeOrMoreDebtsForContractDebtorsCount = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), getCorrectYear(year), TuitionForm.FULL_TIME, Payment.CONTRACT, getDegreeIdByYear(year));
                }
                double debtorsPercent = (budgetDebtorsCount + contractDebtorsCount) / (budgetStudentsCount * 1.0 + contractStudentsCount) * 100;
                double lessThanThreeDebtsPercent = (lessThanThreeDebtsForBudgetDebtorsCount + lessThanThreeDebtsForContractDebtorsCount) /
                        (budgetStudentsCount * 1.0 + contractStudentsCount) * 100;
                double threeOrMoreDebtsPercent = (threeOrMoreDebtsForBudgetDebtorsCount + threeOrMoreDebtsForContractDebtorsCount) /
                        (budgetStudentsCount * 1.0 + contractStudentsCount) * 100;
                SpecializationDebtorsYearBean specializationDebtorsYearBean
                        = new SpecializationDebtorsYearBean(budgetStudentsCount, contractStudentsCount, budgetDebtorsCount,
                        contractDebtorsCount, debtorsPercent, lessThanThreeDebtsForBudgetDebtorsCount,
                        lessThanThreeDebtsForContractDebtorsCount, lessThanThreeDebtsPercent,
                        threeOrMoreDebtsForBudgetDebtorsCount, threeOrMoreDebtsForContractDebtorsCount,
                        threeOrMoreDebtsPercent);

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

    public Map<String, SpecializationDebtorsBean> calculateDebtorsReportDataForGroups(Faculty faculty, List<Integer> groupsIds) throws Exception {
        if (groupsIds.size() == 0)
            throw new Exception("Не вказано жодної групи!");

        List<Integer> studySemestersForTheseStudentGroup = studentGroupService.getStudySemestersByIds(groupsIds, true);

        if (groupsIds.size() != studySemestersForTheseStudentGroup.size())
            throw new Exception("Серед вказаних груп є неактивні або неіснуючі!");

        int maxSemester = getTheLatestSemesterForGroups(studySemestersForTheseStudentGroup);
        Map<Integer, SpecializationDebtorsYearBean> semesterAndDebtors = new TreeMap<>();

        for (int semester = 1; semester <= maxSemester; semester++) {
            int budgetStudentsCount = 0;
            int contractStudentsCount = 0;
            int budgetDebtorsCount = 0;
            int contractDebtorsCount = 0;
            int lessThanThreeDebtsForBudgetDebtorsCount = 0;
            int lessThanThreeDebtsForContractDebtorsCount = 0;
            int threeOrMoreDebtsForBudgetDebtorsCount = 0;
            int threeOrMoreDebtsForContractDebtorsCount = 0;

            budgetStudentsCount += studentDegreeService.getCountOfAllActiveBudgetOrContractStudentsInStudentsGroups(groupsIds, Payment.BUDGET, semester);
            contractStudentsCount += studentDegreeService.getCountOfAllActiveBudgetOrContractStudentsInStudentsGroups(groupsIds, Payment.CONTRACT, semester);

            if (budgetStudentsCount + contractStudentsCount == 0) {
                continue;
            }

            budgetDebtorsCount += studentDegreeService.getCountAllActiveDebtorsInStudentsGroupsByPaymentAndTuitionFormAndSemester(groupsIds, Payment.BUDGET, TuitionForm.FULL_TIME, semester);
            contractDebtorsCount += studentDegreeService.getCountAllActiveDebtorsInStudentsGroupsByPaymentAndTuitionFormAndSemester(groupsIds, Payment.CONTRACT, TuitionForm.FULL_TIME, semester);
            lessThanThreeDebtsForBudgetDebtorsCount += studentDegreeService.getCountAllActiveDebtorsInStudentGroupsWithLessThanThreeDebs(groupsIds, Payment.BUDGET, TuitionForm.FULL_TIME, semester);
            lessThanThreeDebtsForContractDebtorsCount += studentDegreeService.getCountAllActiveDebtorsInStudentGroupsWithLessThanThreeDebs(groupsIds, Payment.CONTRACT, TuitionForm.FULL_TIME, semester);
            threeOrMoreDebtsForBudgetDebtorsCount += studentDegreeService.getCountAllActiveDebtorsInStudentGroupsWithThreeOrMoreDebs(groupsIds, Payment.BUDGET, TuitionForm.FULL_TIME, semester);
            threeOrMoreDebtsForContractDebtorsCount += studentDegreeService.getCountAllActiveDebtorsInStudentGroupsWithThreeOrMoreDebs(groupsIds, Payment.CONTRACT, TuitionForm.FULL_TIME, semester);

            double debtorsPercent = (budgetDebtorsCount + contractDebtorsCount) / (budgetStudentsCount * 1.0 + contractStudentsCount) * 100;
            double lessThanThreeDebtsPercent = (lessThanThreeDebtsForBudgetDebtorsCount + lessThanThreeDebtsForContractDebtorsCount) /
                    (budgetStudentsCount * 1.0 + contractStudentsCount) * 100;
            double threeOrMoreDebtsPercent = (threeOrMoreDebtsForBudgetDebtorsCount + threeOrMoreDebtsForContractDebtorsCount) /
                    (budgetStudentsCount * 1.0 + contractStudentsCount) * 100;

            SpecializationDebtorsYearBean specializationDebtorsYearBean
                    = new SpecializationDebtorsYearBean(budgetStudentsCount, contractStudentsCount, budgetDebtorsCount,
                    contractDebtorsCount, debtorsPercent, lessThanThreeDebtsForBudgetDebtorsCount,
                    lessThanThreeDebtsForContractDebtorsCount, lessThanThreeDebtsPercent,
                    threeOrMoreDebtsForBudgetDebtorsCount, threeOrMoreDebtsForContractDebtorsCount,
                    threeOrMoreDebtsPercent);

            semesterAndDebtors.put(semester, specializationDebtorsYearBean);
        }

        //semesterAndDebtors.put(maxSemester + 1, calculateAllDataOfSpecializationOrFaculty(semesterAndDebtors));

        Map<String, SpecializationDebtorsBean> debtorsReport = new TreeMap<>();

        SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
        specializationDebtorsBean.setSpecializationDebtorsYearBeanMap(semesterAndDebtors);

        debtorsReport.put("Debtor's report for current groups", specializationDebtorsBean);

        return debtorsReport;
    }

    private int getTheLatestSemesterForGroups(List<Integer> studySemestersForTheseStudentGroup) throws Exception {
        if (studySemestersForTheseStudentGroup.size() == 0)
            throw new Exception("Вказаних груп немає в базі даних!");

        int maxSemester = 0;

        for (Integer e : studySemestersForTheseStudentGroup) {
            if (e > maxSemester) {
                maxSemester = e;
            }
        }

        return maxSemester;
    }

    private void calculateAllDataOfSpecialization(Map<String, SpecializationDebtorsBean> debtorsReport) {
        for (Map.Entry<String, SpecializationDebtorsBean> specialization : debtorsReport.entrySet()) {
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

            for (Map.Entry<String, SpecializationDebtorsBean> entry : debtorsReport.entrySet()) {
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

            if (allBudgetStudentOfCurrentFacultyCount + allContractStudentOfCurrentFacultyCount == 0) {
                continue;
            }

            double allDebtorsOfCurrentFacultyPercent = (allBudgetDebtorsOfCurrentFacultyCount + allContractDebtorsOfCurrentFacultyCount)
                    * 1.0 / (allBudgetStudentOfCurrentFacultyCount + allContractStudentOfCurrentFacultyCount) * 100;
            double allDebtorsOfCurrentFacultyWithLessThanThreeDebtsPercent =
                    (allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount +
                     allContractDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount)
                     * 1.0 / (allBudgetStudentOfCurrentFacultyCount + allContractStudentOfCurrentFacultyCount) * 100;
            double allDebtorsOfCurrentFacultyWithThreeOrMoreDebtsPercent =
                    (allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount +
                     allContractDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount)
                            * 1.0 / (allBudgetStudentOfCurrentFacultyCount + allContractStudentOfCurrentFacultyCount) * 100;

            SpecializationDebtorsYearBean specializationDebtorsForFacultyYearBean
                    = new SpecializationDebtorsYearBean(allBudgetStudentOfCurrentFacultyCount, allContractStudentOfCurrentFacultyCount,
                    allBudgetDebtorsOfCurrentFacultyCount, allContractDebtorsOfCurrentFacultyCount,
                    allDebtorsOfCurrentFacultyPercent,
                    allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount,
                    allContractDebtorsWithLessThanThreeDebtsOfCurrentFacultyCount,
                    allDebtorsOfCurrentFacultyWithLessThanThreeDebtsPercent,
                    allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount,
                    allContractDebtorsWithThreeOrMoreDebtsOfCurrentFacultyCount,
                    allDebtorsOfCurrentFacultyWithThreeOrMoreDebtsPercent);
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

        for (Map.Entry<Integer, SpecializationDebtorsYearBean> entry : specializationDebtorsYearBeanMap.entrySet()) {
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

        double allDebtorsPercent = (allBudgetDebtorsCount + allContractDebtorsCount)
                * 1.0 / (allBudgetStudentCount + allContractStudentCount) * 100;
        double allDebtorsWithLessThanThreeDebtsPercent =
                (allBudgetDebtorsWithLessThanThreeDebtsCount +
                 allContractDebtorsWithLessThanThreeDebtsCount)
                 * 1.0 / (allBudgetStudentCount + allContractStudentCount) * 100;
        double allDebtorsWithThreeOrMoreDebtsPercent =
                (allBudgetDebtorsWithThreeOrMoreDebtsCount +
                 allContractDebtorsWithThreeOrMoreDebtsCount)
                 * 1.0 / (allBudgetStudentCount + allContractStudentCount) * 100;

        SpecializationDebtorsYearBean specializationDebtorsYearBean
                = new SpecializationDebtorsYearBean(allBudgetStudentCount, allContractStudentCount, allBudgetDebtorsCount,
                allContractDebtorsCount, allDebtorsPercent,
                allBudgetDebtorsWithLessThanThreeDebtsCount, allContractDebtorsWithLessThanThreeDebtsCount,
                allDebtorsWithLessThanThreeDebtsPercent, allBudgetDebtorsWithThreeOrMoreDebtsCount,
                allContractDebtorsWithThreeOrMoreDebtsCount, allDebtorsWithThreeOrMoreDebtsPercent);

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
