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
    private Font FONT_14;
    private Font FONT_12;
    private BaseFont baseFont;
    @Value(value = "classpath:fonts/timesnewroman/times.ttf")
    private Resource ttf;

    @Autowired
    public void setFont() throws IOException, DocumentException {
        baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        FONT_14 = new Font(baseFont, 14, Font.NORMAL);
        FONT_12 = new Font(baseFont, 12, Font.NORMAL);
    }

    public File formDocument() throws IOException, DocumentException {
        setFont();
        Document document = new Document(PageSize.A4, 36f, 36f, 28f, 28f);
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
        fillFrontPages(document);
        fillBackPages(document);
    }

    private void fillFrontPages(Document document) throws DocumentException {
        addFrontForm(document);
    }

    private void addFrontForm(Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph("Черкаський державний технологічний університет",FONT_14);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Paragraph(" ",FONT_14));
        paragraph = new Paragraph(addPhraseWithLine("АРКУШ УСПІШНОСТІ СТУДЕНТА №",7,"dsf",FONT_14));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        paragraph = new Paragraph(addPhraseWithLine("Факультет",34,"",FONT_12));
        paragraph.add(addPhraseWithLine(" Група",18,"",FONT_12));
        document.add(paragraph);
        document.add(addPhraseWithLine("Курс",19,"",FONT_12));
        document.add(new Paragraph(addPhraseWithLine("Кафедра ",68,"",FONT_12)));
        document.add(new Paragraph(addPhraseWithLine("Навчальна   дисципліна       ",52,"Кононенко О. В.",FONT_12)));
        document.add(new Paragraph("Листок викладач здає особисто в  деканат  у день приймання підсумкового контролю.",FONT_12));
        document.add(new Paragraph("Передавати листок через інших осіб категорично забороняється.",FONT_12));
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

    private void fillBackPages(Document document) throws DocumentException {
        addBackForm(document);
    }

    private void addBackForm(Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph(addPhraseWithLine("Листок дійсний  до “",2,"",FONT_12));
        paragraph.add(addPhraseWithLine("” ",12,"",FONT_12));
        paragraph.add(addPhraseWithLine(" 20",2,"",FONT_12));
        paragraph.add(new Phrase(" р. Причина перенесення підсумкового контролю  ",FONT_12));
        document.add(paragraph);
        document.add(new Paragraph("_____________________________________________________________________________",FONT_12));

    }


    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }
}
