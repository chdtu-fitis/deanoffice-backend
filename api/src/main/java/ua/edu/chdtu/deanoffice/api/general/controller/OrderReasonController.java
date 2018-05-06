package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/reasons")
public class OrderReasonController {
    private final OrderReasonService orderReasonService;

    @Autowired
    public OrderReasonController(OrderReasonService orderReasonService) {
        this.orderReasonService = orderReasonService;
    }

    @GetMapping("/fired-students")
    public ResponseEntity getFiredStudentReasons() {
        List<OrderReason> orderReasons = orderReasonService.getAllByKind("fired_students");
        return ResponseEntity.ok(map(orderReasons, NamedDTO.class));
    }

    @GetMapping("/zarah")
    public ResponseEntity getZarahReasons() {
        List<OrderReason> orderReasons = orderReasonService.getAllByKind("zarah");
        return ResponseEntity.ok(map(orderReasons, NamedDTO.class));
    }

    @GetMapping("/vidp-students")
    public ResponseEntity getVidpStudentReasons() {
        List<OrderReason> orderReasons = orderReasonService.getAllByKind("vidp_students");
        return ResponseEntity.ok(map(orderReasons, NamedDTO.class));
    }
}
