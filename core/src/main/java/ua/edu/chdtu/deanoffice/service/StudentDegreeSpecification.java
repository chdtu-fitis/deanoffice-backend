package ua.edu.chdtu.deanoffice.service;

import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDegreeSpecification {

    public static Specification<StudentDegree> getAbsentStudentDegreeInImportData(List<Integer> ids, int facultyId){
        return new Specification<StudentDegree>() {
            @Override
            public Predicate toPredicate(Root<StudentDegree> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("active"), true));
                if (ids != null && ids.size() != 0){
                    predicates.add(cb.not(root.get("id").in(ids)));
                }
                Join<StudentDegree, Specialization> specialization = root.join("specialization");
                Join<Specialization, Faculty> faculty = specialization.join("faculty");
                predicates.add(cb.equal(faculty.get("id"), facultyId));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
