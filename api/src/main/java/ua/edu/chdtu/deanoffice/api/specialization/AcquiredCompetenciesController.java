package ua.edu.chdtu.deanoffice.api.specialization;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.specialization.dto.AcquiredCompetenciesDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationView;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.AcquiredCompetenciesService;
import ua.edu.chdtu.deanoffice.service.specialization.CompetenciesBean;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
public class AcquiredCompetenciesController {
    private final AcquiredCompetenciesService acquiredCompetenciesService;

    @Autowired
    public AcquiredCompetenciesController(AcquiredCompetenciesService acquiredCompetenciesService) {
        this.acquiredCompetenciesService = acquiredCompetenciesService;
    }

    @RequestMapping(method = RequestMethod.HEAD, path = "/specializations/{specialization_id}/competencies")
    public ResponseEntity isExist(
            @PathVariable("specialization_id") int specializationId,
            @RequestParam(value = "for-current-year", required = false, defaultValue = "false") boolean forCurrentYear
    ) {
        try {
            if (acquiredCompetenciesService.isNotExist(specializationId, forCurrentYear)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    // Копіювати всіх
    // Сформувати CompetenceBean
    // Вставити CompetenceBean в кінцевий DTO

    @GetMapping("/specializations/{specialization_id}/competencies")
//    @JsonView(SpecializationView.AcquiredCompetencies.class)
    public ResponseEntity getCompetenciesForSpecialization(@PathVariable("specialization_id") int specializationId) {
        try {
            return ResponseEntity.ok(getAcquiredCompetenciesDTO(specializationId));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private AcquiredCompetenciesDTO getAcquiredCompetenciesDTO(int specializationId) throws IOException {
        CompetenciesBean competenciesBean = acquiredCompetenciesService.getLastAcquiredCompetencies(specializationId);
        if (competenciesBean == null) {
            return new AcquiredCompetenciesDTO();
        }
        AcquiredCompetenciesDTO acquiredCompetenciesDTO = map( competenciesBean, AcquiredCompetenciesDTO.class);
        return acquiredCompetenciesDTO;
    }


    @PutMapping("/acquired-competencies/{acquired-competencies-id}/ukr")
    public ResponseEntity updateAcquiredCompetenciesUkr(
            @PathVariable("acquired-competencies-id") Integer acquiredCompetenciesId,
            @RequestBody String competencies
    ) {
        try {
            acquiredCompetenciesService.updateCompetencies(acquiredCompetenciesId, competencies);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }


//    @GetMapping("/specializations/{specialization_id}/competencies/ukr")
//    @JsonView(SpecializationView.AcquiredCompetenciesUkr.class)
//    public ResponseEntity getCompetenciesUkrForSpecialization(@PathVariable("specialization_id") int specializationId) {
//        try {
//            return ResponseEntity.ok(getAcquiredCompetenciesDTO(specializationId));
//        } catch (Exception exception) {
//            return handleException(exception);
//        }
//    }
//
//    private AcquiredCompetenciesDTO getAcquiredCompetenciesDTO(int specializationId) {
//        AcquiredCompetencies acquiredCompetencies = acquiredCompetenciesService.getLastAcquiredCompetencies(specializationId);
//        if (acquiredCompetencies == null) {
//            return new AcquiredCompetenciesDTO();
//        }
//        return (AcquiredCompetenciesDTO) map(acquiredCompetencies, AcquiredCompetenciesDTO.class);
//    }
//
//    @PutMapping("/acquired-competencies/{acquired-competencies-id}/ukr")
//    public ResponseEntity updateAcquiredCompetenciesUkr(
//            @PathVariable("acquired-competencies-id") Integer acquiredCompetenciesId,
//            @RequestBody String competencies
//    ) {
//        try {
//            acquiredCompetenciesService.updateCompetenciesUkr(acquiredCompetenciesId, competencies);
//            return ResponseEntity.ok().build();
//        } catch (Exception exception) {
//            return handleException(exception);
//        }
//    }
//
//    @GetMapping("/specializations/{specialization_id}/competencies/eng")
//    @JsonView(SpecializationView.AcquiredCompetenciesEng.class)
//    public ResponseEntity getCompetenciesEngForSpecialization(@PathVariable("specialization_id") int specializationId) {
//        try {
//            return ResponseEntity.ok(getAcquiredCompetenciesDTO(specializationId));
//        } catch (Exception exception) {
//            return handleException(exception);
//        }
//    }
//
//    @PutMapping("/acquired-competencies/{acquired-competencies-id}/eng")
//    public ResponseEntity updateAcquiredCompetenciesEng(
//            @PathVariable("acquired-competencies-id") Integer acquiredCompetenciesId,
//            @RequestBody String competencies
//    ) {
//        try {
//            acquiredCompetenciesService.updateCompetenciesEng(acquiredCompetenciesId, competencies);
//            return ResponseEntity.ok().build();
//        } catch (Exception exception) {
//            return handleException(exception);
//        }
//    }

    @PostMapping("/acquired-competencies")
    public ResponseEntity create(@RequestBody AcquiredCompetenciesDTO acquiredCompetenciesDTO) throws IOException {
        if (!acquiredCompetenciesService.isNotExist(acquiredCompetenciesDTO.getSpecializationId(), true)) {
            return handleException(new OperationCannotBePerformedException(
                    "Не можна створити більше одного набору компетентностей для одного року")
            );
        }

        try {
            AcquiredCompetencies acquiredCompetencies = (AcquiredCompetencies) map(acquiredCompetenciesDTO, AcquiredCompetencies.class);
            this.acquiredCompetenciesService.create(acquiredCompetencies);
            URI location = getNewResourceLocation(acquiredCompetencies.getId());
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, AcquiredCompetenciesController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
