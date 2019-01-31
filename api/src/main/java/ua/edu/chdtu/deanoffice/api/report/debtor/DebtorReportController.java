package ua.edu.chdtu.deanoffice.api.report.debtor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.report.debtor.DebtorReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

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
    public ResponseEntity<Map<String, DebtorStatisticsDto>> getReportOfDebtors(@CurrentUser ApplicationUser user) {
        try {
            Map<String, DebtorStatisticsDto> debtorsReport = new TreeMap<>(); //debtorReportService.calculateDebtorsReportData(user.getFaculty().getId());
            DebtorStatisticsDto dad1 = new DebtorStatisticsDto("13", "2");
            DebtorStatisticsDto dad2 = new DebtorStatisticsDto("11", "3");
            debtorsReport.put("Інженерія програмного забезпечення", dad1);
            debtorsReport.put("Системний аналіз", dad2);
            return ResponseEntity.ok().body(debtorsReport);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
