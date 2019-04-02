package ua.edu.chdtu.deanoffice.service.document.report.graduates.group;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
            document.add(getCenterAlignedParagraph("ФАКУЛЬТЕТ ЕЛЕКТРОННИХ ТЕХНОЛОГІЙ І РОБОТОТЕХНІКИ", basefont, 50));
            document.add(getCenterAlignedParagraph("ВІДОМІСТЬ", new Font(baseFont, 14, Font.BOLD), 0));
            document.add(getCenterAlignedParagraph("навчальних досягнень здобувачів вищої освіти,", basefont, 0));
            document.add(getCenterAlignedParagraph("які виконали усі вимоги навчального плану освітньої програми", basefont, 0));
            document.add(getCenterAlignedParagraph("за період навчання", basefont, 30));
            document.add(createGroupAttributeTable(basefont));
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

    private PdfPTable createGroupAttributeTable(Font font) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setTotalWidth(new float[]{130, 250});
        mainTable.setLockedWidth(true);
        ArrayList<PdfPCell> cells = new ArrayList<>();
        cells.add(new PdfPCell(new Paragraph("Освітній рівень:", font)));
        cells.add(new PdfPCell(new Paragraph("бакалаврський", font)));
        cells.add(new PdfPCell(new Paragraph("Строк навчання:", font)));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("01.09.2015-30.06.2019", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("Спеціальність:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("172 Телекомунікації та радіотехніка", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("Освітня програма:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("Телекомунікації", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("Академічна група:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("ТК-56", font))));
        for (PdfPCell cell : cells) {
            cell.setBorder(0);
            mainTable.addCell(cell);
        }
        mainTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.setSpacingBefore(10);
        return mainTable;
    }
}
