package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.specialization.SpecializationController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;


import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity getDepartments(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active,
            @CurrentUser ApplicationUser user
    ) {
        List<Department> departments = departmentService.getAllByActive(active, user.getFaculty().getId());
        return ResponseEntity.ok(map(departments, NamedDTO.class));
    }

    @DeleteMapping("/{department_id}")
    public ResponseEntity deleteDepartment(@PathVariable("department_id") Integer departmentId) {
        Department department = departmentService.getById(departmentId);
        try {
            if (department == null) {
                return handleException(
                        new OperationCannotBePerformedException("Кафедру [" + departmentId + "] не знайдено")
                );
            }
            if (!department.isActive()) {
                return handleException(
                        new OperationCannotBePerformedException("Кафедра ["+ departmentId +"] не активна в даний час")
                );
            }
            departmentService.delete(departmentId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return  handleException(exception);
        }

    }



    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SpecializationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
