package ua.edu.chdtu.deanoffice.service;

import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class StudentDegreeSpecification {
    public static Specification<StudentDegree> getAbsentStudentDegreeInImportData(List<Integer> id){
        return new Specification<StudentDegree>() {
            @Override
            public Predicate toPredicate(Root<StudentDegree> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (id == null){
                    predicates.add(cb.notEqual(root.get("id"), null));
                } else {
                    predicates.add(cb.notEqual(root.get("id"), id.get(0)));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
