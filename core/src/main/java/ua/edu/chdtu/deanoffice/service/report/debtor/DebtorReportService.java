package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.repository.SpecializationRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DebtorReportService {
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

    public Map<String, SpecializationDebtorsYearBean> calculateDebtorsReportData(int facultyId) {
        Map<String, SpecializationDebtorsBean> debtorsReport = new TreeMap<>();
        List<Specialization> specializations = specializationRepository.findAllByActive(true, facultyId);
        for (Specialization specialization : specializations) {
            SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
            for (int i = 1; i < 6; i++) {

                int budgetStudents = studentDegreeService.getCountAllActiveBudgetStudents(specialization.getId(), i);
                int contractStudents = studentDegreeService.getCountAllActiveContractStudents(specialization.getId(), i);
                int budgetDebtors = studentDegreeService.getCountAllActiveBudgetDebtors(specialization.getId(), i);
                int contractDebtors = studentDegreeService.getCountAllActiveContractDebtors(specialization.getId(), i);
                double debtorsPercent = (budgetDebtors + contractDebtors) / (budgetStudents + contractStudents);
//                int lessThanThreeDebtsForBudgetDebtors = 0;
//                int lessThanThreeDebtsForContractDebtors = 0;
//                int threeOrMoreDebtsForBudgetDebtors = 0;
//                int threeOrMoreDebtsForContractDebtors = 0;
//
//                SpecializationDebtorsYearBean departmentDebtorsYearBean
//                    = new SpecializationDebtorsYearBean(budgetStudents, contractStudents, budgetDebtors,
//                                                        contractDebtors, debtorsPercent, lessThanThreeDebtsForBudgetDebtors,
//                                                        lessThanThreeDebtsForContractDebtors, threeOrMoreDebtsForBudgetDebtors,
//                                                        threeOrMoreDebtsForContractDebtors);
            }
           //     debtorsReport.put(specialization.getName(), )
        }

        return null;
    }
}
