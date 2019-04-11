package ua.edu.chdtu.deanoffice.service.report.debtor;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
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


        return documentIOService.saveDocumentToTemp(template, transliterate(null), FileFormatEnum.DOCX);
    }

}
