package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

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

    public File saveDocxToTemp(WordprocessingMLPackage template, String fileName)
            throws Docx4JException {
        String finalFileName = cleanFileName(fileName) + getFileCreationDateAndTime() + ".docx";
        File documentFile = new File(getJavaTempDirectory() + finalFileName);
        template.save(documentFile);
        return documentFile;
    }

    public File savePdfToTemp(WordprocessingMLPackage template, String fileName)
            throws Docx4JException, FileNotFoundException {
        String finalFileName = cleanFileName(fileName) + getFileCreationDateAndTime() + ".pdf";
        File documentFile = new File(getJavaTempDirectory() + finalFileName);
        Docx4J.toPDF(template, new FileOutputStream(documentFile));
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
