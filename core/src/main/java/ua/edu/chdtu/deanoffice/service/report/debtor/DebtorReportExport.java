package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

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

    public DebtorReportExport(DebtorReportService debtorReportService, DocumentIOService documentIOService) {
        this.debtorReportService = debtorReportService;
        this.documentIOService = documentIOService;
    }

    public File formDocument(Faculty faculty) throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        generateTables(template);
        return documentIOService.saveDocumentToTemp(template, transliterate("look"), FileFormatEnum.DOCX);
    }

    private void generateTables(WordprocessingMLPackage template) {
        Tbl templateTable = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(0);
        for (int i = 0; i < 5; i++) {
            Tbl table = XmlUtils.deepCopy(templateTable);
            fillFirstRow(table);
            template.getMainDocumentPart().addObject(table);
        }
        template.getMainDocumentPart().getContent().remove(0);
    }

    private void fillFirstRow(Tbl table) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Map<String, String> map = new HashMap<>();
        map.put("course", "it works");
        replaceInRow(tableRows.get(0), map);
    }
}
