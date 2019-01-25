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

@RestController
@RequestMapping("/report/debtor")
public class DebtorReportController {

    private DebtorReportService debtorReportService;

    @Autowired
    public DebtorReportController(DebtorReportService debtorReportService) {
        this.debtorReportService = debtorReportService;
    }

    @GetMapping
    public ResponseEntity getReportOfDebtors(@CurrentUser ApplicationUser user) {
        try {
            Map debtorsReport = debtorReportService.calculateDebtorsReportData(user.getFaculty().getId());
        } catch (Exception e) {

        }
        return null;
    }
}
