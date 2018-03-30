package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.service.FileFormatEnum;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DocumentIOService {

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    public WordprocessingMLPackage loadTemplate(String name) throws IOException, Docx4JException {
        return WordprocessingMLPackage.load(new FileInputStream(new ClassPathResource(name).getFile()));
    }

    public SpreadsheetMLPackage loadSpreadsheetDocument(String name) throws IOException, Docx4JException {
        return SpreadsheetMLPackage.load(new FileInputStream(new ClassPathResource(name).getFile()));
    }

    public SpreadsheetMLPackage loadSpreadsheetDocument(InputStream xlsxInputStream) throws Docx4JException {
        return SpreadsheetMLPackage.load(xlsxInputStream);
    }

    public File saveDocumentToTemp(WordprocessingMLPackage document, String fileName, FileFormatEnum format)
            throws Docx4JException, FileNotFoundException {
        return saveDocument(document, fileName, format);
    }

    public File saveDocumentToTemp(SpreadsheetMLPackage document, String fileName, FileFormatEnum format)
            throws Docx4JException, FileNotFoundException {
        return saveDocument(document, fileName, format);
    }

    private File saveDocument(Object document, String fileName, FileFormatEnum format)
            throws Docx4JException, FileNotFoundException {

        String filePath = getJavaTempDirectory() + cleanFileName(fileName) + getFileCreationDateAndTime();
        File documentFile = null;

        if (document instanceof WordprocessingMLPackage) {
            if (FileFormatEnum.PDF.equals(format)) {
                documentFile = new File(filePath + ".pdf");
                Docx4J.toPDF(((WordprocessingMLPackage) document), new FileOutputStream(documentFile));
            } else {
                documentFile = new File(filePath + ".docx");
                ((WordprocessingMLPackage) document).save(documentFile);
            }
        } else if (document instanceof SpreadsheetMLPackage) {
            documentFile = new File(filePath + ".xlsx");
            ((SpreadsheetMLPackage) document).save(documentFile);
        }

        return documentFile;
    }

    private String getFileCreationDateAndTime() {
        DateFormat format = new SimpleDateFormat(" dd-MM-yyyy mm:ss");
        return format.format(new Date());
    }

    public String cleanFileName(String fileName) {
        return fileName.replaceAll("[\\W]*", "");
    }
}
