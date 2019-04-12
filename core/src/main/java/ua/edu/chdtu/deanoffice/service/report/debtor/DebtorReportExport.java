package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class DebtorReportExport {
    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "StudentPerformanceAnalysis.docx";
    private DebtorReportService debtorReportService;
    private DocumentIOService documentIOService;
    private FacultyService facultyService;

    public DebtorReportExport(DebtorReportService debtorReportService, DocumentIOService documentIOService, FacultyService facultyService) {
        this.debtorReportService = debtorReportService;
        this.documentIOService = documentIOService;
        this.facultyService = facultyService;
    }

    public File formDocument(Map<String, SpecializationDebtorsBean> debtorsReport) throws Docx4JException, IOException, JAXBException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);

        generateTables(template, debtorReportService.calculateDebtorsReportData(facultyService.getById(1)));

        return documentIOService.saveDocumentToTemp(template, transliterate("look"), FileFormatEnum.DOCX);
    }

    private void generateTables(WordprocessingMLPackage template, Map<String, SpecializationDebtorsBean> debtorsReport) {
        Tbl templateTable = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(0);
        for (String speciality : debtorsReport.keySet()) {
            Map<Integer, SpecializationDebtorsYearBean> specializationDebtorsYearBeanMap =
                    debtorsReport.get(speciality).getSpecializationDebtorsYearBeanMap();
            for (Integer course : specializationDebtorsYearBeanMap.keySet()) {
                SpecializationDebtorsYearBean specializationDebtorsYearBean = specializationDebtorsYearBeanMap.get(course);
                Tbl table = XmlUtils.deepCopy(templateTable);
                fillRows(table, course, specializationDebtorsYearBean);
                template.getMainDocumentPart().addObject(table);
            }
        }
        template.getMainDocumentPart().getContent().remove(4);
    }

    private void fillRows(Tbl table, int course, SpecializationDebtorsYearBean bean) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Map<String, String> map = new HashMap<>();
        if (course == 7)
            map.put("c", "Всього");
        else {
            map.put("c", "по " + course + " курсу");
            map.put("ac", "по " + course + " курсу");
        }
        fillFirstRows(tableRows, map);
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

    private void fillFirstRows(List<Tr> tableRows, Map<String, String> map) {
        replaceInRow(tableRows.get(0), map);
        replaceInRow(tableRows.get(1), map);
    }
}
