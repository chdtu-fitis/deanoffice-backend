package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AbnormalStudentsSelectiveCoursesSpecification {
    public static Specification<SelectiveCoursesStudentDegrees> getSpecification(
            int degreeId, int studyYear, int studentYear, boolean moreNorm) {
        return (Root<SelectiveCoursesStudentDegrees> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("active"), true));
            predicates.add(cb.equal(root.get("studentDegree").get("specialization").get("degree").get("id"), degreeId));
            predicates.add(cb.equal(root.get("selectiveCourse").get("studyYear"), studyYear));
            if(studentYear != 0) {
                predicates.add(cb.equal(cb.sum(cb.diff(studyYear, root.get("creationYear")), root.get("realBeginYear")), studentYear));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
