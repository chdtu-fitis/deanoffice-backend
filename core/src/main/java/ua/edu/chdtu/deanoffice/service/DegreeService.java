package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;

import java.util.List;

@Service
public class DegreeService {
    private final DegreeRepository degreeRepository;

    public DegreeService(DegreeRepository degreeRepository) {
        this.degreeRepository = degreeRepository;
    }

    public List<Degree> getDegrees() {
        return degreeRepository.findAll();
    }

    public Degree getById(Integer degreeId) {
        return degreeRepository.findOne(degreeId);
    }

    public Degree getByName(String degreeName) {
        return degreeRepository.findByName(degreeName);
    }

    public Degree getByNameEng(String nameEng) {
        return degreeRepository.findByNameEng(nameEng);
    }

    public int getMaxSemesterForDegreeByNameEngAndFacultyIdAndTuitionForm(String nameEng, int facultyId, TuitionForm tuitionForm) {
        return degreeRepository.findMaxSemesterForDegreeByNameEngAndFacultyIdAndTuitionForm(nameEng, facultyId, tuitionForm.toString());
    }
}
