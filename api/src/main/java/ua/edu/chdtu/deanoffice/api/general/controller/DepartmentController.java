package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentWriteDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import javax.validation.constraints.Min;
import java.util.List;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/departments")
@Validated
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity getDepartments(@RequestParam(value = "active", required = false, defaultValue = "true") boolean active) {
        List<Department> departments = departmentService.getFacultyDepartmentsByActive(active);
        return ResponseEntity.ok(map(departments, DepartmentDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity getDepartmentById(@PathVariable("id") int departmentId) {
        Department department = departmentService.getById(departmentId);
        return ResponseEntity.ok(Mapper.strictMap(department, DepartmentDTO.class));
    }

    @PostMapping
    public ResponseEntity createDepartment(@Validated @RequestBody DepartmentWriteDTO departmentDTO) throws UnauthorizedFacultyDataException {
        Department department = Mapper.strictMap(departmentDTO, Department.class);
        department.setFaculty(new Faculty(FacultyUtil.getUserFacultyIdInt()));
        department.setActive(true);
        Department departmentAfterSave = departmentService.save(department);
        DepartmentDTO departmentSavedDTO = Mapper.strictMap(departmentAfterSave, DepartmentDTO.class);
        return new ResponseEntity(departmentSavedDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateDepartment(@PathVariable("id") @Min(1) int id,
                                           @Validated @RequestBody DepartmentWriteDTO departmentDTO) throws UnauthorizedFacultyDataException {
        Department department = departmentService.getById(id);
        Mapper.strictMap(departmentDTO, department);
        Department departmentAfterSave = departmentService.save(department);
        DepartmentDTO departmentSavedDTO = Mapper.strictMap(departmentAfterSave, DepartmentDTO.class);
        return new ResponseEntity(departmentSavedDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDepartment(@PathVariable("id") @Min(1) int departmentId) throws UnauthorizedFacultyDataException {
        Department department = departmentService.getById(departmentId);
        departmentService.delete(department);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore")
    public ResponseEntity restoreDepartment(@RequestParam @Min(1) int departmentId) throws UnauthorizedFacultyDataException {
        Department department = departmentService.getById(departmentId);
        departmentService.restore(department);
        return ResponseEntity.ok().build();
    }
}
