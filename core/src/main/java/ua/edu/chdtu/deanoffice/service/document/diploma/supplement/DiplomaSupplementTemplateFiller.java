package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;
import static ua.edu.chdtu.deanoffice.service.document.Templates.*;

public class DiplomaSupplementTemplateFiller {

    public static File fillWithStudentInformation(String templateFilepath, StudentSummary studentSummary) {
        WordprocessingMLPackage template = getTemplate(templateFilepath);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(studentSummary.getStudentInfoDictionary());
        commonDict.putAll(studentSummary.getTotalDictionary());
        replacePlaceholders(template, commonDict);
        return saveTemplate(template, studentSummary.getStudent().getInitialsUkr() + ".docx");
    }

    private static void fillTableWithGrades(WordprocessingMLPackage template,
                                            List<List<Map<String, String>>> tableDataDictionary)
            throws Docx4JException, JAXBException {
        Set<String> placeholdersToRemove = new HashSet<>();

        List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
        Tbl tempTable = getTemplateTable(tables, "#GradesTable");
        placeholdersToRemove.add("#GradesTable");
        List<Object> rows = getAllElementFromObject(tempTable, Tr.class);

        //Doing reverse to avoid saving file after filling each section
        Collections.reverse(tableDataDictionary);

        Tr templateRow = (Tr) rows.get(7);
        int sectionNumber = 3;
        int rowToAddIndex;

        for (List<Map<String, String>> tableSectionDataDictionary : tableDataDictionary) {
            if (sectionNumber > 0) {
                String sectionPlaceholderKey = "#Section" + sectionNumber;
                placeholdersToRemove.add(sectionPlaceholderKey);
                rowToAddIndex = rows.indexOf(findRowInTable(tempTable, sectionPlaceholderKey)) + 1;
            } else {
                rowToAddIndex = 2;
            }
            for (Map<String, String> replacements : tableSectionDataDictionary) {
                addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
                rowToAddIndex++;
            }
            sectionNumber--;
        }
        tempTable.getContent().remove(templateRow);
        replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }
}
