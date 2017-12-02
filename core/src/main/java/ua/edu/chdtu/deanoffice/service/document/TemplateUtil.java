package ua.edu.chdtu.deanoffice.service.document;

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

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TemplateUtil {

    private static Logger log = LoggerFactory.getLogger(TemplateUtil.class);

    public static final String PLACEHOLDER_PREFIX = "#";

    public static WordprocessingMLPackage loadTemplate(String name) {
        try {
            return WordprocessingMLPackage.load(new FileInputStream(new ClassPathResource(name).getFile()));
        } catch (Docx4JException e) {
            log.error("Supplied file is not a valid template!", e);
        } catch (IOException e) {
            log.error("Could not find or load file!", e);
        }
        return null;
    }

    public static File saveDocument(WordprocessingMLPackage template, String target) {
        File f = new File(target);
        try {
            template.save(f);
        } catch (Docx4JException e) {
            log.error("Could not save template!", e);
        }
        return f;
    }

    public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
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

    public static void replacePlaceholders(WordprocessingMLPackage template, Map<String, String> placeholdersValues) {
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

    public static void replacePlaceholdersWithBlank(WordprocessingMLPackage template, Set<String> placeholders) {
        List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);
        for (Object text : texts) {
            Text textElement = (Text) text;
            if (textElement.getValue().startsWith(PLACEHOLDER_PREFIX)
                    && placeholders.contains(textElement.getValue())) {
                textElement.setValue("");
            }
        }
    }

    public static Tr findRowInTable(Tbl table, String templateKey) {
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

    public static Tbl findTable(List<Object> tables, String templateKey) {
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

    public static void addRowToTable(Tbl reviewTable, Tr templateRow, int rowNumber, Map<String, String> replacements) {
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
