package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.repository.SpecializationRepository;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DebtorReportService {
    private SpecializationRepository specializationRepository;
    private StudentGroupService studentGroupService;

    @Autowired
    public DebtorReportService(SpecializationRepository specializationRepository,
                               StudentGroupService studentGroupService) {
        this.specializationRepository = specializationRepository;
        this.studentGroupService = studentGroupService;
    }

    public Map<String, SpecializationDebtorsYearBean> calculateDebtorsReportData(int facultyId) {
        Map<String, SpecializationDebtorsBean> debtorsReport = new TreeMap<>();
        List<Specialization> specializations = specializationRepository.findAllByActive(true, facultyId);
        for (Specialization specialization : specializations) {
            SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
            /*for (int i = 0; i < 6; i++) {
                SpecializationDebtorsYearBean departmentDebtorsYearBean =
            }
                debtorsReport.put(specialization.getName(), )*/
        }

        return null;
    }
}
