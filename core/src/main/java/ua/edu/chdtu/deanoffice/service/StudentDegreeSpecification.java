package ua.edu.chdtu.deanoffice.service;

import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDegreeSpecification {

    public static Specification<StudentDegree> getAbsentStudentDegreeInImportData(List<Integer> ids, int facultyId, int degreeId, int specialityId){
        return new Specification<StudentDegree>() {
            @Override
            public Predicate toPredicate(Root<StudentDegree> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("active"), true));
                if (ids != null && ids.size() != 0){
                    predicates.add(cb.not(root.get("id").in(ids)));
                }
                Join<StudentDegree, Specialization> specialization = root.join("specialization");
                if (degreeId != 0){
                    predicates.add(cb.equal(specialization.get("degree"), degreeId));
                }
                if (specialityId != 0){
                    predicates.add(cb.equal(specialization.get("speciality"), specialityId));
                }
                predicates.add(cb.equal(specialization.get("faculty"), facultyId));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
