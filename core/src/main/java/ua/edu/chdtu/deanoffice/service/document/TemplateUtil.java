package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.JaxbXmlPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Br;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.jvnet.jaxb2_commons.ppp.Child;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(TemplateUtil.class);
    private static final String PLACEHOLDER_PREFIX = "#";

    private static ObjectFactory factory = new ObjectFactory();

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

    public static List<Tbl> getAllTablesFromDocument(WordprocessingMLPackage document) {
        ContentAccessor accessor = document.getMainDocumentPart();
        return getAllElementsFromObject(accessor, Tbl.class).stream().map(o -> (Tbl) o).collect(Collectors.toList());
    }

    public static List<Tr> getAllRowsFromTable(Tbl table) {
        return table.getContent().stream().map(o -> (Tr) o).collect(Collectors.toList());
    }

    public static Tbl findTable(WordprocessingMLPackage document, String key) {
        return findTable(getAllTablesFromDocument(document), key);
    }

    public static Tbl findTable(List<Tbl> tables, String templateKey) {
        for (Tbl tbl : tables) {
            List<Text> textElements = getAllTextsFromObject(tbl);
            for (Text text : textElements) {
                if (text.getValue() != null && text.getValue().contains(templateKey)) {
                    return tbl;
                }
            }
        }
        log.warn("Could not find table with key {}", templateKey);
        return null;
    }

    public static Object findParentNode(Child child, Class<?> nodeClass) {
        Object result = child.getParent();
        while (!result.getClass().equals(nodeClass)) {
            if (result instanceof Child) {
                result = ((Child) result).getParent();
            } else {
                break;
            }
        }
        return result;
    }

    public static void replaceTextPlaceholdersInTemplate(WordprocessingMLPackage template,
                                                         Map<String, String> placeholdersValues,
                                                         Boolean replaceEmptyWithBlank) {
        List<Text> placeholders = getTextsContainingPlaceholders(template);
        replaceValuesInTextPlaceholders(placeholders, placeholdersValues, replaceEmptyWithBlank);
    }

    public static void replaceTextPlaceholdersInTemplate(WordprocessingMLPackage template,
                                                         Map<String, String> placeholdersValues) {
        List<Text> placeholders = getTextsContainingPlaceholders(template);
        replaceValuesInTextPlaceholders(placeholders, placeholdersValues);
    }

    public static void replaceValuesInTextPlaceholders(List<Text> placeholders,
                                                       Map<String, String> replacements,
                                                       Boolean replaceEmpty) {
        for (Text text : placeholders) {
            String replacement = replacements.get(text.getValue().trim().replaceFirst(PLACEHOLDER_PREFIX, ""));
            if (StringUtils.isEmpty(replacement)) {
                log.debug("{} is empty", text.getValue());
            }
            if (replaceEmpty || !StringUtils.isEmpty(replacement)) {
                text.setValue(getValueSafely(replacement));
            }
        }
    }

    public static void replaceValuesInTextPlaceholders(List<Text> placeholders,
                                                       Map<String, String> replacements) {
        replaceValuesInTextPlaceholders(placeholders, replacements, true);
    }

    private static List<Text> getTextsContainingPlaceholders(WordprocessingMLPackage template) {
        return getTextsPlaceholdersFromContentAccessor(template.getMainDocumentPart());
    }

    public static List<Text> getTextsPlaceholdersFromContentAccessor(ContentAccessor contentAccessor) {
        List<Text> texts = getAllTextsFromObject(contentAccessor);
        List<Object> placeholders = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            Text text = texts.get(i);
            if (isAPlaceholder(text)) {
                placeholders.add(text);
                continue;
            }
            if (text.getValue().trim().equals(PLACEHOLDER_PREFIX)) {
                Iterator<Text> iterator = texts.listIterator(i + 1);
                if (iterator.hasNext()) {
                    Text potentialPlaceholder = iterator.next();
                    potentialPlaceholder.setValue(text.getValue() + potentialPlaceholder.getValue());
                    text.setValue("");
                    placeholders.add(potentialPlaceholder);
                    i++;
                }
            }
        }
        return placeholders.stream().map(o -> (Text) o).collect(Collectors.toList());
    }

    public static List<Text> getAllTextsFromObject(Object object) {
        return getAllElementsFromObject(object, Text.class).stream().map(o -> (Text) o).collect(Collectors.toList());
    }

    public static void replacePlaceholdersWithBlank(WordprocessingMLPackage template, Set<String> placeholders) {
        List<Text> texts = getTextsContainingPlaceholders(template);
        for (Text text : texts) {
            if (placeholders.contains(text.getValue())) {
                text.setValue("");
            }
        }
    }

    public static void replacePlaceholdersWithBlank(WordprocessingMLPackage template) {
        List<Text> texts = getTextsContainingPlaceholders(template);
        for (Text text : texts) {
            text.setValue("");
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

    public static void addRowToTable(Tbl reviewTable, Tr templateRow, int rowNumber, Map<String, String> replacements) {
        Tr workingRow = XmlUtils.deepCopy(templateRow);
        replaceInRow(workingRow, replacements);
        reviewTable.getContent().add(rowNumber, workingRow);
    }

    public static void replaceInRow(Tr tableRow, Map<String, String> replacements) {
        List<Text> textElements = getTextsPlaceholdersFromContentAccessor(tableRow);
        replaceValuesInTextPlaceholders(textElements, replacements);
    }

    public static void replaceInCell(Tc tableCell, Map<String, String> replacements) {
        List<Text> textElements = getTextsPlaceholdersFromContentAccessor(tableCell);
        replaceValuesInTextPlaceholders(textElements, replacements);
    }

    public static void replaceInCell(Tr row, int cellIndex, Map<String, String> replacements) {
        fixRow(row);
        List<Object> cells = row.getContent();
        Tc tableCell = ((JAXBElement<Tc>) (cells.get(cellIndex))).getValue();
        replaceInCell(tableCell, replacements);
    }

    private static void fixRow(Tr row) {
        List<Object> cells = row.getContent();
        JAXBElement jaxbElement = (JAXBElement) (cells.get(cells.size() - 1));
        if (!(jaxbElement.getValue() instanceof Tc)) {
            cells.remove(cells.size() - 1);
            fixRow(row);
        }
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

    public static void addPageBreak(WordprocessingMLPackage document) {
        Br breakObject = createPageBreak();
        P paragraph = createParagraph();
        paragraph.getContent().add(breakObject);

        document.getMainDocumentPart().addObject(paragraph);
    }

    public static Br createPageBreak() {
        Br breakObject = new Br();
        breakObject.setType(STBrType.PAGE);
        return breakObject;
    }

    public static Br createLineBreak() {
        Br breakObject = new Br();
        breakObject.setType(STBrType.TEXT_WRAPPING);
        return breakObject;
    }

    public static P createParagraph() {
        return factory.createP();
    }

    public static Text createText(String value) {
        Text text = factory.createText();
        text.setValue(value);
        return text;
    }

    public static R createR(){
        return factory.createR();
    }

    public static RPr createRPr(){
        return factory.createRPr();
    }

    public static String getValueSafely(String value, String ifNullOrEmpty) {
        return StringUtils.isEmpty(value) ? ifNullOrEmpty : value;
    }

    public static String getValueSafely(String value) {
        return getValueSafely(value, "");
    }
}
