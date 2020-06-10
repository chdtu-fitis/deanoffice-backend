package ua.edu.chdtu.deanoffice.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderApproverTemplateRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderControlTemplateRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderTemplateVersionRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderTemplateVersionRepository orderTemplateVersionRepository;
    private final OrderRepository orderRepository;
    private final FacultyService facultyService;
    private final OrderControlTemplateRepository orderControlTemplateRepository;
    private final StudentDegreeService studentDegreeService;
    private final CurrentYearService currentYearService;
    private final OrderReasonService orderReasonService;
    private final ObjectMapper objectMapper;
    private final StudentExpelRepository studentExpelRepository;
    private final OrderApproverTemplateRepository orderApproverTemplateRepository;
    private final StudentExpelService studentExpelService;
    private final DocumentIOService documentIOService;

    public Integer createOrder(OrderCreateCommand orderCreateCommand) {
        return orderRepository.save(new Order()
                .setOrderDate(orderCreateCommand.getOrderDate())
                .setOrderNumber(orderCreateCommand.getOrderNumber())
                .setOrderTemplateVersion(orderTemplateVersionRepository
                        .findByDbTableNameAndActive(orderCreateCommand.getOrderType().toString(), true))
                .setOrderControlTemplate(orderControlTemplateRepository.
                        findByFacultyIdAndActive(orderCreateCommand.getFacultyId(), true))
                .setFaculty(facultyService.getById(orderCreateCommand.getFacultyId())))
                .getId();
    }

    public File generateStudentExpelDocument(Integer studentExpelId) throws IOException, Docx4JException {
        StudentExpel studentExpel = studentExpelService.getById(studentExpelId);
        StudentExpelBusinessInformation studentExpelBusinessInformation = objectMapper.readValue(studentExpel.getOrderBusinessOperation(), StudentExpelBusinessInformation.class);
        Order order = this.getOrderById(studentExpelBusinessInformation.getOrderId());
        String orderName = order.getOrderTemplateVersion().getDbTableName() + " " + order.getOrderNumber();
        WordprocessingMLPackage orderDocument = documentIOService.loadTemplate(DocumentIOService.ORDERS_PATH +
                order.getOrderTemplateVersion().getTemplateName());
        Map<String, String> placeholderValue = new HashMap<>();
        placeholderValue.put("PHParagraph", objectMapper
                .readValue(studentExpel.getOrderParagraphJson(),
                        OrderParsedParagraphDto.class).getParagraphFields().stream()
                .map(OrderParagraphPiece::getValue).collect(Collectors.joining()));
        placeholderValue.put("PHOrderDate", order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        placeholderValue.put("PHNumber", order.getOrderNumber());
        TemplateUtil.replaceTextPlaceholdersInTemplate(orderDocument, placeholderValue);
        return documentIOService.saveDocumentToTemp(orderDocument, orderName, FileFormatEnum.DOCX);
    }

    public Integer saveStudentExpel(@RequestBody StudentExpelCreateCommand studentExpelCreateCommand) throws JsonProcessingException {

        Map<Integer, OrderReason> orderReasonMap = new HashMap<>();
        orderReasonMap.put(1, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_BACHELOR));
        orderReasonMap.put(2, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_SPECIALIST));
        orderReasonMap.put(3, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_MASTER));

        Order order = this.getOrderById(studentExpelCreateCommand.getOrderId());
        order.setOrderControlTemplate(orderControlTemplateRepository.findOne(studentExpelCreateCommand.getOrderControlTemplateId()))
                .setOrderApproveTemplate(orderApproverTemplateRepository.findOne(studentExpelCreateCommand.getOrderApproverTemplateId()));
        orderRepository.save(order);
        StudentDegree studentDegree = studentDegreeService.getById(studentExpelCreateCommand.getStudentDegreeId());
        return studentExpelRepository.save(new StudentExpel().setStudentDegree(studentDegree)
                .setStudentGroup(studentDegree.getStudentGroup())
                .setStudyYear(currentYearService.getYear() - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears())
                .setPayment(studentExpelCreateCommand.getPayment())
                .setExpelDate(studentExpelCreateCommand.getExpelDate())
                .setOrderNumber(order.getOrderNumber())
                .setOrderDate(order.getOrderDate())
                .setOrderReason(orderReasonMap.get(studentDegree.getSpecialization().getDegree().getId()))
                .setApplicationDate(studentExpelCreateCommand.getApplicationDate())
                .setOrderParagraphJson(objectMapper.writeValueAsString(studentExpelCreateCommand.getOrderParsedParagraphDto()))
                .setOrderBusinessOperation(objectMapper.writeValueAsString(new StudentExpelBusinessInformation(studentExpelCreateCommand.getStudentDegreeId(),
                        studentExpelCreateCommand.getOrderId()))))
                .getId();
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findOne(orderId);
    }

    public List<OrderParagraphPiece> getParsedParagraph(OrderType orderType) {
        String template = orderTemplateVersionRepository.findByDbTableNameAndActive(orderType.toString(), true).getParagraphTemplate();
        List<OrderParagraphPiece> orderParagraphPieces = new ArrayList<>();
        while (template.length() != 0) {
            int lastWordIndex = 0;
            List<Character> charSequence = new ArrayList<>();
            if (template.charAt(0) != '#' & template.charAt(0) != '$') {
                for (int i = 0; i < template.length(); i++) {
                    if (template.charAt(i) != '#' && template.charAt(i) != '$') {
                        charSequence.add(template.charAt(i));
                        continue;
                    }
                    lastWordIndex = i - 1;
                    break;
                }
                orderParagraphPieces.add(new OrderParagraphPiece(charSequence.stream().map(String::valueOf).collect(Collectors.joining()), false));
                template = removeParsedSymbols(template, lastWordIndex);
                continue;
            }

            Boolean editable = template.charAt(0) == '$';

            for (int i = 0; i < template.length(); i++) {
                if (template.charAt(i) != ' ' && template.charAt(i) != ',' && template.charAt(i) != '.' && template.charAt(i) != ';' && template.charAt(i) != '«' && template.charAt(i) != '»') {
                    if (template.charAt(i) != '$') {
                        charSequence.add(template.charAt(i));
                    }
                    continue;
                }
                lastWordIndex = i - 1;
                break;
            }
            orderParagraphPieces.add(new OrderParagraphPiece(charSequence.stream().map(String::valueOf).collect(Collectors.joining()), editable));
            template = removeParsedSymbols(template, lastWordIndex);
        }
        return orderParagraphPieces;
    }

    private String removeParsedSymbols(String string, int lastSymbolIndex) {
        List<Character> resultingString = new ArrayList<>();
        for (int i = lastSymbolIndex + 1; i < string.length(); i++) {
            resultingString.add(string.charAt(i));
        }
        return resultingString.stream().map(String::valueOf).collect(Collectors.joining());
    }
}
