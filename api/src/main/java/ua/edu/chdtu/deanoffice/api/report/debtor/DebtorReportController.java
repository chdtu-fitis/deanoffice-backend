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
            Map<String, SpecializationDebtorStatisticsDto> debtorsReport = new TreeMap<>(); //debtorReportService.calculateDebtorsReportData(user.getFaculty().getId());
//             = debtorReportService.calculateDebtorsReportData(user.getFaculty());
//            Mapper.map(, debtorsReport);
            Map<Integer, SpecializationDebtorsYearBean> testMap = new TreeMap<>();
            SpecializationDebtorsYearBean specializationDebtorsYearBeanTest = new SpecializationDebtorsYearBean(20, 30, 4, 6, 20, 3, 4,1,2);
            testMap.put(1, specializationDebtorsYearBeanTest);
            SpecializationDebtorsYearBean specializationDebtorsYearBeanTest2 = new SpecializationDebtorsYearBean(40,60,8,12,20,6,8,2,4);
            testMap.put(2, specializationDebtorsYearBeanTest2);
            SpecializationDebtorsBean specializationDebtorsBean = new SpecializationDebtorsBean();
            specializationDebtorsBean.setSpecializationDebtorsYearBeanMap(testMap);

            testMap.clear();

            SpecializationDebtorsYearBean specializationDebtorsYearBeanTest3 = new SpecializationDebtorsYearBean(30,30,20,25,75,16, 13,4,12);
            testMap.put(1, specializationDebtorsYearBeanTest3);
            SpecializationDebtorsYearBean specializationDebtorsYearBeanTest4 = new SpecializationDebtorsYearBean(60,60,40,50,75,32, 26,8,24);
            testMap.put(2, specializationDebtorsYearBeanTest4);
            SpecializationDebtorsBean specializationDebtorsBean2 = new SpecializationDebtorsBean();
            specializationDebtorsBean2.setSpecializationDebtorsYearBeanMap(testMap);

            Map<String, SpecializationDebtorsBean> sourceDebtorsReport = new TreeMap<>();
            sourceDebtorsReport.put("Інженерія програмного забезпечення", specializationDebtorsBean);
            SpecializationDebtorStatisticsDto sds1 = new SpecializationDebtorStatisticsDto();
            Mapper.map(specializationDebtorsBean, sds1);
            debtorsReport.put("test specialization1", sds1);

            SpecializationDebtorStatisticsDto sds2 = new SpecializationDebtorStatisticsDto();
            Mapper.map(specializationDebtorsBean2, sds2);
            debtorsReport.put("test specialization2", sds2);

            return ResponseEntity.ok().body(debtorsReport);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
