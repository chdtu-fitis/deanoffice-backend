package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.JaxbXmlPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(TemplateUtil.class);
    private static final String PLACEHOLDER_PREFIX = "#";

    public static List<Object> getAllElementsFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement<?>) obj).getValue();
        }

        if (obj.getClass().equals(toSearch)) {
            result.add(obj);
        } else {
            if (obj instanceof ContentAccessor) {
                List<?> children = ((ContentAccessor) obj).getContent();
                for (Object child : children) {
                    result.addAll(getAllElementsFromObject(child, toSearch));
                }
            }
        }
        return result;
    }

    public static void replaceTextPlaceholdersInTemplate(WordprocessingMLPackage template, Map<String, String> placeholdersValues) {
        List<Text> placeholders = getTextsContainingPlaceholders(template);
        replaceValuesInTextPlaceholders(placeholders, placeholdersValues);
    }

    private static void replaceValuesInTextPlaceholders(List<Text> placeholders, Map<String, String> replacements) {
        for (Text text : placeholders) {
            String replacement = replacements.get(text.getValue().trim().replaceFirst(PLACEHOLDER_PREFIX, ""));
            if (StringUtils.isEmpty(replacement)) {
                log.debug("{} is empty", text.getValue());
            }
            text.setValue(getValueSafely(replacement));
        }
    }

    private static List<Text> getTextsContainingPlaceholders(WordprocessingMLPackage template) {
        return getTextsFromContentAccessor(template.getMainDocumentPart());
    }

    private static List<Text> getTextsFromContentAccessor(ContentAccessor contentAccessor) {
        List<Object> texts = getAllElementsFromObject(contentAccessor, Text.class);
        List<Object> placeholders = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            Text text = ((Text) texts.get(i));
            if (isAPlaceholder(text)) {
                placeholders.add(text);
                continue;
            }
            if (text.getValue().trim().equals(PLACEHOLDER_PREFIX)) {
                Iterator<Object> iterator = texts.listIterator(i + 1);
                if (iterator.hasNext()) {
                    Text potentialPlaceholder = (Text) iterator.next();
                    potentialPlaceholder.setValue(text.getValue() + potentialPlaceholder.getValue());
                    text.setValue("");
                    placeholders.add(potentialPlaceholder);
                    i++;
                }
            }
        }
        return placeholders.stream().map(o -> (Text) o).collect(Collectors.toList());
    }

    public static void replacePlaceholdersWithBlank(WordprocessingMLPackage template, Set<String> placeholders) {
        List<Text> texts = getTextsContainingPlaceholders(template);
        for (Text text : texts) {
            if (placeholders.contains(text.getValue())) {
                text.setValue("");
            }
        }
    }

    private static void replacePlaceholdersInRelativeElement(WordprocessingMLPackage template,
                                                             String relationType,
                                                             Map<String, String> dictionary) throws Docx4JException {
        RelationshipsPart relationshipPart = template.getMainDocumentPart().getRelationshipsPart();
        List<Relationship> relationships = relationshipPart.getRelationshipsByType(relationType);
        List<Text> texts = new ArrayList<>();
        for (Relationship r : relationships) {
            JaxbXmlPart part = (JaxbXmlPart) relationshipPart.getPart(r);
            List<Object> textObjects = getAllElementsFromObject(part.getContents(), Text.class);
            for (Object textObject : textObjects) {
                Text text = (Text) textObject;
                if (isAPlaceholder(text)) {
                    texts.add(text);
                }
            }
        }
        replaceValuesInTextPlaceholders(texts, dictionary);
    }

    public static void replacePlaceholdersInFooter(WordprocessingMLPackage template, Map<String, String> dictionary)
            throws Docx4JException {
        replacePlaceholdersInRelativeElement(template, Namespaces.FOOTER, dictionary);
    }

    private static boolean isAPlaceholder(Text text) {
        return text.getValue() != null && text.getValue().startsWith(PLACEHOLDER_PREFIX) && text.getValue().length() > 1;
    }

    public static Tbl findTable(List<Object> tables, String templateKey) {
        for (Object tbl : tables) {
            List<Object> textElements = getAllElementsFromObject(tbl, Text.class);
            for (Object text : textElements) {
                Text textElement = (Text) text;
                if (textElement.getValue() != null && textElement.getValue().trim().equals(templateKey)) {
                    return (Tbl) tbl;
                }
            }
        }
        return null;
    }

    public static void addRowToTable(Tbl reviewTable, Tr templateRow, int rowNumber, Map<String, String> replacements) {
        Tr workingRow = XmlUtils.deepCopy(templateRow);
        replaceInRow(workingRow, replacements);
        reviewTable.getContent().add(rowNumber, workingRow);
    }

    public static void replaceInRow(Tr tableRow, Map<String, String> replacements) {
        List<Text> textElements = getTextsFromContentAccessor(tableRow);
        replaceValuesInTextPlaceholders(textElements, replacements);
    }

    public static void replaceInCell(Tc tableCell, Map<String, String> replacements) {
        List<Text> textElements = getTextsFromContentAccessor(tableCell);
        replaceValuesInTextPlaceholders(textElements, replacements);
    }

    public static void replaceInCell(Tr row, int cellIndex, Map<String, String> replacements) {
        fixRow(row);
        List<Object> cells = row.getContent();
        Tc tableCell = ((JAXBElement<Tc>) (cells.get(cellIndex))).getValue();
        replaceInCell(tableCell, replacements);
    }

    public static void cloneLastCellInRow(Tr templateRow) {
        fixRow(templateRow);
        List<Object> cells = templateRow.getContent();
        JAXBElement<Tc> jaxbElement = (JAXBElement<Tc>) (cells.get(cells.size() - 1));
        Tc lastCell;
        lastCell = jaxbElement.getValue();
        Tc result = XmlUtils.deepCopy(lastCell);
        templateRow.getContent().add(new JAXBElement<Tc>(jaxbElement.getName(), Tc.class, result));
    }

    public static void copyTable(WordprocessingMLPackage template, int tableIndex) {
        Tbl table = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(tableIndex);
        template.getMainDocumentPart().addObject(XmlUtils.deepCopy(table));
    }

    private static void fixRow(Tr row) {
        List<Object> cells = row.getContent();
        JAXBElement jaxbElement = (JAXBElement) (cells.get(cells.size() - 1));
        if (!(jaxbElement.getValue() instanceof Tc)) {
            cells.remove(cells.size() - 1);
            fixRow(row);
        }
    }

    public static String getValueSafely(String value, String ifNullOrEmpty) {
        return StringUtils.isEmpty(value) ? ifNullOrEmpty : value;
    }

    public static String getValueSafely(String value) {
        return getValueSafely(value, "");
    }
}
