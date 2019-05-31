package ua.edu.chdtu.deanoffice.service.document.report.sheetsuccess.student;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class SheetSuccessService {
    public static final int COUNTCOLUMNS = 5;
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
    public void setFont() throws IOException, DocumentException {
        baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        FONT_14 = new Font(baseFont, FONT_SIZE_14, Font.NORMAL);
        FONT_14_BOLD = new Font(baseFont, FONT_SIZE_14, Font.BOLD);
        FONT_10 = new Font(baseFont,FONT_SIZE_10,Font.NORMAL);
        FONT_12 = new Font(baseFont, FONT_SIZE_12, Font.NORMAL);
    }

    public File formDocument(List<Integer> groupIds) throws IOException, DocumentException {
        setFont();
        Document document = new Document(PageSize.A4, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);
        String filePath = getJavaTempDirectory() + "/" + "name" +".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            fillDocument(document,groupIds);
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private void fillDocument(Document document, List<Integer> groupIds) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        //cell.setBorder(Rectangle.NO_BORDER);
        boolean isFrontSideActive = true;
        int numberForm = 1;
        int positionStart = -1;
        int positionEnd = 3;
        for (int i = 0; i < 9;i++){
            if(isFrontSideActive){
                addFrontForm(cell);
            } else {
                addBackForm(cell);
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

    private void addFrontForm(PdfPCell cell) throws DocumentException {
        cell.setPadding(0);
        Paragraph paragraph = new Paragraph(FrontFormConfig.CHDTU_NAME,FONT_14);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine(FrontFormConfig.FACULTY,FrontFormConfig.LENGTH_FACULTY,"",FONT_14));
        paragraph.add(addPhraseWithLine(FrontFormConfig.STUDY_YEAR,FrontFormConfig.LENGTH_STUDY_YEAR,"",FONT_14));
        paragraph.add(addPhraseWithLine("/",2,"",FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine(FrontFormConfig.YEAR,FrontFormConfig.LENGTH_YEAR,"",FONT_14));
        paragraph.add(addPhraseWithLine(FrontFormConfig.SEMESTER,FrontFormConfig.LENGTH_SEMESTER,"",FONT_14));
        paragraph.add(addPhraseWithLine(FrontFormConfig.GROUP,FrontFormConfig.LENGTH_GROUP,"",FONT_14));
        paragraph.add(addPhraseWithLine(FrontFormConfig.KNOWLEDGE_CONTROL,FrontFormConfig.LENGTH_KNOWLEDGE_CONTROL,"",FONT_14));
        cell.addElement(paragraph);
        cell.addElement(new Paragraph(" ",FONT_14));
        paragraph = new Paragraph(addPhraseWithLine(FrontFormConfig.TITLE,FrontFormConfig.LENGTH_TITLE,"",FONT_14_BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        cell.addElement(new Paragraph(" ",FONT_14));
        cell.addElement(addPhraseWithLine(FrontFormConfig.SRUDENT,FrontFormConfig.LENGTH_SRUDENT,"",FONT_12));
        paragraph = new Paragraph(FrontFormConfig.BY_LINE_SRUDENT,FONT_10);
        cell.addElement(paragraph);
        cell.addElement(addPhraseWithLine(FrontFormConfig.COURSE,FrontFormConfig.LENGTH_COURSE,"",FONT_12));
        paragraph = new Paragraph(FrontFormConfig.BY_LINE_COURSE,FONT_10);
        cell.addElement(paragraph);
        cell.addElement(addPhraseWithLine(FrontFormConfig.TEACHER,FrontFormConfig.LENGTH_TEACHER,"",FONT_12));
        paragraph = new Paragraph(FrontFormConfig.BY_LINE_TEACHER,FONT_10);
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

    private void addBackForm(PdfPCell cell) throws DocumentException {
        cell.setPadding(0);
        float[] arrayWidthColumns = new float[COUNTCOLUMNS];
        setWidthColumns(arrayWidthColumns);
        PdfPTable table = new PdfPTable(COUNTCOLUMNS);
        table.setWidthPercentage(100);
        table.setTotalWidth(arrayWidthColumns);
        fillTable(table);
        cell.addElement(table);
        cell.addElement(new Paragraph(" ",FONT_12));
        Paragraph paragraph = new Paragraph(addPhraseWithLine(BackFormConfig.REASON,BackFormConfig.LENGTH_REASON,"",FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine(BackFormConfig.SHEET,BackFormConfig.LENGTH_SHEET,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackFormConfig.SHEET_END,BackFormConfig.LENGTH_SHEET_END,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackFormConfig.YEAR,BackFormConfig.LENGTH_YEAR,"",FONT_14));
        paragraph.add(new Phrase(BackFormConfig.YEAR_MARK,FONT_14));
        cell.addElement(paragraph);
        cell.addElement(new Paragraph("",FONT_12));
        paragraph = new Paragraph(addPhraseWithLine(BackFormConfig.DEAN,BackFormConfig.LENGTH_DEAN,"      ",FONT_14));
        paragraph.add(addPhraseWithLine(" ",BackFormConfig.LENGTH_SPACE,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackFormConfig.BRACKET_OPEN,BackFormConfig.LENGTH_BRACKET_OPEN,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackFormConfig.BRACKET_CLOSE,BackFormConfig.LENGTH_BRACKET_CLOSE,"",FONT_14));
        paragraph.add(addPhraseWithLine(BackFormConfig.YEAR,BackFormConfig.LENGTH_YEAR,"",FONT_14));
        paragraph.add(new Phrase(BackFormConfig.YEAR_MARK,FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(BackFormConfig.BY_LINE,FONT_14);
        paragraph.setLeading(15);
        cell.addElement(paragraph);
        cell.setFixedHeight(HEIGHT_TABLE_PAGE);
    }

    private void fillTable(PdfPTable table) throws DocumentException {
        table.addCell(fillCell(BackFormConfig.INITIALS));
        table.addCell(fillCell(BackFormConfig.NUMBER));
        PdfPCell cell = new PdfPCell(fillInternalTable());
        cell.setPadding(0);
        cell.setFixedHeight(70);
        table.addCell(cell);
        table.addCell(fillCell(BackFormConfig.DATE));
        table.addCell(fillCell(BackFormConfig.SIGN));
    }

    private PdfPTable fillInternalTable() throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = fillCell(BackFormConfig.GRADE);
        cell.setPaddingBottom(PADDING_BOTTOM);
        table.addCell(cell);
        PdfPTable iternalTable = new PdfPTable(3);
        float[] arrayWidthColumns = {50,25,25};
        iternalTable.setTotalWidth(arrayWidthColumns);
        iternalTable.addCell(fillCell(BackFormConfig.NATIONAL));
        iternalTable.addCell(fillCell(BackFormConfig.HUNDRED_SCALE));
        iternalTable.addCell(fillCell(BackFormConfig.ECTS));
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
       arrayWidthColumns[0] = 15;
       arrayWidthColumns[1] = 15;
       arrayWidthColumns[2] = 45;
       arrayWidthColumns[3] = 10;
       arrayWidthColumns[4] = 15;
    }

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }
}
