package ua.edu.chdtu.deanoffice.api.report.debtor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.report.debtor.DebtorReportService;
import ua.edu.chdtu.deanoffice.service.report.debtor.SpecializationDebtorsBean;
import ua.edu.chdtu.deanoffice.service.report.debtor.SpecializationDebtorsYearBean;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;

import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/report/debtor")
public class DebtorReportController {

    private DebtorReportService debtorReportService;

    @Autowired
    public DebtorReportController(DebtorReportService debtorReportService) {
        this.debtorReportService = debtorReportService;
    }

    @GetMapping
    public ResponseEntity<Map<String, SpecializationDebtorStatisticsDto>> getReportOfDebtors(@CurrentUser ApplicationUser user) {
        try {
            Map<String, SpecializationDebtorsBean> debtorsReport = debtorReportService.calculateDebtorsReportData(user.getFaculty());
            Map<String, SpecializationDebtorStatisticsDto> debtorsReportDTO = new TreeMap<>();
            for (Map.Entry<String, SpecializationDebtorsBean> specializationDebtorsBeanEntry: debtorsReport.entrySet()) {
                SpecializationDebtorStatisticsDto sds = new SpecializationDebtorStatisticsDto();
                Mapper.strictMap(specializationDebtorsBeanEntry.getValue(), sds);
                debtorsReportDTO.put(specializationDebtorsBeanEntry.getKey(), sds);
            }

            return ResponseEntity.ok().body(debtorsReportDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
