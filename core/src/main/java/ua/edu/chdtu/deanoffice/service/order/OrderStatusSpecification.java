package ua.edu.chdtu.deanoffice.service.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusSpecification implements Specification<Order> {
    private boolean signed;
    private boolean draft;
    private boolean rejected;

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        query.orderBy(builder.desc(root.get("orderDate")));
        Predicate facultyPredicate = builder.equal(root.get("faculty"), FacultyUtil.getUserFacultyIdInt());
        Predicate statusPredicate = null;
        if (signed && draft && !rejected) {
            statusPredicate = builder.equal(root.get("active"), true);
        }
        if (signed && !draft && rejected) {
            statusPredicate = builder.or(builder.equal(root.get("signed"), true), builder.equal(root.get("active"), false));
        }
        if (signed && !draft && !rejected) {
            statusPredicate = builder.equal(root.get("signed"), true);
        }
        if (!signed && draft && rejected) {
            statusPredicate = builder.equal(root.get("signed"), false);
        }
        if (!signed && draft && !rejected) {
            statusPredicate = builder.and(builder.equal(root.get("signed"), false), builder.equal(root.get("active"), true));
        }
        if (!signed && !draft && rejected) {
            statusPredicate = builder.equal(root.get("active"), false);
        }
        if (statusPredicate == null)
            return facultyPredicate;
        else
            return builder.and(facultyPredicate, statusPredicate);
    }
}
