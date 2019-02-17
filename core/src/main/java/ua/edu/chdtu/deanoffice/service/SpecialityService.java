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

    public List<Speciality> getAllActiveInFaculty(int facultyId) {
        return specialityRepository.findAllActiveInFaculty(facultyId);
    }

    public List<Speciality> getAllInFaculty(int facultyId) {
        return specialityRepository.findAllInFaculty(facultyId);
    }

    public List<Speciality> getAllActive(boolean active) {
        return specialityRepository.findAllByActiveOrderByName(active);
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

    public Speciality findSpecialityByCodeAndName(String code, String name){return specialityRepository.getSpecialityByCodeAndName(code, name);}

    public Speciality save(Speciality speciality) {
        return specialityRepository.save(speciality);
    }
}
