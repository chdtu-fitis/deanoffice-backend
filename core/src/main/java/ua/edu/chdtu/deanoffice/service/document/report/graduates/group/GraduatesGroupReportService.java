package ua.edu.chdtu.deanoffice.service.document.report.graduates.group;

import com.itextpdf.text.Chunk;
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
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import javax.swing.border.Border;
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
            document.add(createGroupAttributeTable(basefont, createGroupAttributeBean(groupId)));
            document.add(createTable(new Font(baseFont, 10)));
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

    private PdfPTable createGroupAttributeTable(Font font, GroupAttributeBean bean) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setTotalWidth(new float[]{150, 250});
        mainTable.setLockedWidth(true);
        ArrayList<PdfPCell> cells = new ArrayList<>();
        cells.add(new PdfPCell(new Paragraph("   Освітній рівень:", font)));
        cells.add(new PdfPCell(new Paragraph(bean.getDegree(), font)));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("   Спеціальність:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph(bean.getSpeciality(), font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("   Освітня програма:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph(bean.getSpecialization(), font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("   Академічна група:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph(bean.getAcademicGroup(), font))));
        for (PdfPCell cell : cells) {
            cell.setBorder(0);
            mainTable.addCell(cell);
        }
        mainTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        return mainTable;
    }

    private GroupAttributeBean createGroupAttributeBean(int groupId) {
        GroupAttributeBean bean = new GroupAttributeBean();
        StudentGroup group = studentGroupService.getById(groupId);
        Specialization specialization = group.getSpecialization();
        bean.setSpecialization(specialization.getName());
        bean.setDegree(specialization.getDegree().getName());
        bean.setSpeciality(specialization.getSpeciality().getName());
        bean.setAcademicGroup(group.getName());
        return bean;
    }

    private PdfPTable createTable(Font font) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(4);
        mainTable.setSpacingBefore(10);
        float[] columnWidths = {1f, 10f, 2f, 10f};
        mainTable.setWidths(columnWidths);
        mainTable.setWidthPercentage(100);
        PdfPCell number = new PdfPCell();
        Paragraph numberParagraph = new Paragraph("№ з/п", font);
        numberParagraph.setAlignment(Element.ALIGN_CENTER);
        number.addElement(numberParagraph);
        mainTable.addCell(number);
        PdfPCell pip = new PdfPCell();
        Paragraph pipParagraph = new Paragraph("ПІБ здобувача", font);
        pipParagraph.setAlignment(Element.ALIGN_CENTER);
        pip.addElement(pipParagraph);
        mainTable.addCell(pip);
        PdfPCell eBook = new PdfPCell();
        Paragraph eBookParagraph = new Paragraph("№ залікової книжки", font);
        eBookParagraph.setAlignment(Element.ALIGN_CENTER);
        eBook.addElement(eBookParagraph);
        mainTable.addCell(eBook);

/////////////////////////////////////////////////////////////////////////////////////////////////
        PdfPTable achievements = new PdfPTable(2);
        achievements.setWidths(new float[]{3f, 0.9f});
        achievements.setWidthPercentage(100);

        PdfPCell achievementCell = new PdfPCell();
        Paragraph achievementParagraph = new Paragraph("Навчальні досягнення здобувача", font);
        achievementParagraph.setAlignment(Element.ALIGN_CENTER);
        achievementCell.addElement(achievementParagraph);
        achievementCell.setColspan(2);
        achievementCell.setBorder(0);
        achievements.addCell(achievementCell);

/////////////////////////////////////////////////////////////////////////////////////////////////
        PdfPTable grades = new PdfPTable(3);
        grades.setWidthPercentage(100);
        grades.setSplitRows(false);

        PdfPCell dividingGrades = new PdfPCell();
        Paragraph dividingGradesParagraph = new Paragraph("Розподіл оцінок", font);
        dividingGradesParagraph.setAlignment(Element.ALIGN_CENTER);
        dividingGrades.addElement(dividingGradesParagraph);
        dividingGrades.setColspan(3);
        dividingGrades.setBorder(0);

        grades.addCell(dividingGrades);

        PdfPCell a = new PdfPCell();
        Paragraph aParagraph = new Paragraph("Відмінно, %", font);
        aParagraph.setAlignment(Element.ALIGN_CENTER);
        a.addElement(aParagraph);
        PdfPCell b = new PdfPCell();
        Paragraph bParagraph = new Paragraph("Добре, %", font);
        bParagraph.setAlignment(Element.ALIGN_CENTER);
        b.addElement(bParagraph);
        PdfPCell e = new PdfPCell();
        Paragraph eParagraph = new Paragraph("Задовільно, %", font);
        eParagraph.setAlignment(Element.ALIGN_CENTER);
        e.addElement(eParagraph);

        grades.addCell(a);
        grades.addCell(b);
        grades.addCell(e);

////////////////////////////////////////////////////////////////////////////////////////////////
        PdfPCell tt1 = new PdfPCell();
        tt1.setPadding(0);
        tt1.addElement(grades);
        achievements.addCell(tt1);

        PdfPCell middleScore = new PdfPCell();
        Paragraph middleScoreParagraph = new Paragraph("Середній бал", font);
        middleScoreParagraph.setAlignment(Element.ALIGN_CENTER);
        middleScore.addElement(middleScoreParagraph);
        achievements.addCell(middleScore);

        PdfPCell tt = new PdfPCell();
        tt.setPadding(0);
        tt.addElement(achievements);
        mainTable.addCell(tt);
        return mainTable;
    }
}
