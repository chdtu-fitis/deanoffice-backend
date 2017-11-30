package ua.edu.chdtu.deanoffice.api.diplomasupplement;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TemplateFillFactory {

    private static Logger log = LoggerFactory.getLogger(TemplateFillFactory.class);

    private static final String PLACEHOLDER_PREFIX = "#";

    private static WordprocessingMLPackage getTemplate(String name) {
        try {
            return WordprocessingMLPackage.load(new FileInputStream(new ClassPathResource(name).getFile()));
        } catch (Docx4JException e) {
            log.error("Supplied file is not a valid template!", e);
        } catch (IOException e) {
            log.error("Could not find or load file!", e);
        }
        return null;
    }

    private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }

        }
        return result;
    }

    private static void replacePlaceholders(WordprocessingMLPackage template, Map<String, String> placeholdersValues) {
        List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);
        List<Object> placeholders = new ArrayList<>();
        placeholders.addAll(texts.stream().filter(o -> ((Text) o).getValue().startsWith(PLACEHOLDER_PREFIX)).collect(Collectors.toList()));
        String value;
        for (Object text : placeholders) {
            Text textElement = (Text) text;
            try {
                value = placeholdersValues.get(textElement.getValue().trim());
                if (value != null) {
                    textElement.setValue(value);
                } else log.warn(textElement.getValue() + " is null");
            } catch (NullPointerException e) {
                log.warn(textElement.getValue() + " is null");
            }
        }
    }

    private static void replacePlaceholdersWithBlank(WordprocessingMLPackage template, Set<String> placeholders) {
        List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);
        for (Object text : texts) {
            Text textElement = (Text) text;
            if (textElement.getValue().startsWith(PLACEHOLDER_PREFIX)
                    && placeholders.contains(textElement.getValue())) {
                textElement.setValue("");
            }
        }
    }

    private static File saveTemplate(WordprocessingMLPackage template, String target) {
        File f = new File(target);
        try {
            template.save(f);
        } catch (Docx4JException e) {
            log.error("Could not save template!", e);
        }
        return f;
    }

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


    private static Tr findRowInTable(Tbl table, String templateKey)
            throws Docx4JException, JAXBException {
        for (Object row : table.getContent()) {
            List<?> textElements = getAllElementFromObject(row, Text.class);
            for (Object text : textElements) {
                Text textElement = (Text) text;
                if (textElement.getValue() != null && textElement.getValue().trim().equals(templateKey))
                    return (Tr) row;
            }
        }
        return null;
    }

    private static Tbl getTemplateTable(List<Object> tables, String templateKey)
            throws Docx4JException, JAXBException {
        for (Object tbl : tables) {
            List<?> textElements = getAllElementFromObject(tbl, Text.class);
            for (Object text : textElements) {
                Text textElement = (Text) text;
                if (textElement.getValue() != null && textElement.getValue().trim().equals(templateKey))
                    return (Tbl) tbl;
            }
        }
        return null;
    }

    private static void addRowToTable(Tbl reviewTable, Tr templateRow, int rowNumber, Map<String, String> replacements) {
        Tr workingRow = (Tr) XmlUtils.deepCopy(templateRow);
        List<?> textElements = getAllElementFromObject(workingRow, Text.class);
        for (Object object : textElements) {
            Text text = (Text) object;
            String replacementValue = replacements.get(text.getValue().trim());
            if (replacementValue != null)
                text.setValue(replacementValue);
        }
        reviewTable.getContent().add(rowNumber, workingRow);
    }
}
