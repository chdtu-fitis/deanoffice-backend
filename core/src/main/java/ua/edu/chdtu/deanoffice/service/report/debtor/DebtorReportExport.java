package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;

@Service
public class DebtorReportExport {
    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "StudentPerformanceAnalysis.docx";
    private final String WINTER_SEASON = "зимової";
    private final String SPRING_SEASON = "весняної";
    private DocumentIOService documentIOService;

    public DebtorReportExport(DocumentIOService documentIOService) {
        this.documentIOService = documentIOService;
    }

    public File formDocument(Map<String, SpecializationDebtorsBean> debtorsReport) throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        generateTables(template, debtorsReport);
        String fileName = "statystyka_borzhnykiv_" + getFileCreationDateAndTime() + ".docx";
        return documentIOService.saveDocumentToTemp(template, fileName, FileFormatEnum.DOCX);
    }

    private void generateTables(WordprocessingMLPackage template, Map<String, SpecializationDebtorsBean> debtorsReport) {
        List<Object> documentTables = getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class);
        Tbl specialityTable = (Tbl) documentTables.get(0);
        Tbl performanceTable = (Tbl) documentTables.get(1);
        for (String speciality : debtorsReport.keySet()) {
            Tbl specialityTableCopy = XmlUtils.deepCopy(specialityTable);
            fillSpecialityTableRows(specialityTableCopy, speciality);
            template.getMainDocumentPart().addObject(specialityTableCopy);
            Map<Integer, SpecializationDebtorsYearBean> specializationDebtorsYearBeanMap = debtorsReport.get(speciality).getSpecializationDebtorsYearBeanMap();
            for (Integer year : specializationDebtorsYearBeanMap.keySet()) {
                SpecializationDebtorsYearBean specializationDebtorsYearBean = specializationDebtorsYearBeanMap.get(year);
                Tbl performanceTableCopy = XmlUtils.deepCopy(performanceTable);
                fillPerformanceTableRows(performanceTableCopy, year, specializationDebtorsYearBean);
                template.getMainDocumentPart().addObject(performanceTableCopy);
            }
        }
        template.getMainDocumentPart().getContent().remove(0);
        template.getMainDocumentPart().getContent().remove(1);
    }

    private void fillPerformanceTableRows(Tbl table, int course, SpecializationDebtorsYearBean bean) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Map<String, String> map = new HashMap<>();
        if (course == 7)
            map.put("year", "Всього");
        else {
            map.put("year", "по " + course + " курсу");
            map.put("ayear", "по " + course + " курсу");
        }
        fillFirsAndSecondRows(tableRows, map);
        map.put("ts", (bean.getBudgetStudents() + bean.getContractStudents()) + "");
        map.put("bs", bean.getBudgetStudents() + "");
        map.put("cs", bean.getContractStudents() + "");
        map.put("td", (bean.getBudgetDebtors() + bean.getContractDebtors()) + "");
        map.put("bd", bean.getBudgetDebtors() + "");
        map.put("cd", bean.getContractDebtors() + "");
        map.put("%", String.format("%.2f", bean.getDebtorsPercent()));
        map.put("lttb", bean.getLessThanThreeDebtsForBudgetDebtors() + "");
        map.put("lttc", bean.getLessThanThreeDebtsForContractDebtors() + "");
        map.put("tomb", bean.getThreeOrMoreDebtsForBudgetDebtors() + "");
        map.put("tomc", bean.getThreeOrMoreDebtsForContractDebtors() + "");
        replaceInRow(tableRows.get(3), map);
    }

    private void fillFirsAndSecondRows(List<Tr> tableRows, Map<String, String> map) {
        replaceInRow(tableRows.get(0), map);
        replaceInRow(tableRows.get(1), map);
    }

    private void fillSpecialityTableRows(Tbl table, String speciality) {
        Map<String, String> map = new HashMap<>();
        map.put("speciality", speciality);
        map.put("season", getSeason());
        map.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        replaceInRow((Tr) getAllElementsFromObject(table, Tr.class).get(0), map);
    }

    private String getSeason() {
        LocalDate winterSessionStarts = LocalDate.of(LocalDate.now().getYear(), 12, 15);
        LocalDate winterSessionEnds = LocalDate.of(LocalDate.now().getYear(), 6, 10);
        if (LocalDate.now().isAfter(winterSessionEnds) && LocalDate.now().isBefore(winterSessionStarts))
            return SPRING_SEASON;
        else
            return WINTER_SEASON;
    }

}
