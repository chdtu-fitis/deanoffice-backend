package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DocumentIOService {

    public static final String TEMPLATES_PATH = "/docs/templates/";

    public WordprocessingMLPackage loadTemplate(String name) throws Docx4JException {
        InputStream inputStream = getClass().getResourceAsStream(name);
        return WordprocessingMLPackage.load(inputStream);
    }

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
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

        String filePath = getJavaTempDirectory() + "/" + cleanFileName(fileName) + getFileCreationDateAndTime();
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
        DateFormat dateFormat = new SimpleDateFormat(" dd-MM-yyyy HH-mm");
        System.out.println(dateFormat.format(new Date()));
        return dateFormat.format(new Date());
    }

    public String cleanFileName(String fileName) {
        String result = new String(fileName);
        result = result.replaceAll(" +", " ");
        result = result.replaceAll("[^a-zA-Z0-9_]+", "");
        return result;
    }
}
