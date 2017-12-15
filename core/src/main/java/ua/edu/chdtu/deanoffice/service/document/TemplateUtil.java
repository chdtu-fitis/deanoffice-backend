package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.JaxbXmlPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
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

    public static List<Object> getAllElementsFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementsFromObject(child, toSearch));
            }
        }
        return result;
    }

    public static void replaceTextPlaceholdersInTemplate(WordprocessingMLPackage template, Map<String, String> placeholdersValues) {
        List<Text> placeholders = getTextsContainingPlaceholders(template);
        replaceValuesInTextPlaceholders(placeholders, placeholdersValues);
    }

    private static void replaceValuesInTextPlaceholders(List<Text> placeholders, Map<String, String> placeholdersValues) {
        for (Text text : placeholders) {
            try {
                String value = placeholdersValues.get(text.getValue().trim());
                text.setValue(value);
            } catch (NullPointerException e) {
                log.debug(text.getValue() + " is null");
            }
        }
    }

    private static List<Text> getTextsContainingPlaceholders(WordprocessingMLPackage template) {
        List<Object> texts = getAllElementsFromObject(template.getMainDocumentPart(), Text.class);
        List<Object> placeholders = texts.stream().filter(o ->
                isAPlaceholder((Text) o)).collect(Collectors.toList());
        List<Text> result = new ArrayList<>();
        placeholders.forEach(p -> result.add((Text) (p)));
        return result;
    }

    public static void replacePlaceholdersWithBlank(WordprocessingMLPackage template, Set<String> placeholders) {
        List<Text> texts = getTextsContainingPlaceholders(template);
        for (Text text : texts) {
            if (placeholders.contains(text.getValue())) {
                text.setValue("");
            }
        }
    }

    private static void replacePlaceholdersInRelativeElement(WordprocessingMLPackage template, String relationType, Map<String, String> dictionary) {
        RelationshipsPart relationshipPart = template.getMainDocumentPart().getRelationshipsPart();
        List<Relationship> relationships = relationshipPart.getRelationshipsByType(relationType);
        List<Text> texts = new ArrayList<>();
        for (Relationship r : relationships) {
            JaxbXmlPart part = (JaxbXmlPart) relationshipPart.getPart(r);
            List<Object> textObjects = null;
            try {
                textObjects = getAllElementsFromObject(part.getContents(), Text.class);
            } catch (Docx4JException e) {
                log.debug("Could not extract contents from part", e);
            }
            for (Object textObject : textObjects) {
                Text text = (Text) textObject;
                if (isAPlaceholder(text))
                    texts.add(text);
            }

        }
        replaceValuesInTextPlaceholders(texts, dictionary);
    }

    public static void replacePlaceholdersInFooter(WordprocessingMLPackage template, Map<String, String> dictionary) {
        replacePlaceholdersInRelativeElement(template,
                Namespaces.FOOTER,
                dictionary);
    }

    private static boolean isAPlaceholder(Text text) {
        return text.getValue() != null && text.getValue().startsWith(PLACEHOLDER_PREFIX);
    }

    public static Tr findRowInTable(Tbl table, String templateKey) {
        for (Object row : table.getContent()) {
            List<?> textElements = getAllElementsFromObject(row, Text.class);
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
            List<Object> textElements = getAllElementsFromObject(tbl, Text.class);
            for (Object text : textElements) {
                Text textElement = (Text) text;
                if (textElement.getValue() != null && textElement.getValue().trim().equals(templateKey))
                    return (Tbl) tbl;
            }
        }
        return null;
    }

    public static void addRowToTable(Tbl reviewTable, Tr templateRow, int rowNumber, Map<String, String> replacements) {
        Tr workingRow = XmlUtils.deepCopy(templateRow);
        List<?> textElements = getAllElementsFromObject(workingRow, Text.class);
        for (Object object : textElements) {
            Text text = (Text) object;
            String replacementValue = replacements.get(text.getValue().trim());
            if (replacementValue != null)
                text.setValue(replacementValue);
        }
        reviewTable.getContent().add(rowNumber, workingRow);
    }
}
