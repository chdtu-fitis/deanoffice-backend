package ua.edu.chdtu.deanoffice.service.document.report.journal.rating;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;
@Service
public class FormRatingPDFService extends FormRatingBase {
    private final int FONT_SIZE = FONT_SIZE_14/2;
    private final int WIDTH_FIRST_ROW = 2;
    private final int WIDTH_SECOND_ROW = 13;
    private final int PADDING_BOTTOM = 4;
    private final int PADDING_TOP = 1;
    private Font FONT;
    private Font FONT_TITLE;
    @Value(value = "classpath:fonts/timesnewroman/times.ttf")
    private Resource ttf;

//    @Autowired
    public void setFont() throws IOException, DocumentException {
        BaseFont baseFont = BaseFont.createFont(ttf.getURI().toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        FONT = new Font(baseFont, FONT_SIZE, Font.NORMAL);
        FONT_TITLE = new Font(baseFont, FONT_SIZE, Font.BOLD);
    }

    public File formDocument(
            Integer degreeId,
            Integer year,
            int facultyId,
            String tuitionFormText,
            Integer semester
    ) throws IOException, DocumentException {
        TuitionForm tuitionForm = TuitionForm.valueOf(tuitionFormText);
        setFont();
        List<StudentGroup> studentGroups = groupService.getGroupsByDegreeAndYearAndTuitionForm(degreeId, year, facultyId, tuitionForm);
        String fileName = transliterate(JOURNAL+year+KURS);
        return createTables(semester, studentGroups, fileName);
    }

    private File createTables(Integer semester, List<StudentGroup> studentGroups, String fileName) throws FileNotFoundException, DocumentException {
        com.itextpdf.text.Document document = new Document(PageSize.A4.rotate(), 28f, 28f, 28f, 28f);
        String filePath = getJavaTempDirectory() + "/" + fileName +".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            fillDocument(document,semester,studentGroups);
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private void fillDocument(Document document,Integer semester, List<StudentGroup> studentGroups) throws DocumentException {
        List<String> namesStudents = new ArrayList<>();
        List<String> namesCourses = new ArrayList<>();
        for(StudentGroup studentGroup: studentGroups) {
            getDataFromDataBase(studentGroup, semester, namesStudents, namesCourses);
            int countColumns = namesCourses.size()+2;
            float[] arrayWidthColumns = new float[countColumns];
            setWidthColumns(arrayWidthColumns);
            PdfPTable table = new PdfPTable(countColumns);
            table.setWidthPercentage(100);
            document.newPage();
            setTitle(document,studentGroup);
            if(namesCourses.size() != 0) {
                fillFirstRow(namesCourses,table,arrayWidthColumns);
                fillOtherRows(namesStudents,namesCourses,table);
            }
            document.add(table);
        }
    }

    private void fillOtherRows(List<String> namesStudents,List<String> namesCourses, PdfPTable table) {
        for(int i=0;i<namesStudents.size();i++){
            PdfPCell cell = new PdfPCell(new Phrase((i+1)+".",FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(PADDING_BOTTOM);
            cell.setPaddingTop(PADDING_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(namesStudents.get(i),FONT));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            addEmptyCells(table,namesCourses);
        }
    }

    private void addEmptyCells(PdfPTable table, List<String> namesCourses) {
        for(int i=0;i<namesCourses.size();i++){
            table.addCell("");
        }
    }

    private void fillFirstRow(List<String> namesCourses, PdfPTable table,float[] arrayWidthColumns) throws DocumentException {
        table.addCell("");
        table.addCell("");
        table.setTotalWidth(arrayWidthColumns);
        for(int i=0;i<namesCourses.size();i++){
            PdfPCell cell = new PdfPCell(new Phrase(namesCourses.get(i),FONT));
            cell.setRotation(90);
            cell.setFixedHeight(HEIGHT_FIRST_ROW);
            table.addCell(cell);
        }
    }

    private void setWidthColumns(float[] arrayWidthColumns) {
        float widthOtherRows = (100 - WIDTH_FIRST_ROW - WIDTH_SECOND_ROW)/ arrayWidthColumns.length-2;
        arrayWidthColumns[0] = WIDTH_FIRST_ROW;
        arrayWidthColumns[1] = WIDTH_SECOND_ROW;
        for(int i=2;i<arrayWidthColumns.length;i++){
            arrayWidthColumns[i] = widthOtherRows;
        }
    }

    private void setTitle(Document document, StudentGroup studentGroup) throws DocumentException {
        Paragraph purpose = new Paragraph(studentGroup.getName(), FONT_TITLE);
        purpose.setSpacingAfter(FONT_SIZE);
        document.add(purpose);
    }


    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

}
