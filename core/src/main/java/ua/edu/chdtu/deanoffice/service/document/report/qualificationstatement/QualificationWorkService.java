package ua.edu.chdtu.deanoffice.service.document.report.qualificationstatement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getJavaTempDirectory;

@Service
public class QualificationWorkService {
    private StudentGroupService studentGroupService;

    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    @Autowired
    public QualificationWorkService(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    public File createQualificationWorkStatementForGroup(int groupId) throws IOException, DocumentException {
        StudentGroup studentGroup = studentGroupService.getById(groupId);
        Document document = new Document(PageSize.A4, 28f, 28f, 28f, 28f);
        String filePath = getJavaTempDirectory() + "/" + "vidomist_qalificaciinoi_roboti" +
                studentGroup.getName() +
                getFileCreationDateAndTime() + ".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 14);
            document.add(createCenterAlignedParagraph("ЧЕРКАСЬКИЙ ДЕРЖАВНИЙ ТЕХНОЛОГІЧНИЙ УНІВЕРСИТЕТ", new Font(baseFont, 14, Font.BOLD), 0));
            document.add(createCenterAlignedParagraph(studentGroup.getSpecialization().getFaculty().getName().toUpperCase(), font, 0));
            document.add(createCenterAlignedParagraph("ВІДОМІСТЬ, КВАЛІФІКАЦІЙНОЇ РОБОТИ " + studentGroup.getSpecialization().getDegree().getName().toUpperCase(), font, 0));

        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private Paragraph createCenterAlignedParagraph(String text, Font font, int spacingAfter) {
        Paragraph element = new Paragraph(text, font);
        element.setAlignment(Element.ALIGN_CENTER);
        element.setSpacingAfter(spacingAfter);
        return element;
    }
}
