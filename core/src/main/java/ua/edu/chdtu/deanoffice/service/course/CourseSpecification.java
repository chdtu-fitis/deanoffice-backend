package ua.edu.chdtu.deanoffice.service.course;

import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {
    static Specification<Course> getCourseWithImportFilters(
            String courseName, Integer hours, Integer hoursPerCredit, String knowledgeControl,
            String nameStartingWith, String nameContains) {
        return (Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (courseName != null && !courseName.isEmpty())
                predicates.add(cb.equal(root.get("courseName").get("name"), courseName));
            if (hours != null)
                predicates.add(cb.equal(root.get("hours"), hours));
            if (hoursPerCredit != null)
                predicates.add(cb.equal(root.get("hoursPerCredit"), hoursPerCredit));
            if (knowledgeControl != null && !knowledgeControl.isEmpty())
                predicates.add(cb.equal(root.get("knowledgeControl"), hoursPerCredit));
            if (nameStartingWith != null && !nameStartingWith.isEmpty())
                predicates.add(cb.like(root.get("courseName").get("name"), nameStartingWith + "%"));
            if (nameContains != null && !nameContains.isEmpty())
                predicates.add(cb.like(root.get("courseName").get("name"), "%" + nameContains + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
