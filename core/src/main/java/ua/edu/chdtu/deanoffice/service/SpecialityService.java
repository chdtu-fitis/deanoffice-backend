package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public List<Speciality> getSpecialityByActive(boolean onlyActive) {
        if (onlyActive) {
            return specialityRepository.findAllByActiveOrderByName(true);
        }
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        return specialityRepository.findAll(sort);
    }

    public Speciality getSpecialityByName(String name) {
        return specialityRepository.getSpecialityByName(name);
    }

    public Speciality getSpecialityByCode(String code) {
        return specialityRepository.getSpecialityByCode(code);
    }
}
