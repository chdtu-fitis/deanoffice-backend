package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.repository.SpecialityRepository;

import java.util.List;

@Service
public class SpecialityService {
    private final SpecialityRepository specialityRepository;

    @Autowired
    public SpecialityService(SpecialityRepository specialityRepository) {
        this.specialityRepository = specialityRepository;
    }

    public List<Speciality> getAllActive(int facultyId) {
        return specialityRepository.findAllActive(facultyId);
    }

    public List<Speciality> getAll(int facultyId) {
        return specialityRepository.findAll(facultyId);
    }

    public Speciality getById(Integer specialityId) {
        return specialityRepository.findOne(specialityId);
    }

    public Speciality getSpecialityByName(String name) {
        return specialityRepository.getSpecialityByName(name);
    }

    public Speciality getSpecialityByCode(String code) {
        return specialityRepository.getSpecialityByCode(code);
    }

    public Speciality save(Speciality speciality) {
        return specialityRepository.save(speciality);
    }
}
