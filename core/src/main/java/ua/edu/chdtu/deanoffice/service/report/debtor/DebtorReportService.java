package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
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

            Map<Integer, SpecializationDebtorsYearBean> specializationDebtorsYearBeanMap = new TreeMap<>();

            for (int year = 1; year <= NUMBER_OF_YEARS; year++) {

                int budgetStudents = studentDegreeService.getCountAllActiveBudgetStudents(specialization.getId(), year);
                int contractStudents = studentDegreeService.getCountAllActiveContractStudents(specialization.getId(), year);

                if (budgetStudents + contractStudents == 0) {
                    continue;
                }

                int budgetDebtors = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), year, TuitionForm.FULL_TIME, Payment.BUDGET);
                int contractDebtors = studentDegreeService.getCountAllActiveDebtors(specialization.getId(), year, TuitionForm.FULL_TIME, Payment.CONTRACT);
                double debtorsPercent = (budgetDebtors + contractDebtors) / (budgetStudents * 1.0 + contractStudents) * 100;
                int lessThanThreeDebtsForBudgetDebtors = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), year, TuitionForm.FULL_TIME, Payment.BUDGET);
                int lessThanThreeDebtsForContractDebtors = studentDegreeService.getCountAllActiveDebtorsWithLessThanThreeDebs(specialization.getId(), year, TuitionForm.FULL_TIME, Payment.CONTRACT);
                int threeOrMoreDebtsForBudgetDebtors = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), year, TuitionForm.FULL_TIME, Payment.BUDGET);
                int threeOrMoreDebtsForContractDebtors = studentDegreeService.getCountAllActiveDebtorsWithThreeOrMoreDebts(specialization.getId(), year, TuitionForm.FULL_TIME, Payment.CONTRACT);

                SpecializationDebtorsYearBean specializationDebtorsYearBean
                    = new SpecializationDebtorsYearBean(budgetStudents, contractStudents, budgetDebtors,
                                                        contractDebtors, debtorsPercent, lessThanThreeDebtsForBudgetDebtors,
                                                        lessThanThreeDebtsForContractDebtors, threeOrMoreDebtsForBudgetDebtors,
                                                        threeOrMoreDebtsForContractDebtors);

                specializationDebtorsYearBeanMap.put(year, specializationDebtorsYearBean);
            }

            int allBudgetStudent = 0;
            int allContractStudent = 0;
            int allBudgetDebtors = 0;
            int allContractDebtors = 0;
            double allDebtorsPercent = 0;
            int allBudgetDebtorsWithLessThanThreeDebts = 0;
            int allContractDebtorsWithLessThanThreeDebts = 0;
            int allBudgetDebtorsWithThreeOrMoreDebts = 0;
            int allContractDebtorsWithThreeOrMoreDebts = 0;

            for (Map.Entry<Integer, SpecializationDebtorsYearBean> entry: specializationDebtorsYearBeanMap.entrySet()) {
                allBudgetStudent += entry.getValue().getBudgetStudents();
                allContractStudent += entry.getValue().getContractStudents();
                allBudgetDebtors += entry.getValue().getBudgetDebtors();
                allContractDebtors += entry.getValue().getContractDebtors();
                allDebtorsPercent += entry.getValue().getDebtorsPercent();
                allBudgetDebtorsWithLessThanThreeDebts += entry.getValue().getLessThanThreeDebtsForBudgetDebtors();
                allContractDebtorsWithLessThanThreeDebts += entry.getValue().getLessThanThreeDebtsForContractDebtors();
                allBudgetDebtorsWithThreeOrMoreDebts += entry.getValue().getThreeOrMoreDebtsForBudgetDebtors();
                allContractDebtorsWithThreeOrMoreDebts += entry.getValue().getThreeOrMoreDebtsForContractDebtors();
            }

            SpecializationDebtorsYearBean specializationDebtorsForAllYearsBean
                    = new SpecializationDebtorsYearBean(allBudgetStudent, allContractStudent, allBudgetDebtors,
                                                        allContractDebtors, allDebtorsPercent / NUMBER_OF_YEARS,
                                                        allBudgetDebtorsWithLessThanThreeDebts, allContractDebtorsWithLessThanThreeDebts,
                                                        allBudgetDebtorsWithThreeOrMoreDebts, allContractDebtorsWithThreeOrMoreDebts);

            specializationDebtorsYearBeanMap.put(NUMBER_OF_YEARS + 1, specializationDebtorsForAllYearsBean);

            SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
            specializationDebtorsBean.setSpecializationDebtorsYearBeanMap(specializationDebtorsYearBeanMap);
            debtorsReport.put(specialization.getName(), specializationDebtorsBean);
        }

        Map<Integer, SpecializationDebtorsYearBean> allSpecializationDebtorsYearBeanMap = new TreeMap<>();

        for (int year = 1; year <= NUMBER_OF_YEARS; year++) {
            int allBudgetStudentOfCurrentFaculty = 0;
            int allContractStudentOfCurrentFaculty = 0;
            int allBudgetDebtorsOfCurrentFaculty = 0;
            int allContractDebtorsOfCurrentFaculty = 0;
            int allDebtorsPercentOfCurrentFaculty = 0;
            int allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFaculty = 0;
            int allContractDebtorsWithLessThanThreeDebtsOfCurrentFaculty = 0;
            int allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFaculty = 0;
            int allContractDebtorsWithThreeOrMoreDebtsOfCurrentFaculty = 0;

            for (Map.Entry<String, SpecializationDebtorsBean> entry: debtorsReport.entrySet()) {
                allBudgetStudentOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getBudgetStudents();
                allContractStudentOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getContractStudents();
                allBudgetDebtorsOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getBudgetDebtors();
                allContractDebtorsOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getContractDebtors();
                allDebtorsPercentOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getDebtorsPercent();
                allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getLessThanThreeDebtsForBudgetDebtors();
                allContractDebtorsWithLessThanThreeDebtsOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getLessThanThreeDebtsForContractDebtors();
                allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getThreeOrMoreDebtsForBudgetDebtors();
                allContractDebtorsWithThreeOrMoreDebtsOfCurrentFaculty += entry.getValue().getSpecializationDebtorsYearBeanMap().get(year).getThreeOrMoreDebtsForContractDebtors();
            }

            SpecializationDebtorsYearBean specializationDebtorsForFacultyYearBean
                    = new SpecializationDebtorsYearBean(allBudgetStudentOfCurrentFaculty, allContractStudentOfCurrentFaculty,
                                                        allBudgetDebtorsOfCurrentFaculty, allContractDebtorsOfCurrentFaculty,
                                                        allDebtorsPercentOfCurrentFaculty / specializations.size(),
                                                        allBudgetDebtorsWithLessThanThreeDebtsOfCurrentFaculty,
                                                        allContractDebtorsWithLessThanThreeDebtsOfCurrentFaculty,
                                                        allBudgetDebtorsWithThreeOrMoreDebtsOfCurrentFaculty,
                                                        allContractDebtorsWithThreeOrMoreDebtsOfCurrentFaculty);
            allSpecializationDebtorsYearBeanMap.put(year, specializationDebtorsForFacultyYearBean);
        }

        int allBudgetStudentOfFaculty = 0;
        int allContractStudentOfFaculty = 0;
        int allBudgetDebtorsOfFaculty = 0;
        int allContractDebtorsOfFaculty = 0;
        double allDebtorsPercentOfFaculty = 0;
        int allBudgetDebtorsWithLessThanThreeDebtsOfFaculty = 0;
        int allContractDebtorsWithLessThanThreeDebtsOfFaculty = 0;
        int allBudgetDebtorsWithThreeOrMoreDebtsOfFaculty = 0;
        int allContractDebtorsWithThreeOrMoreDebtsOfFaculty = 0;

        for (Map.Entry<Integer, SpecializationDebtorsYearBean> entry: allSpecializationDebtorsYearBeanMap.entrySet()) {

            allBudgetStudentOfFaculty += entry.getValue().getBudgetStudents();
            allContractStudentOfFaculty += entry.getValue().getContractStudents();
            allBudgetDebtorsOfFaculty += entry.getValue().getBudgetDebtors();
            allContractDebtorsOfFaculty += entry.getValue().getContractDebtors();
            allDebtorsPercentOfFaculty += entry.getValue().getDebtorsPercent();
            allBudgetDebtorsWithLessThanThreeDebtsOfFaculty += entry.getValue().getLessThanThreeDebtsForBudgetDebtors();
            allContractDebtorsWithLessThanThreeDebtsOfFaculty += entry.getValue().getLessThanThreeDebtsForContractDebtors();
            allBudgetDebtorsWithThreeOrMoreDebtsOfFaculty += entry.getValue().getThreeOrMoreDebtsForBudgetDebtors();
            allContractDebtorsWithThreeOrMoreDebtsOfFaculty += entry.getValue().getThreeOrMoreDebtsForContractDebtors();
        }

        SpecializationDebtorsYearBean facultyDebtorsForAllYearsBean
                = new SpecializationDebtorsYearBean(allBudgetStudentOfFaculty, allContractStudentOfFaculty,
                                                    allBudgetDebtorsOfFaculty, allContractDebtorsOfFaculty,
                                                    allDebtorsPercentOfFaculty / NUMBER_OF_YEARS,
                                                    allBudgetDebtorsWithLessThanThreeDebtsOfFaculty,
                                                    allContractDebtorsWithLessThanThreeDebtsOfFaculty,
                                                    allBudgetDebtorsWithThreeOrMoreDebtsOfFaculty,
                                                    allContractDebtorsWithThreeOrMoreDebtsOfFaculty);

        allSpecializationDebtorsYearBeanMap.put(NUMBER_OF_YEARS + 1, facultyDebtorsForAllYearsBean);

        SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
        specializationDebtorsBean.setSpecializationDebtorsYearBeanMap(allSpecializationDebtorsYearBeanMap);
        debtorsReport.put(faculty.getName(), specializationDebtorsBean);

        return debtorsReport;
    }
}
