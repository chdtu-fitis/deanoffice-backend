package ua.edu.chdtu.deanoffice.api.order;

import lombok.RequiredArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.order.command.CreateOrderCommand;
import ua.edu.chdtu.deanoffice.api.order.command.ParagraphCreateCommand;
import ua.edu.chdtu.deanoffice.api.order.command.StudentExpelParagraphCommand;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.entity.order.PlaceholderValue;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.order.OrderService;
import ua.edu.chdtu.deanoffice.util.DocumentUtil;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController extends DocumentResponseController {

    private final OrderService orderService;
    private final DocumentIOService documentIOService;

    @PutMapping
    public ResponseEntity<Resource> generateDocument(CreateOrderCommand createOrderCommand)
            throws Docx4JException, FileNotFoundException {
        WordprocessingMLPackage orderDocument = documentIOService.loadTemplate(DocumentIOService.ORDERS_PATH +
                createOrderCommand.getOrderTemplateName());
        Map<String, String> placeholderValue = new HashMap<>();
        placeholderValue.put("PHParagraph", createOrderCommand.getParagraph());
        placeholderValue.put("PHOrderDate",createOrderCommand.getOrderDate().toString());
        placeholderValue.put("PHNumber", createOrderCommand.getOrderNumber());
        TemplateUtil.replaceTextPlaceholdersInTemplate(orderDocument, placeholderValue);
        return buildDocumentResponseEntity(documentIOService.saveDocumentToTemp(orderDocument, createOrderCommand.getFileName(), FileFormatEnum.DOCX),
                createOrderCommand.getFileName(), MEDIA_TYPE_DOCX);
    }

    @GetMapping("/paragraph")
    public String getStudentExpelValidParagraph(StudentExpelParagraphCommand studentExpelParagraphCommand, OrderType orderType) {
        String paragraph = orderService.getParagraphByOrderType(orderType);
        for (PlaceholderValue placeholderValue : getCommandPlaceholderValueMap(studentExpelParagraphCommand)) {
            String placeholder = placeholderValue.getPlaceholderName();
            if (paragraph.contains(placeholder)) {
                String documentInfoField = placeholderValue.getValue();
                if (documentInfoField != null) {
                    paragraph = paragraph.replace(placeholder, documentInfoField);
                } else {
                    paragraph = paragraph.replace(placeholder, "");
                }
            }
        }
        return paragraph;
    }

    public static Set<PlaceholderValue> getCommandPlaceholderValueMap(ParagraphCreateCommand paragraphCreateCommand) {
        Set<PlaceholderValue> documentPlaceholders = new HashSet<>();
        Arrays.stream(paragraphCreateCommand.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList())
                .forEach(commandField -> {
                    try {
                        Field field = paragraphCreateCommand.getClass().getDeclaredField(commandField);
                        field.setAccessible(true);
                        if (field.get(paragraphCreateCommand) != null) {
                            documentPlaceholders.add(new PlaceholderValue(DocumentUtil.PLACEHOLDER
                                    + field.getName().substring(0, 1).toUpperCase()
                                    + field.getName().substring(1),
                                    field.get(paragraphCreateCommand).toString()));
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                });
        return documentPlaceholders;
    }
}
