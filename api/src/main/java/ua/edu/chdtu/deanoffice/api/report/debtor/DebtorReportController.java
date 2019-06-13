package ua.edu.chdtu.deanoffice.api.report.debtor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.report.debtor.DebtorReportExport;
import ua.edu.chdtu.deanoffice.service.report.debtor.DebtorReportService;
import ua.edu.chdtu.deanoffice.service.report.debtor.SpecializationDebtorsBean;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/report/debtor")
public class DebtorReportController extends DocumentResponseController {

    private DebtorReportService debtorReportService;
    private DebtorReportExport debtorReportExport;

    public DebtorReportController(DebtorReportService debtorReportService, DebtorReportExport debtorReportExport) {
        this.debtorReportService = debtorReportService;
        this.debtorReportExport = debtorReportExport;
    }

    @GetMapping
    public ResponseEntity<Map<String, SpecializationDebtorStatisticsDto>> getReportOfDebtors(
            @CurrentUser ApplicationUser user,
            @RequestParam(required = false, name = "forCurrentSemester", defaultValue = "false") Boolean forCurrentSemester) {
        try {
            Map<String, SpecializationDebtorsBean> debtorsReport = debtorReportService.calculateDebtorsReportData(user.getFaculty(), forCurrentSemester);
            Map<String, SpecializationDebtorStatisticsDto> debtorsReportDTO = new TreeMap<>();
            for (Map.Entry<String, SpecializationDebtorsBean> specializationDebtorsBeanEntry : debtorsReport.entrySet()) {
                SpecializationDebtorStatisticsDto sds = new SpecializationDebtorStatisticsDto();
                Mapper.strictMap(specializationDebtorsBeanEntry.getValue(), sds);
                debtorsReportDTO.put(specializationDebtorsBeanEntry.getKey(), sds);
            }
            return ResponseEntity.ok().body(debtorsReportDTO);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/export")
    public ResponseEntity getDebtorReportDocx(
            @CurrentUser ApplicationUser user,
            @RequestParam(required = false, name = "forCurrentSemester", defaultValue = "false") Boolean forCurrentSemester) {
        try {
            Map<String, SpecializationDebtorsBean> map = debtorReportService.calculateDebtorsReportData(user.getFaculty(), forCurrentSemester);
            File file = debtorReportExport.formDocument(map);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, DebtorReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
