package ua.edu.chdtu.deanoffice.api.specialization;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationView;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationWriteDTO;
import ua.edu.chdtu.deanoffice.entity.AcademicTitle;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.DataVerificationService;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;
import java.util.List;

@RestController
@RequestMapping("/specializations")
public class SpecializationController {
    private final SpecializationService specializationService;
    private final SpecialityService specialityService;
    private final DepartmentService departmentService;
    private final DegreeService degreeService;
    private final DataVerificationService verificationService;
    private final FacultyAuthorizationService facultyAuthorizationService;

    @Autowired
    public SpecializationController(
            SpecializationService specializationService,
            SpecialityService specialityService,
            DepartmentService departmentService,
            DegreeService degreeService,
            DataVerificationService verificationService,
            FacultyAuthorizationService facultyAuthorizationService
    ) {
        this.specializationService = specializationService;
        this.specialityService = specialityService;
        this.departmentService = departmentService;
        this.degreeService = degreeService;
        this.verificationService = verificationService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @GetMapping
    @JsonView(SpecializationView.Extended.class)
    public ResponseEntity getSpecializationByActive(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active,
            @RequestParam(value = "facultyId", required = false) Integer facultyId,
            @RequestParam(value = "degreeId", required = false) Integer degreeId
    ) {
        try {
            List<Specialization> specializations;
            if (facultyId == null)
                facultyId = FacultyUtil.getUserFacultyIdInt();
            if (degreeId == null) {
                specializations = specializationService.getAllByActive(active, facultyId);
            } else {
                specializations = specializationService.getAllByActiveAndDegree(active, facultyId, degreeId);
            }
            return ResponseEntity.ok(Mapper.map(specializations, SpecializationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PostMapping
    public ResponseEntity createSpecialization(
            @RequestBody SpecializationWriteDTO specializationDTO,
            @CurrentUser ApplicationUser user
    ) {
        try {
            Specialization specialization = Mapper.strictMap(specializationDTO, Specialization.class);
            Specialization specializationAfterSave = specializationService.create(specialization);
            SpecializationDTO specializationSavedDTO = Mapper.strictMap(specializationAfterSave, SpecializationDTO.class);
            return new ResponseEntity(specializationSavedDTO, HttpStatus.CREATED);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("{specialization_id}")
    public ResponseEntity getSpecializationById(@PathVariable("specialization_id") Integer specializationId) {
        try {
            Specialization specialization = specializationService.getById(specializationId);
            return ResponseEntity.ok(Mapper.map(specialization, SpecializationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PutMapping("{id}")
    public ResponseEntity updateSpecialization(@PathVariable int id, @RequestBody SpecializationWriteDTO specializationDTO) {
        try {
            if (!specializationDTO.isActive()) {
                return handleException(
                        new OperationCannotBePerformedException("Не можна змінювати неактивну освітню програму")
                );
            }
            Specialization specialization = Mapper.strictMap(specializationDTO, Specialization.class);
            specialization.setId(id);
            Specialization specializationAfterSave = specializationService.update(specialization);
            SpecializationDTO specializationSavedDTO = Mapper.strictMap(specializationAfterSave, SpecializationDTO.class);
            return new ResponseEntity(specializationSavedDTO, HttpStatus.CREATED);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PutMapping("/restore")
    public ResponseEntity restoreSpecialization(@CurrentUser ApplicationUser user, @RequestParam int specializationId){
        try {
            Specialization specialization = specializationService.getById(specializationId);
            this.verificationService.specializationNotNullAndNotActive(specialization, specializationId);
            facultyAuthorizationService.verifySpecializationAccessibility(user, specialization);
            specializationService.restore(specialization);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @DeleteMapping("/{specialization_id}")
    public ResponseEntity deleteSpecialization(@PathVariable("specialization_id") int specializationId) {
        try {
            Specialization specialization = specializationService.getById(specializationId);
            this.verificationService.specializationNotNullAndActive(specialization, specializationId);
            specializationService.delete(specialization);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SpecializationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
