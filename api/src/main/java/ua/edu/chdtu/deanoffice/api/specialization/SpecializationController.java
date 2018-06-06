package ua.edu.chdtu.deanoffice.api.specialization;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import static java.util.Arrays.asList;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/specializations")
public class SpecializationController {
    private final SpecializationService specializationService;
    private final SpecialityService specialityService;
    private final DepartmentService departmentService;
    private final DegreeService degreeService;

    @Autowired
    public SpecializationController(
            SpecializationService specializationService,
            SpecialityService specialityService,
            DepartmentService departmentService,
            DegreeService degreeService
    ) {
        this.specializationService = specializationService;
        this.specialityService = specialityService;
        this.departmentService = departmentService;
        this.degreeService = degreeService;
    }

    @GetMapping
    @JsonView(SpecializationView.Extended.class)
    public ResponseEntity getSpecializationByActive(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active,
            @CurrentUser ApplicationUser user
    ) {
        List<Specialization> specializations = specializationService.getAllByActive(active, user.getFaculty().getId());
        return ResponseEntity.ok(Mapper.map(specializations, SpecializationDTO.class));
    }

    @PostMapping
    public ResponseEntity createSpecialization(
            @RequestBody SpecializationDTO specializationDTO,
            @CurrentUser ApplicationUser user
    ) {
        try {
            Specialization specialization = create(specializationDTO, user.getFaculty());
            specialization.setActive(true);
            specialization = specializationService.save(specialization);

            URI location = getNewResourceLocation(specialization.getId());

            class Id {
                private int id;
                private Id(int id) {
                    this.id = id;
                }

                public int getId() {
                    return id;
                }
            }
            return ResponseEntity.created(location).body(new Id(specialization.getId()));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Specialization create(SpecializationDTO specializationDTO, Faculty faculty) {
        Specialization specialization = (Specialization) Mapper.strictMap(specializationDTO, Specialization.class);

        Speciality speciality = this.specialityService.getById(specializationDTO.getSpecialityId());
        specialization.setSpeciality(speciality);

        if (specializationDTO.getDepartmentId() != null && specializationDTO.getDepartmentId() != 0) {
            Department department = departmentService.getById(specializationDTO.getDepartmentId());
            specialization.setDepartment(department);
        }

        Degree degree = degreeService.getById(specializationDTO.getDegreeId());
        specialization.setDegree(degree);

        specialization.setFaculty(faculty);

        return specialization;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SpecializationController.class);
    }

    @GetMapping("{specialization_id}")
    public ResponseEntity getSpecializationById(@PathVariable("specialization_id") Integer specializationId) {
        Specialization specialization = specializationService.getById(specializationId);
        return ResponseEntity.ok(Mapper.map(specialization, SpecializationDTO.class));
    }

    @PutMapping
    public ResponseEntity updateSpecialization(
            @RequestBody SpecializationDTO specializationDTO,
            @CurrentUser ApplicationUser user
    ) {
        try {
            if (!specializationDTO.isActive()) {
                throwException("You can not update inactive specialization");
            }
            Specialization specialization = create(specializationDTO, user.getFaculty());
            specializationService.save(specialization);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private void throwException(String message) throws Exception {
        throw new Exception(message);
    }

    @DeleteMapping("/{specialization_id}")
    public ResponseEntity deleteSpecialization(@PathVariable("specialization_id") Integer specializationId) {
        Specialization specialization = specializationService.getById(specializationId);
        if (specialization == null) {
            return ExceptionHandlerAdvice.handleException(
                    "Not found specialization [" + specializationId + "]",
                    SpecializationController.class,
                    HttpStatus.NOT_FOUND
            );
        }
        try {
            if (!specialization.isActive()) {
                throwException("Specialization [id = " + specializationId + "] already inactive");
            }
            specializationService.delete(specializationId);
            return ResponseEntity.noContent().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }
}
