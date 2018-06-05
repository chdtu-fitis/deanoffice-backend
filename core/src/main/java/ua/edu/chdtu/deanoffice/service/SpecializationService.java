package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.repository.AcquiredCompetenciesRepository;
import ua.edu.chdtu.deanoffice.repository.SpecializationRepository;

import java.util.List;

@Service
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    @Autowired
    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public List<Specialization> getAllByActive(boolean active, int facultyId) {
        return specializationRepository.findAllByActive(active, facultyId);
    }

    public Specialization save(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public Specialization getById(Integer specializationId) {
        return specializationRepository.findOne(specializationId);
    }

    public Specialization getByNameAndDegreeAndSpecialityAndFaculty(String name, Integer degreeId, Integer specialityId, Integer facultyId) {
        List<Specialization> specializations = specializationRepository.findByNameAndDegreeAndSpecialityAndFaculty(name, degreeId, specialityId, facultyId);
        return specializations.size() == 0 ? null : specializations.get(0);
    }

    public void delete(Integer specializationId) {
        Specialization specialization = getById(specializationId);
        specialization.setActive(false);
        specializationRepository.save(specialization);
    }
}
