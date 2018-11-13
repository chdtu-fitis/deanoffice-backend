package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ThesisImportService {

    private DocumentIOService documentIOService;

    @Autowired
    public ThesisImportService(DocumentIOService documentIOService){
        this.documentIOService = documentIOService;
    }

    public ThesisReport getThesisImportReport(InputStream docxInputStream) throws Exception{
        if (docxInputStream == null){
            throw new Exception("Помилка часу виконання");
        }
        List<ThesisImportData> thesisImportData = getThesisesFromStream(docxInputStream);
        //ThesisImportService thesisImportService = null;
        //thesisImportService = getThesisImportService(docxInputStream);
        return null;
    }

    private List<ThesisImportData> getThesisesFromStream(InputStream docxInputStream) throws Docx4JException, IOException {
        return getThesisImportInfo(docxInputStream);
    }

    private List<ThesisImportData> getThesisImportInfo(Object source) throws Docx4JException, IOException {
        WordprocessingMLPackage docxPkg;
        if (source instanceof String){
            docxPkg = documentIOService.loadTemplateWordDocument((String) source);
        } else
            docxPkg = documentIOService.loadTemplateWordDocument((InputStream) source);
        return getThesisImportedDataFromDocxPkg(docxPkg);
    }

    private List<ThesisImportData> getThesisImportedDataFromDocxPkg(WordprocessingMLPackage docxPkg) {

        List<ThesisImportData> thesisImportData = new ArrayList();
        return thesisImportData;
    }

}


//    MainDocumentPart mainDocumentPart = docxPkg.getMainDocumentPart();
//        String textNotesPath = "//w:t";
//        List <Object> textNotes = mainDocumentPart.getJAXBNodesViaXPath(textNotesPath, true);
//        for (Object obj : textNotes) {
//            Text text = (Text) ((JAXBElement) obj).getValue();
//            String textValue = text.getValue();
//            System.out.println(textValue);
//        }