package ua.edu.chdtu.deanoffice.service.document.report.exam.ssc;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;

import java.io.*;
import java.util.List;

@Service
public class SingleStudentAndCourseExamReportService {
    public static final int COLUMNS_COUNT = 5;
    public static final float PAGE_MARGIN = 36f;
    public static final float HEIGHT_TABLE_PAGE = 250;
    public static final float HEIGHT_FIXED = 50;
    public static final float UNDERLINE_THICKNESS = 0.7f;
    public static final float FONT_SIZE_14 = 14f;
    public static final float FONT_SIZE_12 = 12f;
    public static final float FONT_SIZE_10 = 10f;
    public static final float PADDING_BOTTOM = 5;
    private Font FONT_14;
    private Font FONT_14_BOLD;
    private Font FONT_12;
    private Font FONT_10;
    private BaseFont baseFont;
    @Value(value = "classpath:fonts/timesnewroman/times.ttf")
    private Resource ttf;

    @Autowired
    private StudentDegreeService studentDegreeService;
    @Autowired
    private CourseService courseService;
    @Autowired
    CourseForGroupService courseForGroupService;
    @Autowired
    private CurrentYearService currentYearService;

//    @Autowired
    public void setFont() throws IOException, DocumentException {
        baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        FONT_14 = new Font(baseFont, FONT_SIZE_14, Font.NORMAL);
        FONT_14_BOLD = new Font(baseFont, FONT_SIZE_14, Font.BOLD);
        FONT_10 = new Font(baseFont,FONT_SIZE_10,Font.NORMAL);
        FONT_12 = new Font(baseFont, FONT_SIZE_12, Font.NORMAL);
    }

    public File formDocument(List<Integer> studentIds, List<Integer> courseIds) throws IOException, DocumentException {
        setFont();
        Document document = new Document(PageSize.A4, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);
        String filePath = getJavaTempDirectory() + "/" + "name" +".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            fillDocument(document,studentIds,courseIds);
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private void fillDocument(Document document, List<Integer> studentIds, List<Integer> courseIds) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        boolean isFrontSideActive = true;
        int numberForm = 1;
        int positionStart = -1;
        int positionEnd = 3;
        for (int i = 0; i < studentIds.size();i++){
            if(isFrontSideActive){
                addFrontForm(cell,studentIds.get(i),courseIds.get(i));
            } else {
                addBackForm(cell,studentIds.get(i),courseIds.get(i));
            }
            numberForm++;
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setFixedHeight(HEIGHT_TABLE_PAGE);
            cell.setBorder(Rectangle.NO_BORDER);
            if(numberForm > 3 || i == 9-1){
                if(isFrontSideActive) {
                    isFrontSideActive = !isFrontSideActive;
                    positionEnd = i;
                    i = positionStart;
                    if(numberForm == 2){
                        table.addCell(cell);
                        table.addCell(cell);
                    }
                    if(numberForm == 3){
                        table.addCell(cell);
                    }
                } else {
                    isFrontSideActive = !isFrontSideActive;
                    positionStart = positionEnd;
                    i = positionEnd;
                }
                numberForm = 1;
            }
        }
        document.add(table);
    }

