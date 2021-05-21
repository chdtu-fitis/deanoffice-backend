package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SelectiveCoursesStudentDegreeSpecification {
    public static Specification<SelectiveCoursesStudentDegrees> getSelectiveCoursesStudentDegree(boolean all, int studyYear, List<Integer> studentDegreeIds) {
        return new Specification<SelectiveCoursesStudentDegrees>() {
            @Override
            public Predicate toPredicate(Root<SelectiveCoursesStudentDegrees> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!all)
                    predicates.add(cb.equal(root.get("active"), true));
                Join<SelectiveCoursesStudentDegrees, SelectiveCourse> selectiveCourse = root.join("selectiveCourse");
                predicates.add(cb.equal(selectiveCourse.get("studyYear"), studyYear));
                Join<SelectiveCoursesStudentDegrees, StudentDegree> studentDegree = root.join("studentDegree");
                Expression<Integer> studentDegreeIdExpression = studentDegree.get("id");
                predicates.add(studentDegreeIdExpression.in(studentDegreeIds));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
