package ua.edu.chdtu.deanoffice.service.document.report.graduates.group;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;

@Service
public class GraduatesGroupReportService {
    private StudentGroupService studentGroupService;
    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    public GraduatesGroupReportService(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    public File createGraduatesReportForGroupPdf(int groupId) throws IOException, DocumentException {
        StudentGroup group = studentGroupService.getById(groupId);
        Document document = new Document(PageSize.A4, 28f, 28f, 28f, 28f);
//        String filePath = getJavaTempDirectory() + "/" + "vidomist_vypusknykiv_" + group.getName() +"_" + getFileCreationDateAndTime() + ".pdf";
        String filePath = "D:/deanoffice/deanoffice-backend" + "/" + "vidomist_vypusknykiv_" + group.getName() + "_" + getFileCreationDateAndTime() + ".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font basefont = new Font(baseFont, 14);
            document.add(getCenterAlignedParagraph("ЧЕРКАСЬКИЙ ДЕРЖАВНИЙ ТЕХНОЛОГІЧНИЙ УНІВЕРСИТЕТ", basefont, 0));
            document.add(getCenterAlignedParagraph("ФАКУЛЬТЕТ ЕЛЕКТРОННИХ ТЕХНОЛОГІЙ І РОБОТОТЕХНІКИ", basefont, 100));
            document.add(getCenterAlignedParagraph("ВІДОМІСТЬ", new Font(baseFont, 14, Font.BOLD), 0));
            document.add(getCenterAlignedParagraph("навчальних досягнень здобувачів вищої освіти,", basefont, 0));
            document.add(getCenterAlignedParagraph("які виконали усі вимоги навчального плану освітньої програми", basefont, 0));
            document.add(getCenterAlignedParagraph("за період навчання", basefont, 30));

//            document.add(addContent(group));
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private Paragraph getCenterAlignedParagraph(String text, Font font, int spacingAfter) {
        Paragraph element = new Paragraph(text, font);
        element.setAlignment(Element.ALIGN_CENTER);
        element.setSpacingAfter(spacingAfter);
        return element;
    }

    private Element createTable() {


        return null;
    }
}
