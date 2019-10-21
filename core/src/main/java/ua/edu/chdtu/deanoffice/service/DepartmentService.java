package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.repository.DepartmentRepository;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllByActive(boolean active,int facultyId) {
        return departmentRepository.getAllByActive(active,facultyId);
    }

    public Department getById(Integer departmentId) {
        return this.departmentRepository.findOne(departmentId);
    }

    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    public void delete(Department department) {
        department.setActive(false);
        departmentRepository.save(department);
    }
    public void restore(Department department) {
        department.setActive(true);
        departmentRepository.save(department);
    }
}
