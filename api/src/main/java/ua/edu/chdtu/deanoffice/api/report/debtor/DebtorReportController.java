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
    public ResponseEntity<Map<String, DebtorAnalysisDto>> getReportOfDebtors(@CurrentUser ApplicationUser user) {
        try {
            Map<String, DebtorAnalysisDto> debtorsReport = new TreeMap<>(); //debtorReportService.calculateDebtorsReportData(user.getFaculty().getId());
            DebtorAnalysisDto dad1 = new DebtorAnalysisDto("13", "2");
            DebtorAnalysisDto dad2 = new DebtorAnalysisDto("11", "3");
            debtorsReport.put("Інженерія програмного забезпечення", dad1);
            debtorsReport.put("Системний аналіз", dad2);
            return ResponseEntity.ok().body(debtorsReport);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
