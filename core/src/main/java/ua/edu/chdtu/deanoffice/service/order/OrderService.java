package ua.edu.chdtu.deanoffice.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.OrderType;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderApproverTemplateRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderTemplateVersionRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.order.beans.OrderCreateCommand;
import ua.edu.chdtu.deanoffice.service.order.beans.OrderParagraphPiece;
import ua.edu.chdtu.deanoffice.service.order.beans.OrderParsedParagraphDto;
import ua.edu.chdtu.deanoffice.service.order.beans.OrderTypeBean;
import ua.edu.chdtu.deanoffice.service.order.beans.StudentExpelBusinessInformation;
import ua.edu.chdtu.deanoffice.service.order.beans.StudentExpelCreateCommand;
import ua.edu.chdtu.deanoffice.service.order.beans.StudentExpelResponseDto;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderTemplateVersionRepository orderTemplateVersionRepository;
    private final OrderRepository orderRepository;
    private final FacultyService facultyService;
    private final StudentDegreeService studentDegreeService;
    private final CurrentYearService currentYearService;
    private final OrderReasonService orderReasonService;
    private final ObjectMapper objectMapper;
    private final StudentExpelRepository studentExpelRepository;
    private final OrderApproverTemplateRepository orderApproverTemplateRepository;
    private final StudentExpelService studentExpelService;
    private final DocumentIOService documentIOService;

    public Set<OrderTypeBean> getOrderTypes() {
        Set<OrderTypeBean> orderTypes = new HashSet<>();
        for (OrderType orderType : OrderType.values()) {
            orderTypes.add(new OrderTypeBean(orderType.name(), orderType.getNameUkr()));
        }
        return orderTypes;
    }

    public Integer createOrder(OrderCreateCommand orderCreateCommand) {
        return orderRepository.save(new Order()
                .setOrderDate(orderCreateCommand.getOrderDate())
                .setOrderNumber(orderCreateCommand.getOrderNumber())
                .setOrderTemplateVersion(orderTemplateVersionRepository
                        .findByDbTableNameAndActive(orderCreateCommand.getOrderType(), true))
                .setFaculty(facultyService.getById(orderCreateCommand.getFacultyId())))
                .getId();
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findOne(orderId);
    }

    //UnauthorizedFacultyDataException потрібен для перевірки права доступу в аспектах
    @FacultyAuthorized
    public void updateOrder(Order order) throws UnauthorizedFacultyDataException {
        orderRepository.save(order);
    }

    public File generateStudentExpelDocument(Integer studentExpelId) throws IOException, Docx4JException {
        StudentExpel studentExpel = studentExpelService.getById(studentExpelId);
        StudentExpelBusinessInformation studentExpelBusinessInformation = objectMapper.readValue(studentExpel.getOrderBusinessOperation(), StudentExpelBusinessInformation.class);
        Order order = getOrderById(studentExpelBusinessInformation.getOrderId());
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
        return documentIOService.saveDocumentToTemp(orderDocument, order.getOrderTemplateVersion().getDbTableName() + " " + order.getOrderNumber(), FileFormatEnum.DOCX);
    }

    public List<StudentExpelResponseDto> saveStudentExpel(Integer orderId, List<StudentExpelCreateCommand> studentExpelCreateCommands) throws JsonProcessingException {

        Map<Integer, OrderReason> orderReasonMap = new HashMap<>();
        orderReasonMap.put(1, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_BACHELOR));
        orderReasonMap.put(2, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_SPECIALIST));
        orderReasonMap.put(3, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_MASTER));
        List<StudentExpelResponseDto> studentExpelResponseDtos = new ArrayList<>();
        Order order = this.getOrderById(orderId);
        orderRepository.save(order);

        for (StudentExpelCreateCommand studentExpelCreateCommand : studentExpelCreateCommands) {
            StudentDegree studentDegree = studentDegreeService.getById(studentExpelCreateCommand.getStudentDegreeId());
            studentExpelResponseDtos.add(new StudentExpelResponseDto(orderId, studentExpelRepository.save(new StudentExpel().setStudentDegree(studentDegree)
                    .setStudentGroup(studentDegree.getStudentGroup())
                    .setStudyYear(currentYearService.getYear() - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears())
                    .setPayment(studentDegree.getPayment())
                    .setExpelDate(studentExpelCreateCommand.getExpelDate())
                    .setOrderNumber(order.getOrderNumber())
                    .setOrderDate(order.getOrderDate())
                    .setOrderReason(orderReasonMap.get(studentDegree.getSpecialization().getDegree().getId()))
                    .setOrderParagraphJson(objectMapper.writeValueAsString(studentExpelCreateCommand.getOrderParsedParagraphDto()))
                    .setOrderBusinessOperation(objectMapper.writeValueAsString(new StudentExpelBusinessInformation(studentExpelCreateCommand.getStudentDegreeId(),
                            orderId))))
                    .getId()));
        }
        return studentExpelResponseDtos;
    }

    public List<OrderParagraphPiece> getParsedParagraphByOrderType(String orderType) {
        String template = orderTemplateVersionRepository.findByDbTableNameAndActive(orderType, true).getParagraphTemplate();
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
            } else {
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
            }
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