    private void addFrontForm(PdfPCell cell, Integer studentId, Integer courseId) throws DocumentException {
        StudentDegree student = studentDegreeService.getById(studentId);
        Course course = courseService.getById(courseId);
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(student.getStudentGroup().getId(),courseId);
        cell.setPadding(0);
        Paragraph paragraph = new Paragraph(FrontSideConfig.CHDTU_NAME,FONT_14);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine(FrontSideConfig.FACULTY, FrontSideConfig.LENGTH_FACULTY,student.getSpecialization().getFaculty().getAbbr(),FONT_14));
        paragraph.add(addPhraseWithLine(FrontSideConfig.STUDY_YEAR, FrontSideConfig.LENGTH_STUDY_YEAR,"20",FONT_14));
        paragraph.add(addPhraseWithLine("/",2,String.valueOf(currentYearService.get().getCurrYear()).substring(2,4),FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine(FrontSideConfig.YEAR, FrontSideConfig.LENGTH_YEAR, String.valueOf(currentYearService.get().getCurrYear() - student.getStudentGroup().getCreationYear() + 1),FONT_14));
        paragraph.add(addPhraseWithLine(FrontSideConfig.SEMESTER, FrontSideConfig.LENGTH_SEMESTER,dividesByTwo(course.getSemester().intValue()),FONT_14));
        paragraph.add(addPhraseWithLine(FrontSideConfig.GROUP, FrontSideConfig.LENGTH_GROUP,student.getStudentGroup().getName(),FONT_14));
        paragraph.add(addPhraseWithLine(FrontSideConfig.KNOWLEDGE_CONTROL, FrontSideConfig.LENGTH_KNOWLEDGE_CONTROL,course.getKnowledgeControl().getName(),FONT_14));
        cell.addElement(paragraph);
        cell.addElement(new Paragraph(" ",FONT_14));
        paragraph = new Paragraph(addPhraseWithLine(FrontSideConfig.TITLE, FrontSideConfig.LENGTH_TITLE,"",FONT_14_BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        cell.addElement(new Paragraph(" ",FONT_14));
        cell.addElement(addPhraseWithLine(FrontSideConfig.SRUDENT, FrontSideConfig.LENGTH_SRUDENT,student.getStudent().getInitialsUkr(),FONT_12));
        paragraph = new Paragraph(FrontSideConfig.BY_LINE_SRUDENT,FONT_10);
        cell.addElement(paragraph);
        cell.addElement(addPhraseWithLine(FrontSideConfig.COURSE, FrontSideConfig.LENGTH_COURSE,course.getCourseName().getName(),FONT_12));
        paragraph = new Paragraph(FrontSideConfig.BY_LINE_COURSE,FONT_10);
        cell.addElement(paragraph);
        cell.addElement(addPhraseWithLine(FrontSideConfig.TEACHER, FrontSideConfig.LENGTH_TEACHER,(courseForGroup.getTeacher() != null?courseForGroup.getTeacher().getInitialsUkr():""),FONT_12));
        paragraph = new Paragraph(FrontSideConfig.BY_LINE_TEACHER,FONT_10);
        cell.addElement(paragraph);
        cell.setFixedHeight(HEIGHT_TABLE_PAGE);
    }

    private Phrase addPhraseWithLine(String text, int length, String textLine,Font font) {
        String underlineText = "";
        for(int i = 0; i <= length-1;i++) {
            if (i < textLine.length()) {
                underlineText += textLine.charAt(i);
            }
            else {
                underlineText += "_";
            }
        }
        Phrase phrase = new Phrase(text,font);
        Chunk chunk = new Chunk(underlineText);
        chunk.setFont(font);
        float yPosition = font.getSize() == FONT_SIZE_14 ? -2.9f : -2.2f;
        chunk.setUnderline(UNDERLINE_THICKNESS, yPosition);
        phrase.add(new Phrase(chunk));
        return phrase;
    }

    private String dividesByTwo(int a){
        if (a%2==0){
            return "2";
        } else {
            return "1";
        }
    }

    private void addBackForm(PdfPCell cell, Integer studentId, Integer courseId) throws DocumentException {
        StudentDegree student = studentDegreeService.getById(studentId);
        cell.setPadding(0);
        float[] arrayWidthColumns = new float[COLUMNS_COUNT];
        setWidthColumns(arrayWidthColumns);
        PdfPTable table = new PdfPTable(COLUMNS_COUNT);
        table.setWidthPercentage(100);
        table.setTotalWidth(arrayWidthColumns);
        fillTable(table,student);
        cell.addElement(table);
        cell.addElement(new Paragraph(" ",FONT_12));
        Paragraph paragraph = new Paragraph(addPhraseWithLine(BackSideConfig.REASON, BackSideConfig.LENGTH_REASON,"",FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine(BackSideConfig.SHEET, BackSideConfig.LENGTH_SHEET,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackSideConfig.SHEET_END, BackSideConfig.LENGTH_SHEET_END,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackSideConfig.YEAR, BackSideConfig.LENGTH_YEAR,"",FONT_14));
        paragraph.add(new Phrase(BackSideConfig.YEAR_MARK,FONT_14));
        cell.addElement(paragraph);
        cell.addElement(new Paragraph("",FONT_12));
        paragraph = new Paragraph(addPhraseWithLine(BackSideConfig.DEAN, BackSideConfig.LENGTH_DEAN,"      ",FONT_14));
        paragraph.add(addPhraseWithLine(" ", BackSideConfig.LENGTH_SPACE,convertFullNameToIntials(student.getSpecialization().getFaculty().getDean()),FONT_14));
        paragraph.add(addPhraseWithLine(BackSideConfig.BRACKET_OPEN, BackSideConfig.LENGTH_BRACKET_OPEN,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackSideConfig.BRACKET_CLOSE, BackSideConfig.LENGTH_BRACKET_CLOSE,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackSideConfig.YEAR, BackSideConfig.LENGTH_YEAR,"",FONT_14));
        paragraph.add(new Phrase(BackSideConfig.YEAR_MARK,FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(BackSideConfig.BY_LINE,FONT_14);
        paragraph.setLeading(15);
        cell.addElement(paragraph);
        cell.setFixedHeight(HEIGHT_TABLE_PAGE);
    }

    private void fillTable(PdfPTable table, StudentDegree student) throws DocumentException {
        table.addCell(fillCell(BackSideConfig.INITIALS));
        table.addCell(fillCell(BackSideConfig.NUMBER));
        PdfPCell cell = new PdfPCell(fillInternalTable());
        cell.setPadding(0);
        cell.setFixedHeight(70);
        table.addCell(cell);
        table.addCell(fillCell(BackSideConfig.DATE));
        table.addCell(fillCell(BackSideConfig.SIGN));

        table.addCell(fillCell(student.getStudent().getInitialsUkr()));
        table.addCell(fillCell(student.getRecordBookNumber()));
        cell = new PdfPCell(fillInternalTwoRowTable());
        cell.setPadding(0);
        cell.setFixedHeight(70);
        table.addCell(cell);

        table.addCell("");
        table.addCell("");
    }

    private PdfPTable fillInternalTwoRowTable() throws DocumentException {
        PdfPCell cell;
        PdfPTable iternalTable = new PdfPTable(3);
        float[] arrayWidthColumns = {50,25,25};
        iternalTable.setTotalWidth(arrayWidthColumns);
        iternalTable.addCell(fillCell(""));
        iternalTable.addCell(fillCell(""));
        iternalTable.addCell(fillCell(""));
        cell = new PdfPCell(iternalTable);
        cell.setFixedHeight(HEIGHT_FIXED);
        cell.setPadding(0);
        return iternalTable;
    }

    private PdfPTable fillInternalTable() throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = fillCell(BackSideConfig.GRADE);
        cell.setPaddingBottom(PADDING_BOTTOM);
        table.addCell(cell);
        PdfPTable iternalTable = new PdfPTable(3);
        float[] arrayWidthColumns = {50,25,25};
        iternalTable.setTotalWidth(arrayWidthColumns);
        iternalTable.addCell(fillCell(BackSideConfig.NATIONAL));
        iternalTable.addCell(fillCell(BackSideConfig.HUNDRED_SCALE));
        iternalTable.addCell(fillCell(BackSideConfig.ECTS));
        cell = new PdfPCell(iternalTable);
        cell.setFixedHeight(HEIGHT_FIXED);
        cell.setPadding(0);
        table.addCell(cell);
        return table;
    }


    private PdfPCell fillCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,FONT_12));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private void setWidthColumns(float[] arrayWidthColumns) {
       arrayWidthColumns[0] = 19;
       arrayWidthColumns[1] = 15;
       arrayWidthColumns[2] = 40;
       arrayWidthColumns[3] = 13;
       arrayWidthColumns[4] = 13;
    }

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    private String convertFullNameToIntials(String fullName){
        String[] nameParts = fullName.split(" ");
        char middleInitial = nameParts[1].charAt(0);
        char lastInitial = nameParts[2].charAt(0);
        return nameParts[0] + " " + middleInitial+"." + lastInitial+".";
    }
}
