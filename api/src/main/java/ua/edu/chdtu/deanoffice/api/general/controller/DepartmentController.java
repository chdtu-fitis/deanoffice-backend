package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.specialization.SpecializationController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DataVerificationService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;


import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final DataVerificationService verificationService;

    @Autowired
    public DepartmentController(DepartmentService departmentService,
                                DataVerificationService verificationService) {
        this.departmentService = departmentService;
        this.verificationService = verificationService;
    }

    @GetMapping
    public ResponseEntity getDepartments(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active,
            @CurrentUser ApplicationUser user
    ) {
        List<Department> departments = departmentService.getAllByActive(active, user.getFaculty().getId());
        return ResponseEntity.ok(map(departments, DepartmentDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDepartment(@PathVariable("id") int departmentId) {
        try {
            Department department = departmentService.getById(departmentId);
            this.verificationService.departmentInstanceNotNullAndActive(department,departmentId);
            departmentService.delete(department);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return  handleException(exception);
        }

    }



    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SpecializationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
