package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.specialization.SpecializationController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DataVerificationService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;


import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final DataVerificationService verificationService;
    private final FacultyAuthorizationService facultyAuthorizationService;

    @Autowired
    public DepartmentController(DepartmentService departmentService,
                                DataVerificationService verificationService,
                                FacultyAuthorizationService facultyAuthorizationService) {
        this.departmentService = departmentService;
        this.verificationService = verificationService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @GetMapping
    public ResponseEntity getDepartments(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active,
            @CurrentUser ApplicationUser user
    ) {
        List<Department> departments = departmentService.getAllByActive(active, user.getFaculty().getId());
        return ResponseEntity.ok(map(departments, DepartmentDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity getDepartmentById(@PathVariable("id") int departmentId,
                                            @CurrentUser ApplicationUser user) {
        try {
            Department department = departmentService.getById(departmentId);
            this.facultyAuthorizationService.verifyAccessibilityOfDepartment(user, department);
            return ResponseEntity.ok(Mapper.strictMap(department, DepartmentDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping
    public ResponseEntity createDepartment(
            @RequestBody DepartmentDTO departmentDTO,
            @CurrentUser ApplicationUser user
    ) {
        try {
            Department department = create(departmentDTO, user.getFaculty());
            department.setActive(true);
            Department departmentAfterSave = departmentService.save(department);
            DepartmentDTO departmentSavedDTO = Mapper.strictMap(departmentAfterSave, DepartmentDTO.class);
            return new ResponseEntity(departmentSavedDTO, HttpStatus.CREATED);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping
    public ResponseEntity updateDepartment(@RequestBody DepartmentDTO departmentDTO,
                                           @CurrentUser ApplicationUser user) {
        try {
            if (!departmentDTO.isActive()) {
                throw new OperationCannotBePerformedException("Не можна змінювати не активну кафедру");
            }
            Department department = Mapper.strictMap(departmentDTO, Department.class);
            this.facultyAuthorizationService.verifyAccessibilityOfDepartment(user, department);
            Department departmentAfterSave = departmentService.save(department);
            DepartmentDTO departmentSavedDTO = Mapper.strictMap(departmentAfterSave, DepartmentDTO.class);
            return new ResponseEntity(departmentSavedDTO, HttpStatus.CREATED);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDepartment(@PathVariable("id") int departmentId,
                                           @CurrentUser ApplicationUser user) {
        try {
            Department department = departmentService.getById(departmentId);
            this.verificationService.departmentNotNullAndActive(department, departmentId);
            this.facultyAuthorizationService.verifyAccessibilityOfDepartment(user, department);
            departmentService.delete(department);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }

    }

    private Department create(DepartmentDTO departmentDTO, Faculty faculty) {
        Department department = Mapper.strictMap(departmentDTO, Department.class);
        department.setFaculty(faculty);
        return department;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SpecializationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
