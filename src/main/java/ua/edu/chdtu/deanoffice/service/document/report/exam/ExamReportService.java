package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class ExamReportService {

    private static final String TEMPLATE = TEMPLATES_PATH + "ExamReport.docx";

    private final DocumentIOService documentIOService;
    private final ExamReportTemplateFillService examReportTemplateFillService;

    public ExamReportService(DocumentIOService documentIOService,
                             ExamReportTemplateFillService examReportTemplateFillService) {
        this.documentIOService = documentIOService;
        this.examReportTemplateFillService = examReportTemplateFillService;
    }


    public File createExamReport(List<ExamReportDataBean> examReportDataBeans, FileFormatEnum format) throws Exception {
        if (!examReportDataBeans.isEmpty()) {
            try {
                String fileName = LanguageUtil.transliterate(examReportDataBeans.get(0).getGroupExamReportDataBean().getGroupName());
                WordprocessingMLPackage filledTemplate = examReportTemplateFillService.fillTemplate(TEMPLATE, examReportDataBeans, 0);
                return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);
            } catch (IOException | Docx4JException e) {
                throw new Exception("Немає даних для побудови відомості");
            }
        } else
            throw new OperationCannotBePerformedException("Немає даних для побудови відомості");
    }
}
