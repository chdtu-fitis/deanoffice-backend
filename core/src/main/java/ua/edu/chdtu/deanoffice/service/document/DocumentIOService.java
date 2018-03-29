package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class DocumentIOService {

    public WordprocessingMLPackage loadTemplate(String name) throws IOException, Docx4JException {
        return WordprocessingMLPackage.load(new FileInputStream(new ClassPathResource(name).getFile()));
    }

    public SpreadsheetMLPackage loadSpreadsheetDocument(String name) throws IOException, Docx4JException {
        return SpreadsheetMLPackage.load(new FileInputStream(new ClassPathResource(name).getFile()));
    }

    public SpreadsheetMLPackage loadSpreadsheetDocument(InputStream xlsxInputStream) throws Docx4JException {
        return SpreadsheetMLPackage.load(xlsxInputStream);
    }

    public File saveDocumentToTemp(WordprocessingMLPackage document, String fileName) throws Docx4JException {
        return saveDocument(document, fileName);
    }

    public File saveDocumentToTemp(SpreadsheetMLPackage document, String fileName) throws Docx4JException {
        return saveDocument(document, fileName);
    }

    private File saveDocument(Object document, String fileName) throws Docx4JException {
        String finalFileName = cleanFileName(fileName);
        File documentFile = new File(System.getProperty("java.io.tmpdir") + finalFileName);

        (document instanceof WordprocessingMLPackage
                ? ((WordprocessingMLPackage) document)
                : ((SpreadsheetMLPackage) document)
        ).save(documentFile);

        return documentFile;
    }

    public String cleanFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        return name.replaceAll("[\\W]*", "") + extension;
    }

}
