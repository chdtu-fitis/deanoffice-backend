package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.AcquiredCompetenciesRepository;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;
import ua.edu.chdtu.deanoffice.repository.DepartmentRepository;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.repository.SpecialityRepository;
import ua.edu.chdtu.deanoffice.repository.SpecializationRepository;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.List;

@Service
public class SpecializationService {
    private final SpecializationRepository specializationRepository;
    private final SpecialityRepository specialityRepository;
    private final DegreeRepository degreeRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public SpecializationService(SpecializationRepository specializationRepository,
                                 SpecialityRepository specialityRepository,
                                 DegreeRepository degreeRepository,
                                 DepartmentRepository departmentRepository,
                                 FacultyRepository facultyRepository,
                                 TeacherRepository teacherRepository) {
        this.specializationRepository = specializationRepository;
        this.specialityRepository = specialityRepository;
        this.degreeRepository = degreeRepository;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.teacherRepository = teacherRepository;
    }

    public List<Specialization> getAllByActive(boolean active, int facultyId) {
        return specializationRepository.findAllByActive(active, facultyId);
    }

    public List<Specialization> getAllByActiveAndDegree(boolean active, int facultyId, int degreeId) {
        return specializationRepository.findAllByActiveAndDegree(active,facultyId,degreeId);
    }

    public Specialization save(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public Specialization getById(Integer specializationId) {
        return specializationRepository.findById(specializationId).get();
    }

    public Specialization getByNameAndDegreeAndSpecialityAndFaculty(String name, Integer degreeId, Integer specialityId, Integer facultyId) {
        List<Specialization> specializations = specializationRepository.findByNameAndDegreeAndSpecialityAndFaculty(name, degreeId, specialityId, facultyId);
        return specializations.size() == 0 ? null : specializations.get(0);
    }

    public List<Specialization> getAllActiveBySpecialityAndDegree(int specialityId, int facultyId, int degreeId) {
        List<Specialization> specializations = specializationRepository.findAllBySpecialityId(specialityId, facultyId, degreeId);
        return specializations;
    }

    public Specialization getForSpecialityIfSole(int specialityId, int facultyId, int degreeId) {
        List<Specialization> specializations = specializationRepository.findAllBySpecialityId(specialityId, facultyId, degreeId);
        if (specializations.size() == 1) {
            return specializations.get(0);
        } else
            return null;
    }

    public Specialization create(Specialization specialization) {
        setDependenciesFromDBForCreate(specialization);
        specialization.setActive(true);
        return specializationRepository.save(specialization);
    }

    public Specialization update(Specialization specialization) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        Specialization specializationFromDB = specializationRepository.findById(specialization.getId()).get();
        if (specializationFromDB == null) {
            throw new OperationCannotBePerformedException("Освітньої програми з вказаним ідентифікатором не існує!");
        } else {
            setDependenciesFromDBForUpdate(specialization, specializationFromDB);
            specialization.setActive(true);
        }
        return specializationRepository.save(specialization);
    }

    private void setDependenciesFromDBForCreate(Specialization specialization) {
        specialization.setSpeciality(specialityRepository.findById(specialization.getSpeciality().getId()).get());
        specialization.setDegree(degreeRepository.findById(specialization.getDegree().getId()).get());
        specialization.setDepartment(departmentRepository.findById(specialization.getDepartment().getId()).get());
        if (specialization.getProgramHead() != null)
            specialization.setProgramHead(teacherRepository.findById(specialization.getProgramHead().getId()).get());
        specialization.setFaculty(facultyRepository.findById(FacultyUtil.getUserFacultyIdInt()).get());
    }

    private void setDependenciesFromDBForUpdate(Specialization specialization, Specialization specializationFromDB) throws  UnauthorizedFacultyDataException {
        if (FacultyUtil.getUserFacultyIdInt() == specializationFromDB.getFaculty().getId()) {
            specialization.setFaculty(specializationFromDB.getFaculty());
        } else {
            throw new UnauthorizedFacultyDataException("Зміна освітніх програм інших факультетів заборонена");
        }

        if (specialization.getSpeciality().getId() == specializationFromDB.getSpeciality().getId()) {
            specialization.setSpeciality(specializationFromDB.getSpeciality());
        } else {
            specialization.setSpeciality(specialityRepository.findById(specialization.getSpeciality().getId()).get());
        }

        if (specialization.getDegree().getId() == specializationFromDB.getDegree().getId()) {
            specialization.setDegree(specializationFromDB.getDegree());
        } else {
            specialization.setDegree(degreeRepository.findById(specialization.getDegree().getId()).get());
        }

        if (specialization.getDepartment().getId() == specializationFromDB.getDepartment().getId()) {
            specialization.setDepartment(specializationFromDB.getDepartment());
        } else {
            specialization.setDepartment(departmentRepository.findById(specialization.getDepartment().getId()).get());
        }

        if (specialization.getProgramHead() != null) {
            if (specializationFromDB.getProgramHead() != null && specialization.getProgramHead().getId() == specializationFromDB.getProgramHead().getId()) {
                specialization.setProgramHead(specializationFromDB.getProgramHead());
            } else {
                specialization.setProgramHead(teacherRepository.findById(specialization.getProgramHead().getId()).get());
            }
        }
    }

    //UnauthorizedFacultyDataException потрібен для перевірки права доступу в аспектах
    @FacultyAuthorized
    public void delete(Specialization specialization) throws UnauthorizedFacultyDataException {
        specialization.setActive(false);
        specializationRepository.save(specialization);
    }

    @FacultyAuthorized
    public void restore(Specialization specialization) throws UnauthorizedFacultyDataException {
        specialization.setActive(true);
        specializationRepository.save(specialization);
    }
}
