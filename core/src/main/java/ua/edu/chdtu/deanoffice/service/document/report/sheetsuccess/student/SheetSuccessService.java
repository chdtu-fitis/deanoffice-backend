package ua.edu.chdtu.deanoffice.service.document.report.sheetsuccess.student;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class SheetSuccessService {
    public static final String TEMPLATES_PATH = "C:/Projects/deanoffice/SheetSuccessFrontSide.pdf";
    public static final int COUNTCOLUMNS = 5;
    public static final float PAGE_MARGIN = 36f;
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
        FONT_14 = new Font(baseFont, 14, Font.NORMAL);
        FONT_14_BOLD = new Font(baseFont, 14, Font.BOLD);
        FONT_10 = new Font(baseFont,10,Font.NORMAL);
        FONT_12 = new Font(baseFont, 12, Font.NORMAL);
    }

    public File formDocument() throws IOException, DocumentException {
        setFont();
        Document document = new Document(PageSize.A4, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);
        String filePath = getJavaTempDirectory() + "/" + "name" +".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            fillDocument(document);
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private void fillDocument(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell pdfPCell = new PdfPCell();
        addFrontForm(pdfPCell);
        table.addCell(pdfPCell);
        pdfPCell = new PdfPCell();
        addBackForm(pdfPCell);
        table.addCell(pdfPCell);
        pdfPCell = new PdfPCell();
        addFrontForm(pdfPCell);
        table.addCell(pdfPCell);
        document.add(table);
    }

    private void addFrontForm(PdfPCell cell) throws DocumentException {
        cell.setPadding(0);
        Paragraph paragraph = new Paragraph("ЧЕРКАСЬКИЙ ДЕРЖАВНИЙ ТЕХНОЛОГІЧНИЙ УНІВЕРСИТЕТ",FONT_14);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine("Факультет",34,"",FONT_14));
        paragraph.add(addPhraseWithLine(" Навчальний рік",2,"",FONT_14));
        paragraph.add(addPhraseWithLine("/",2,"",FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine("Курс",3,"",FONT_14));
        paragraph.add(addPhraseWithLine(" Семестр",3,"",FONT_14));
        paragraph.add(addPhraseWithLine(" Група",10,"",FONT_14));
        paragraph.add(addPhraseWithLine(" Форма контролю",25,"",FONT_14));
        cell.addElement(paragraph);
        cell.addElement(new Paragraph(" ",FONT_14));
        paragraph = new Paragraph(addPhraseWithLine("АРКУШ УСПІШНОСТІ СТУДЕНТА №",7,"",FONT_14_BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        cell.addElement(new Paragraph(" ",FONT_14));
        cell.addElement(addPhraseWithLine("Студент",79,"",FONT_12));
        paragraph = new Paragraph("                                                                      прізвище та ініціали студента",FONT_10);
        cell.addElement(paragraph);
        cell.addElement(addPhraseWithLine("Навчальна дисципліна",67,"",FONT_12));
        paragraph = new Paragraph("                                                                      назва навчальної дисципліни",FONT_10);
        cell.addElement(paragraph);
        cell.addElement(addPhraseWithLine("Викладач",78,"",FONT_12));
        paragraph = new Paragraph("                                                                      вчене звання, прізвище та ініціали",FONT_10);
        cell.addElement(paragraph);
        cell.setFixedHeight(250);
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
        float yPosition = font.getSize() == 14 ? -2.9f : -2.2f;
        chunk.setUnderline(0.7f, yPosition);
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
        Paragraph paragraph = new Paragraph(addPhraseWithLine("Причина перенесення підсумкового контролю  ",33,"",FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph(addPhraseWithLine("Аркуш успішності дійсний до “",3,"",FONT_14));
        paragraph.add(addPhraseWithLine("” ",15,"",FONT_14));
        paragraph.add(addPhraseWithLine(" 20",2,"",FONT_14));
        paragraph.add(new Phrase(" р.",FONT_14));
        cell.addElement(paragraph);
        cell.addElement(new Paragraph("",FONT_12));
        paragraph = new Paragraph(addPhraseWithLine("Декан факультету",15,"      ",FONT_14));
        paragraph.add(addPhraseWithLine(" ",22,"",FONT_14));
        paragraph.add(addPhraseWithLine(" “",3,"",FONT_14));
        paragraph.add(addPhraseWithLine("” ",10,"",FONT_14));
        paragraph.add(addPhraseWithLine(" 20",2,"",FONT_14));
        paragraph.add(new Phrase(" р.",FONT_14));
        cell.addElement(paragraph);
        paragraph = new Paragraph("                                   (підпис)        (прізвище та ініціали)",FONT_14);
        paragraph.setLeading(15);
        cell.addElement(paragraph);
        cell.setFixedHeight(300);
    }

    private void fillTable(PdfPTable table) throws DocumentException {
        table.addCell(fillCell("ПІБ студента"));
        table.addCell(fillCell("Номер залікової книжки"));
        PdfPCell cell = new PdfPCell(fillInternalTable());
        cell.setPadding(0);
        cell.setFixedHeight(70);
        table.addCell(cell);
        table.addCell(fillCell("Дата"));
        table.addCell(fillCell("Підпис викладача"));
    }

    private PdfPTable fillInternalTable() throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = fillCell("Оцінка");
        cell.setPaddingBottom(5);
        table.addCell(cell);
        PdfPTable iternalTable = new PdfPTable(3);
        float[] arrayWidthColumns = {50,25,25};
        iternalTable.setTotalWidth(arrayWidthColumns);
        iternalTable.addCell(fillCell("за національною шкалою"));
        iternalTable.addCell(fillCell("100-бальна шкала"));
        iternalTable.addCell(fillCell("ЄКТС"));
        cell = new PdfPCell(iternalTable);
        cell.setFixedHeight(50);
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
